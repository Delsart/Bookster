package com.delsart.bookdownload.searchengine;


import android.os.Message;

import com.delsart.bookdownload.listandadapter.mlist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Delsart on 2017/7/22.
 */

public class shuyuzhe extends baseFragment {


    public shuyuzhe() {
        super();
    }

    public void get(String url) throws Exception {
        super.get(url);

    }


    public void getpage(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setsearchingpage();

                    //获得知轩藏书数据
                    Document doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").timeout(10000).get();
                    //获得下一页数据
                    loadmore = "";
                    loadmore = doc.select("a[href]:contains(下一页)").attr("href");
                    //分析得到数据
                    Elements elements = doc.select("a[href][title][target]");
                    for (int i = 0; i < elements.size(); i++) {
                        //统计数目
                        ii++;
                        //
                        String t = elements.get(i).attr("title").replace("Book.ShuYuZhe.com书语者_", "");
                        String name;
                        if (t.contains("-")) {
                            name = "《" + t.substring(0, t.indexOf("-")) + "》" + "作者：" + t.substring(t.indexOf("-") + 1, t.indexOf("."));
                        } else {
                            name = "《" + t.substring(0, t.indexOf(".")) + "》";
                        }
                        getnext(elements.get(i).attr("href"), name, t.substring(t.indexOf("."), t.length()));
                    }
                    ifnopage();
                    Message message = showlist.obtainMessage();
                    message.sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = failload.obtainMessage();
                    message.sendToTarget();
                }

            }
        }).start();
    }

    //分级加载
    public void getnext(final String url, final String name, final String form) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc2 = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").get();
                    Elements element3 = doc2.select("div.hero-unit:has(br)");
                    String t2 = element3.text();
                    String time = t2.substring(t2.indexOf("发布日期："), t2.indexOf("资源介绍："));
                    String info = "格式：" + form + "\n" + t2.substring(t2.indexOf("文件标签："), t2.indexOf("发布日期：") - 2) + "\n" + t2.substring(t2.indexOf("资源介绍："), t2.length());
                    String durl = element3.select("div.common_content_main").select("a:contains(下载此书)").attr("href");
                    Message message = addlist.obtainMessage();
                    message.obj = new mlist(name, time, info, durl,"");
                    message.sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


}
