package com.delsart.bookdownload.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.delsart.bookdownload.MsgType;
import com.delsart.bookdownload.Url;
import com.delsart.bookdownload.bean.DownloadBean;
import com.delsart.bookdownload.bean.NovelBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DongManZhiJiaService extends BaseService {
    private final Handler mHandler;
    private int mPage;
    private String mBaseUrl;
    private CountDownLatch latch;
    private ArrayList<NovelBean> list = new ArrayList<>();
    final String MAINURL = "http://q.dmzj.com";

    public DongManZhiJiaService(Handler handler, String keywords) {
        super(handler, keywords);
        this.mHandler = handler;
        mPage = 1;
        mBaseUrl = Url.DONGMANZHIJIA + keywords;
    }

    @Override
    public void get() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.clear();
                    String select = Jsoup.connect(mBaseUrl)
                            .timeout(10000)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .userAgent(Url.MOBBILE_AGENT)
                            .get().toString();
                    if (select.contains("[")) {
                        select = select.substring(select.indexOf("["), select.length());
                        JSONArray jsonArray = new JSONArray(select);
                        latch = new CountDownLatch(jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            runInSameTime(jsonArray.getJSONObject(i));
                        }
                        latch.await();
                        Message msg = mHandler.obtainMessage();
                        msg.what = MsgType.SUCCESS;
                        msg.obj = list;
                        mHandler.sendMessage(msg);
                    }
                    else
                    {
                        Message msg = mHandler.obtainMessage();
                        msg.what = MsgType.ERROR;
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = mHandler.obtainMessage();
                    msg.what = MsgType.ERROR;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runInSameTime(final JSONObject jsonObject) throws IOException {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
        try {
            String name = jsonObject.getString("full_name");
            String time = "";
            String info = jsonObject.getString("description");
            String category = "类型：" + jsonObject.getString("types");
            String status = "状态：" + jsonObject.getString("status");
            String author = "作者：" + jsonObject.getString("author");
            String words = "最新章节：" + jsonObject.getString("last_chapter_name");
            String pic = jsonObject.getString("image_url");
            String url = MAINURL + jsonObject.getString("lnovel_url").replace("..", "");
            NovelBean no = new NovelBean(name, time, info, category, status, author, words, pic, url);
            list.add(no);
        } catch (JSONException e) {
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
                            .getElementsByTag("script");
                    Matcher m = Pattern.compile("<div class=\"chapnamesub\">[^a-z]+</div>.+<a target=\"_blank\" href=\".+\">[^a-z]+</a>").matcher(elements.toString());
                    while (m.find()) {
                        String name="";
                        String path="";
                        Matcher matcher = Pattern.compile("<div class=\"chapnamesub\">([^a-z]+)</div>").matcher(m.group());
                        if (matcher.find()) {
                             name = matcher.group(1);
                        }
                        matcher = Pattern.compile("<a target=\"_blank\" href=\"(.+)\">[^a-z]+</a>").matcher(m.group());
                        if (matcher.find()) {
                             path = matcher.group(1);

                        }
                        urls.add(new DownloadBean(name, path));
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
