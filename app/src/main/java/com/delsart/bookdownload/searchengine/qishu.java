package com.delsart.bookdownload.searchengine;


import android.os.Message;

import com.delsart.bookdownload.listandadapter.mlist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Delsart on 2017/7/22.
 */

public class qishu extends baseFragment {
    String url;
    byte page = 1;

    public qishu() {
        super();
    }

    public void get(String url) throws Exception {
        clean();
        getpage(url);
        this.url = url;
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
                try {
                    setsearchingpage();

                    Document doc = Jsoup.connect(url).timeout(10000).get();

                    //获得下一页数据
                    loadmore = "";
                    loadmore = url.replace("q=a", "q=a&p=" + ++page);
                    //分析得到数据
                    Elements elements = doc.select("a[target=_self]");

                    for (int i = 0; i < elements.size(); i++) {

                        //统计数目
                        ii++;
                        //
                        String durl = elements.get(i).attr("href");
                        if (durl.contains(".html"))
                        getnext(durl.substring(durl.indexOf("http:"), durl.indexOf(".html") + 5));
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
    public void getnext(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc2 = Jsoup.connect(url).get();
                    Elements element3 = doc2.select("div.show");
                    String t1 =element3.select("div.detail_right").select("ul").text();
                    String name = element3.select("div.detail_right").select("h1").text()+t1.substring(t1.indexOf("书籍作者："),t1.indexOf("书籍等级"));
                    String time = t1.substring(t1.indexOf("书籍语言："),t1.indexOf("下载次数"))+"\n"+t1.substring(t1.indexOf("文件大小："),t1.indexOf("书籍类型"))+"\n"+t1.substring(t1.indexOf("书籍类型："),t1.indexOf("发布日期"))+"\n"+t1.substring(t1.indexOf("发布日期："),t1.indexOf("连载状态"))+"\n"+t1.substring(t1.indexOf("连载状态："),t1.indexOf("书籍作者"));
                    String info = element3.select("div.showInfo").text();
                    String durl = doc2.select("a:containsOwn(RAR格式下载)").attr("href");
                    String pic = "http://www.qisuu.com"+element3.select("div.showBox").select("img").attr("src");
                    Message message = addlist.obtainMessage();
                    message.obj = new mlist(name, time, info, durl, pic);
                    message.sendToTarget();
                } catch (Exception e){

                }

            }
        }).start();
    }


}
