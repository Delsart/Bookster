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

public class ZhouDuService extends BaseService {
    private static String TAG = "test";
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<NovelBean> list = new ArrayList<>();

    public ZhouDuService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.ZHOUDU + keywords + "&page=";
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
                            .get()
                            .select("body > div > div > ul > a");
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
                try {
                    Document document = Jsoup.connect(element.attr("abs:href"))
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .get();

                    String t = document.select("body > div > div > div.hanghang-za > div.hanghang-shu-content > div.hanghang-shu-content-font").text();
                    if (t.length() < 5) {
                        latch.countDown();
                        return;
                    }
                    String name = document.select("body > div > div > div.hanghang-za > div:nth-child(1)").text();
                    String status = "";
                    String time = "";
                    String info = t.substring(t.indexOf("简介"), t.length());
                    String category = document.select("body > div > div > div.hanghang-za > div.hanghang-shu-content > div.hanghang-shu-content-font > p:nth-child(2)").text();
                    String author = document.select("body > div > div > div.hanghang-za > div.hanghang-shu-content > div.hanghang-shu-content-font > p:nth-child(1)").text();
                    String words = "";
                    String url = document.select("body > div > div > div.hanghang-za > div.hanghang-box > div.hanghang-shu-content-btn > a").attr("href");
                    String pic = document.select("body > div > div > div.hanghang-za > div.hanghang-shu-content > div.hanghang-shu-content-img > img").attr("abs:src");
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
                urls.add(new DownloadBean("百度云", url));
                latch.countDown();
            }
        });
        latch.await();
        return urls;
    }

}
