package com.delsart.bookdownload.searchengine;


import android.os.Message;
import android.util.Log;

import com.delsart.bookdownload.listandadapter.mlist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Delsart on 2017/7/22.
 */

public class zhoudu extends baseFragment {
public zhoudu(){
    super();
    Log.d("a", "zhoudu: ");

}
    public void get(String url) throws Exception {
        clean();
        getpage(url);
    }

    public void getpage(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setsearchingpage();
                    Document doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").timeout(10000).get();
                    //获得下页
                    loadmore = "";
                    if (doc.select("a:contains(下一页>>)").attr("href").length()>5)
                    loadmore ="http://www.ireadweek.com"+ doc.select("a:contains(下一页>>)").attr("href");
                    //获得数据
                    Elements elements = doc.select("a[href^=/index.php]:has(li)");
                    for (int i = 5; i < elements.size(); i++) {
                        //统计数目
                        ii++;
                        //
                        String name = "《" + elements.get(i).select("div.hanghang-list-name").text() + "》" + "作者：" + elements.get(i).select("div.hanghang-list-zuozhe").text();
                        getnext(elements.get(i).select("a").attr("href"), name, elements.get(i));
                    }
                    ifnopage();
                    //判断为空
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = failload.obtainMessage();
                    message.sendToTarget();
                }

            }
        }).start();
    }
    //分级加载
    public void getnext(final String url, final String name, final Element element) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc2 = Jsoup.connect("http://www.ireadweek.com" + url).timeout(10000).userAgent("Mozilla").get();
                    Elements element3 = doc2.select("div.hanghang-shu-content-font");
                    String t = element3.select("p").text();
                    String time = t.substring(t.indexOf("分类："), t.indexOf("豆瓣评分："));
                    String info = t.substring(t.indexOf("简介："), t.length());
                    String durl = doc2.select("a[href].downloads").attr("href");
                    String pic ="http://www.ireadweek.com" +doc2.select("div.hanghang-shu-content-img").select("img").attr("src");
                    Message message = showlist.obtainMessage();
                    message.obj = new mlist(name, time, info, durl,pic);
                    message.sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
