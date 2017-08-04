package com.delsart.bookdownload.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

public class BlahService extends BaseService {
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<NovelBean> list = new ArrayList<>();
    private static String TAG = "test";

    public BlahService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.BLAH + keywords + "&page=";
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
                            .select("div.ok-book-item").select("a.okShowInfoModal");
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
                Log.d(TAG, "run: "+url);
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
                String name = document.select("#okBookShow > div.ok-book-base-info > div.row > div.col-sm-8.ok-book-info > div.ok-book-meta > h1").text();
                String time = "";
                String info = "";
                Elements elements = document.select("#okBookShow > div.ok-book-base-info > div.row > div.col-sm-8.ok-book-info > div.ok-book-meta > div.ok-book-desc > div.ok-book-meta-content").select("p");
                for (int i = 0; i < elements.size(); i++) {


                    if (!elements.get(i).text().equals(""))
                        info = info + elements.get(i).text() + "\n\n";


                }
                String category = document.select("#okBookShow > div.ok-book-base-info > div.row > div.col-sm-8.ok-book-info > div.ok-book-meta > div.ok-book-subjects").text();
                String status ="";
                String author =document.select("#okBookShow > div.ok-book-base-info > div.row > div.col-sm-8.ok-book-info > div.ok-book-meta > div.row > div > div").text();
                String words = "";
                String pic =document.select("#okBookShow > div.ok-book-base-info > div.row > div.col-sm-4 > div > img").attr("abs:src");
                NovelBean no = new NovelBean(name, time, info, category, status, author, words, pic, url);
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

                for (Element element : document.select("#okBookShow > div.ok-book-base-info > div.row > div.col-sm-8.ok-book-info > div.ok-book-opt > div > div.col-md-5.ok-book-download > div").select("a")) {
                    urls.add(new DownloadBean(element.text(), element.attr("abs:href")));
                }


                latch.countDown();
            }
        }).start();
        latch.await();
        return urls;
    }

}
