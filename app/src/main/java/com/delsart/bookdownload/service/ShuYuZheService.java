package com.delsart.bookdownload.service;

import android.icu.text.LocaleDisplayNames;
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
import java.util.jar.Attributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShuYuZheService extends BaseService {
    private static String TAG = "test";
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<NovelBean> list = new ArrayList<>();

    public ShuYuZheService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.SHUYUZHE + keywords;
    }

    @Override
    public void get() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.clear();
                    Log.d(TAG, "run: "+mBaseUrl + "/"+mPage);
                    Elements select = Jsoup.connect(mBaseUrl + "/"+mPage)
                            .timeout(10000)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.PC_AGENT)
                            .get()
                            .select("body > div.container > div > div > div.panel.panel-success > div.panel-body > table > tbody > tr");
                        latch = new CountDownLatch(select.size() - 1);
                        for (int i = 1; i < select.size(); i++) {
                            runInSameTime(select.get(i));
                        }

                        Log.d(TAG, "run: "+select.size());
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
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(element.select("td:nth-child(1) > a").attr("abs:href"))
                            .ignoreContentType(true)
                            .userAgent(Url.PC_AGENT)
                            .get();
                    Elements elements = document.select("ul.list-group");
                    String name = elements.select("li").get(2).text().replace("文件名称：Book.ShuYuZhe.com书语者_","");
                    String status =  elements.select("li").get(4).text();

                    String time =  elements.select("li").get(6).text();

                    String info = "";

                    String category = elements.select("li").get(5).text();



                    String author = "";

                    String words =  elements.select("li").get(3).text();


                    String url = document.select("a:contains(下载此书)").attr("href");
                    String pic = "";
                    list.add(new NovelBean(name, time, info, category, status, author, words, pic, url));
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
                urls.add(new DownloadBean("全文下载", url));
                latch.countDown();
            }
        });
        latch.await();
        return urls;
    }

}
