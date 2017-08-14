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

public class M360DService extends BaseService {
    private static String TAG = "test";
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<DownloadBean> urls = new ArrayList<>();
    private ArrayList<NovelBean> list = new ArrayList<>();

    public M360DService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.M360D + keywords + "&page=";
    }

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
                            .select("div[itemtype=http://schema.org/Novel].am-thumbnail");
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
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                String name = element.select("a[itemprop=name]").text();
                String durl = element.select("a[itemprop=name]").attr("abs:href");
                String time = "最后更新：" + element.select("span[itemprop=dateModified]").text();
                String info = element.select("div[itemprop=description]").text();
                String category = "分类：" + element.select("a[itemprop=genre]").text();
                String status = "状态：" + element.select("span[itemprop=updataStatus]").text();
                String author = "作者：" + element.select("a[itemprop=author]").text();
                String words = "";
                String pic = "http://www.360dxs.com/static/books/logo/" + durl.substring(durl.indexOf("_") + 1, durl.indexOf(".html")) + "s.jpg";
                NovelBean no = new NovelBean(name, time, info, category, status, author, words, pic, durl);
                list.add(no);
                latch.countDown();
            }
        });

    }

    @Override
    public ArrayList<DownloadBean> getDownloadurls(final String url) throws InterruptedException {
        latch = new CountDownLatch(1);
        final ArrayList<DownloadBean> urls = new ArrayList<>();
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(url)
                            .timeout(10000)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.MOBBILE_AGENT)
                            .get();
                    Document document2 = Jsoup.connect(document.select("a:contains(全文下载)").attr("abs:href")).get();
                    Elements elements = document2.select("div.am-u-sm-12").get(1).select("a");
                    for (Element element : elements) {
                        urls.add(new DownloadBean("全文下载： " + element.text(), element.attr("abs:href")));
                    }
                    document = Jsoup.connect(document.select("a:contains(分卷下载)").attr("abs:href")).get();
                    Elements elements1 = document.select("div.am-u-sm-12").get(1).select("h3.am-text-center");
                    Elements elements2 = document.select("div.am-u-sm-12").get(1).select("ul");
                    for (int i = 0; i < elements2.size(); i++) {
                        Elements elements3 = elements2.get(i).select("a");
                        for (Element element : elements3) {
                            urls.add(new DownloadBean(elements1.get(i).text() + "：" + element.text(), element.attr("abs:href")));
                        }
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
