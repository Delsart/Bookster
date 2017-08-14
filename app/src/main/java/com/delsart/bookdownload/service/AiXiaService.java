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

public class AiXiaService extends BaseService {
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<NovelBean> list = new ArrayList<>();

    public AiXiaService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.AIXIA + keywords + "&page=";
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
                            .select("body > div.list > li > a");
                    latch = new CountDownLatch(select.size());
                    for (Element element : select) {
                        runInSameTime(element);
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
                String url = element.attr("abs:href");
                try {
                    Document document = Jsoup.connect(url)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.MOBBILE_AGENT)
                            .get();
                    String name = document.select("body > div:nth-child(2) > h1").text();
                    String time = document.select("body > div:nth-child(3) > li:nth-child(6)").text();
                    String info = document.select("body > div.intro > li").text();
                    String category = document.select("body > div:nth-child(3) > li:nth-child(2)").text();
                    String status = document.select("body > div:nth-child(3) > li:nth-child(5)").text();
                    String author = document.select("body > div:nth-child(3) > li:nth-child(1)").text();
                    String words = document.select("body > div:nth-child(3) > li:nth-child(3)").text();
                    String pic = "";
                    NovelBean no = new NovelBean(name, time, info, category, status, author, words, pic, url);
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
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Elements elements = Jsoup.connect(url)
                            .timeout(10000)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.MOBBILE_AGENT)
                            .get()
                            .select("body > div:nth-child(5)");
                    String u1 = elements.select("li:nth-child(2) > a").attr("abs:href");
                    String u1n = elements.select("li:nth-child(2) > a").text();
                    String u2 = elements.select("li:nth-child(3) > a").attr("abs:href");
                    String u2n = elements.select("li:nth-child(3) > a").text();
                    urls.add(new DownloadBean(u1n, u1));
                    urls.add(new DownloadBean(u2n, u2));
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
