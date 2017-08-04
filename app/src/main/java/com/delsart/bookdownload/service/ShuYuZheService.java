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
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ShuYuZheService extends BaseService {
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<NovelBean> list = new ArrayList<>();
    private static String TAG = "test";

    public ShuYuZheService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.SHUYUZHE + keywords + "/";
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
                            .select("body > div.container > div > table > tbody > tr");
                    latch = new CountDownLatch(select.size() - 1);
                    for (int i = 1; i < select.size(); i++) {
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
                Document document = null;
                try {
                    document = Jsoup.connect(element.select("td:nth-child(2) > a").attr("abs:href"))
                            .ignoreContentType(true)
                            .get();

                Elements elements = document.select("div.hero-unit");
                String t = elements.text();
                String name = t.substring(0, t.indexOf("上一页")).replace("Book.ShuYuZhe.com书语者_", "");
                String status = "文件格式：" + name.substring(name.lastIndexOf("."), name.length());
                name = name.substring(0, name.lastIndexOf("."));
                String time = t.substring(t.indexOf("发布日期"), t.indexOf("资源介绍"));
                String info = t.substring(t.indexOf("资源介绍"), t.length());
                String category = t.substring(t.indexOf("文件标签"), t.indexOf("发布日期"));
                String author = "";
                String words = t.substring(t.indexOf("资源大小"), t.indexOf("下载积分"));
                String url = document.select("a:contains(下载此书)").attr("href");
                String pic = "";
                list.add(new NovelBean(name, time, info, category, status, author, words, pic, url));
                latch.countDown();
                }catch (SocketException e) {
                    e.printStackTrace();
                  latch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                urls.add(new DownloadBean("全文下载", url));
                latch.countDown();
            }
        }).start();
        latch.await();
        return urls;
    }

}
