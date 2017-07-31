package com.delsart.bookdownload.searchengine;


import android.os.Message;
import android.util.Log;

import com.delsart.bookdownload.listandadapter.mlist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Delsart on 2017/7/22.
 */

public class aixia extends baseFragment {
    byte page = 1;
    String urls="http://m.ixdzs.com";
    public aixia() {
        super();
    }

    public void get(String url) throws Exception {
        super.get(url);

    }

    @Override
    public void clean() {
        super.clean();
        page = 1;
    }

    public void getpage(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                setsearchingpage();
                try {
                    doc = Jsoup.connect(url).timeout(10000).get();
                } catch (Exception e) {
                    Message message = failload.obtainMessage();
                    message.sendToTarget();
                    e.printStackTrace();
                }
                if (doc != null) {
                    //获得下一页数据
                    loadmore = "";
                    loadmore = urls + doc.select("a:containsOwn(下一页)").attr("href");
                    //分析得到数据
                    Elements elements = doc.select("div.list").select("a");
                    for (int i = 0; i < elements.size(); i++) {
                        //统计数目
                        ii++;

                        //
                        String durl = urls + elements.get(i).attr("href");
                        Log.d("test", "run: " + durl);
                        try {
                            getnext(durl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ifnopage();
                    page++;
                    Message message = showlist.obtainMessage();
                    message.sendToTarget();
                } else {
                    Message message = failload.obtainMessage();
                    message.sendToTarget();
                }
            }
        }).start();
    }

    @Override
    public void downloadclick() throws Exception {
        showdownload(lis);
    }

    String[] lis = new String[2];

    //分级加载
    public void getnext(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc2 = Jsoup.connect(url).get();
                    String t1 = doc2.select("div.list").get(0).text();
                    String name = doc2.select("div.line").get(0).text();
                    String time = t1.replace(" ", "\n").replace("<a href=\"http://www.d9cn.com/u", "");
                    String info = doc2.select("div.intro").text();
                    String durl =doc2.select("div.list").get(1).select("a").get(1).attr("href");
                    String durl2 =doc2.select("div.list").get(1).select("a").get(2).attr("href");
                    lis[0] =urls+ durl;
                    lis[1] = urls+durl2;
                    String pic = "";
                    Message message = addlist.obtainMessage();
                    message.obj = new mlist(name, time, info, durl, pic);
                    message.sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }).start();
    }


}
