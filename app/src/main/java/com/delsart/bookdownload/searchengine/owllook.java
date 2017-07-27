package com.delsart.bookdownload.searchengine;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.delsart.bookdownload.R;
import com.delsart.bookdownload.listandadapter.mListAdapter;
import com.delsart.bookdownload.listandadapter.mlist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Delsart on 2017/7/22.
 */

public class owllook extends Fragment {
    public static RecyclerView recyclerView = null;
    private mListAdapter adapter;
    ArrayList<mlist> list = new ArrayList<>();


    public void clean() {
        adapter.setNewData(null);
        list.clear();
    }

    Handler showlist = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            adapter.addData((mlist) msg.obj);
            list.add((mlist) msg.obj);

        }

    };


    public void getowllook(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获得知轩藏书数据
                    Document doc = Jsoup.connect(url).get();
                    Elements elements2 = doc.select("a.down_pagenavi");
                    getowllook(elements2.attr("href"));
                    Elements elements = doc.select("div.info");
                    for (int i = 0; i < elements.size(); i++) {
                        String name = elements.get(i).select("a[href]").text().replace("（校对版全本）", "");
                        String time = "收录日期：" + elements.get(i).select("span").text();
                        String info = elements.get(i).select("p").text().replace("　", "\n").replace("   ", "\n").replace("\n\n", "\n").replace("\n", "\n\n");
                        String durl = elements.get(i).select("a[href]").attr("href");
                        Message message = showlist.obtainMessage();
                        message.obj = new mlist(name, time, info, durl,"");
                        message.sendToTarget();
                    }
                    //迭代来加载下一页

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    public static float dip2px(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    private Handler showdownload = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Uri uri = Uri.parse(msg.obj.toString());
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };


    public void getdownload(final String url) throws Exception {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //获得下载地址
                    String daima = "";
                    Document download = Jsoup.connect(url).get();
                    download = Jsoup.connect(download.select("div.down_2").select("a[href]").attr("href")).get();
                    String durl = download.select("span.downfile").select("a[href]").attr("href");
                    Message message = showdownload.obtainMessage();
                    message.obj = durl;
                    message.sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new mListAdapter();
        adapter.setNewData(null);
        list.clear();
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击事件
                try {
                    getdownload(list.get(position).getdurl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        if (adapter.getEmptyView() != null) {
            ViewGroup pare = (ViewGroup) adapter.getEmptyView().getParent();
            if (pare != null)
                pare.removeView(adapter.getEmptyView());
        }

        View notDataView = getLayoutInflater(savedInstanceState).inflate(R.layout.nosearch, (ViewGroup) recyclerView.getParent(), false);
        adapter.setEmptyView(notDataView);


        return view;
    }


}
