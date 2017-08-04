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

public class ZhiXuanService extends BaseService {
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<NovelBean> list = new ArrayList<>();
    private static String TAG = "test";

    public ZhiXuanService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.ZHIXUAN + keywords + "&page=";
    }

    @Override
    public void get() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<NovelBean> list2 = new ArrayList<>();
                    list2.addAll(list);
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
                    if ( list2.size() > 0 ) {
                        if (list2.size() > 0 && list2.get(0).getShowText().equals(list.get(0).getShowText()))
                            list.clear();
                    }
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
                String url = element.select("a").attr("abs:href");
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
                String t=document.select("#m > div.postcont > p:nth-child(4)").text();
                String name = document.select("#m > div.posttitle").text().replace("（校对版全本）", "").replace("《","").replace("》","");
                String time = "收录日期："+element.select("span").text();
                String info =  t.substring(t.indexOf("【内容简介"),t.length()).replace("　　", "\n").replace("      ","\n").replace("【内容简介】： \n","");
                String category= document.select("#m > div.postcont > div.pagefujian > div.filecont > p.fileinfo > span:nth-child(1)").text();
                String status = "";
                String author = name.substring(name.indexOf("作者"),name.length());
               name= name.replace(author,"");
                String words = t.substring(0,t.indexOf("【内容简介")).replace("【","").replace("】","");
                String pic =document.select("img[title=点击查看原图]").attr("abs:src");
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
                String u1 = document.select("#m > div.postcont > div.pagefujian > div.filecont > p.filetit > a").attr("abs:href");
                String u1n = document.select("#m > div.postcont > div.pagefujian > div.filecont > p.filetit > a").text();
                urls.add(new DownloadBean(u1n, u1));
                latch.countDown();
            }
        }).start();
        latch.await();
        return urls;
    }

}
