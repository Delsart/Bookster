package com.delsart.bookdownload.searchengine;


import android.os.Message;

import com.delsart.bookdownload.listandadapter.mlist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Delsart on 2017/7/22.
 */

public class zhixuan extends baseFragment {


    public zhixuan( ) {
        super();
    }

    public void get(String url) throws Exception {
        super.get(url);
    }


    @Override
    public void getpage(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setsearchingpage();
                Document doc=null;
                try {
                    doc = Jsoup.connect(url).data("query", "Java").timeout(10000).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = failload.obtainMessage();
                    message.sendToTarget();
                }
                if (doc!=null) {
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
                        try {
                            getnext(elements.get(i).select("a[href]").attr("href"), name, time, info);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ifnopage();
                    Message message = showlist.obtainMessage();
                    message.sendToTarget();

                }
            }
        }).start();
    }

    public void getnext(final String url,final String name, final String time,final String info) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document download = Jsoup.connect(url).data("query", "Java").get();
                    String durl = download.select("div.down_2").select("a[href]").attr("href");
                    String pic=download.select("img[title=点击查看原图]").attr("src");
                    Message message = addlist.obtainMessage();
                    message.obj = new mlist(name, time, info, durl,pic);
                    message.sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void downloadclick() throws Exception {
        getdownload(clickdurl);
    }

    public void getdownload(final String url) throws Exception {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //获得下载地址
                    Document download = Jsoup.connect(url).data("query", "Java").get();
                    String durl = download.select("span.downfile").select("a[href]").attr("href");
                    showdownload(durl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

}
