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

public class zhixuan extends baseFragment {
    public zhixuan() {
        super();
        Log.d("a", "zhixuan: ");

    }

    public void get(String url) throws Exception {
        clean();
        getpage(url);
    }


    @Override
    public void getpage(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setsearchingpage();

                    Document doc = Jsoup.connect(url).data("query", "Java").timeout(10000).get();
                    //获得下一页数据
                    loadmore = "";
                    loadmore = doc.select("a.down_pagenavi").attr("href");
                    //分析得到数据
                    Elements elements = doc.select("div.info");
                    for (int i = 0; i < elements.size(); i++) {
                        //统计数目
                        ii++;
                        //
                        String name = elements.get(i).select("a[href]").text().replace("（校对版全本）", "");
                        String time = "收录日期：" + elements.get(i).select("span").text();
                        String info = elements.get(i).select("p").text().replace("　", "\n").replace("   ", "\n").replace("\n\n", "\n").replace("\n", "\n\n");
                        String durl = elements.get(i).select("a[href]").attr("href");
                        Message message = showlist.obtainMessage();
                        message.obj = new mlist(name, time, info, durl);
                        message.sendToTarget();
                    }
                    ifnopage();
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = failload.obtainMessage();
                    message.sendToTarget();
                }

            }
        }).start();
    }

    @Override
    public void downloadclick() throws Exception {
        getdownload(getClickdurl());
    }

    public void getdownload(final String url) throws Exception {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //获得下载地址
                    Document download = Jsoup.connect(url).data("query", "Java").get();
                    download = Jsoup.connect(download.select("div.down_2").select("a[href]").attr("href")).get();
                    String durl = download.select("span.downfile").select("a[href]").attr("href");
                    showdownload(durl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

}
