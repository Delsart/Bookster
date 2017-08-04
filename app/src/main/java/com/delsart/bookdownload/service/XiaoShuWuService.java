package com.delsart.bookdownload.service;

import android.graphics.pdf.PdfDocument;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.delsart.bookdownload.MsgType;
import com.delsart.bookdownload.Url;
import com.delsart.bookdownload.adapter.PagerAdapter;
import com.delsart.bookdownload.bean.DownloadBean;
import com.delsart.bookdownload.bean.NovelBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class XiaoShuWuService extends BaseService {
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<NovelBean> list = new ArrayList<>();
    private static String TAG = "test";

    public XiaoShuWuService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.XIAOSHUWU + keywords;
    }

    @Override
    public void get() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.clear();
                    Elements select = Jsoup.connect(mBaseUrl.replace("0", mPage + ""))
                            .timeout(10000)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.MOBBILE_AGENT)
                            .get()
                            .select("div#posts-list").select("a");
                    latch = new CountDownLatch(select.size());
                    for (int i = 0; i < select.size(); i++) {
                        runInSameTime(select.get(i));
                    }
                    latch.await();
                    mPage++;
                    Message msg = mHandler.obtainMessage();
                    msg.what = MsgType.SUCCESS;
                    msg.obj = list;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = mHandler.obtainMessage();
                    msg.what = MsgType.ERROR;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runInSameTime(final Element element) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = element.attr("abs:href");
                Document document = null;
                try {
                    document = Jsoup.connect(url)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.MOBBILE_AGENT)
                            .get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (document == null) {
                    latch.countDown();
                    return;
                }
                String name = document.select("#post-title").text();
                String time = document.select("#container > div > div.post_content > div > p:nth-child(5)").text().replace("作者信息", "\n作者信息");
                String info = "";
                Elements elements = document.select("#container > div > div.post_content > p");
                for (int i = 0; i < elements.size(); i++) {


                                if (!elements.get(i).text().equals(""))
                                    info = info + elements.get(i).text() + "\n\n";


                }

                String category = document.select("#container > div > div.post_detail > ul:nth-child(1) > li:nth-child(1)").text() + "\n" + document.select("#container > div > div.post_detail > ul:nth-child(1) > li:nth-child(2)").text();

                String status = "";
                String author = "";
                if (name.contains("）")) {
                    status = "文件格式：" + name.substring(name.lastIndexOf("）") + 1, name.length());
                    if (name.contains("》")) {
                        if (name.lastIndexOf("（") > name.indexOf("》"))
                            author = "作者：" + name.substring(name.lastIndexOf("》") + 1, name.lastIndexOf("（"));
                        name = name.substring(1, name.indexOf("》"));
                    } else
                        name = name.replace("《", "").replace("》", "");
                } else {
                    name = name.replace("《", "").replace("》", "");
                }
                String words = "";
                String pic = document.select("#container > div > div.post_content > p:nth-child(1) > img").attr("abs:src");
                String durl = document.select("#container > div > div.post_content > div > p.downlink > strong > a").attr("abs:href");
                NovelBean no = new NovelBean(name, time, info, category, status, author, words, pic, durl);
                list.add(no);
                latch.countDown();
            }
        }).start();
    }


    @Override
    public ArrayList<DownloadBean> getDownloadurls(final String url) throws InterruptedException {
        latch = new CountDownLatch(1);
        final ArrayList<DownloadBean> urls = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document document = null;
                try {
                    document = Jsoup.connect(url)
                            .timeout(10000)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.MOBBILE_AGENT)
                            .get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String u1 = "";
                String u1n = document.select("body > div:nth-child(4) > p:nth-child(7)").text();
                Elements elements = document.select("body > div.list").select("a");
                urls.add(new DownloadBean(u1n, u1));
                for (Element element : elements) {
                    urls.add(new DownloadBean(element.text(), element.attr("abs:href")));
                }


                latch.countDown();
            }
        }).start();
        latch.await();
        return urls;
    }

}
