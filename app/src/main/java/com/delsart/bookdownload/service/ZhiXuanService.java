package com.delsart.bookdownload.service;

import android.os.Handler;
import android.os.Message;

import com.delsart.bookdownload.MsgType;
import com.delsart.bookdownload.Url;
import com.delsart.bookdownload.bean.DownloadBean;
import com.delsart.bookdownload.bean.NovelBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZhiXuanService extends BaseService {
    private static String TAG = "test";
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<NovelBean> list = new ArrayList<>();

    public ZhiXuanService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.ZHIXUAN + keywords + "&page=";
    }
String lasts="";
    @Override
    public void get() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.clear();
                    Elements select = Jsoup.connect(mBaseUrl + mPage)
                            .timeout(10000)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.MOBBILE_AGENT)
                            .get()
                            .select("div.info");
                    latch = new CountDownLatch(select.size());
                    for (int i = 0; i < select.size(); i++) {
                        runInSameTime(select.get(i));
                    }
                    latch.await();
                    if (select.toString().equals(lasts))
                        list.clear();
                    lasts=select.toString();
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
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                String url = element.select("a").attr("abs:href");
                Document document = null;
                try {
                    document = Jsoup.connect(url)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.MOBBILE_AGENT)
                            .get();

                    String t = document.select("#m > div.postcont > p:nth-child(4)").text();
                    String name = document.select("#m > div.posttitle").text().replace("（校对版全本）", "").replace("《", "").replace("》", "");
                    String time = "收录日期：" + element.select("span").text();
                    String info = "";
                    Matcher m = Pattern.compile("【内容简介】.+").matcher(t);
                    if (m.find())
                        info = m.group(0).replace("　　", "\n").replace("      ", "\n").replace("【内容简介】： \n", "");
                    String category = document.select("#m > div.postcont > div.pagefujian > div.filecont > p.fileinfo > span:nth-child(1)").text();
                    String status = "";
                    String author = "";
                    if (name.contains("作者")) {
                        author = name.substring(name.indexOf("作者"), name.length());
                        name = name.replace(author, "");
                    }
                    String words = "";
                    if (t.contains("【内容简介"))
                        words = t.substring(0, t.indexOf("【内容简介")).replace("【", "").replace("】", "");
                    String pic = document.select("img[title=点击查看原图]").attr("abs:src");
                    String durl=document.select("#m > div.postcont > div.pagefujian > div.filecont > p.filetit > a").attr("abs:href");
                    NovelBean no = new NovelBean(name, time, info, category, status, author, words, pic, durl);
                    list.add(no);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
        });
    }


    @Override
    public ArrayList<DownloadBean> getDownloadurls(final String url) throws InterruptedException {
        latch = new CountDownLatch(1);
        final ArrayList<DownloadBean> urls = new ArrayList<>();
        mExecutorService.execute(new Runnable() {
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
                    Elements elements=document.select("body > div.wrap > div.content > div:nth-child(4) > div.panel-body a");
                    for (Element element : elements) {
                        urls.add(new DownloadBean(element.text(), element.attr("abs:href")));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
        });
        latch.await();
        return urls;
    }

}
