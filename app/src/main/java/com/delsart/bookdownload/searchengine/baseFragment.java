package com.delsart.bookdownload.searchengine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.delsart.bookdownload.R;
import com.delsart.bookdownload.listandadapter.mListAdapter;
import com.delsart.bookdownload.listandadapter.mlist;

import java.util.ArrayList;

/**
 * Created by Delsart on 2017/7/25.
 */


public class baseFragment extends Fragment {
    public RecyclerView recyclerView = null;
    private mListAdapter adapter;
    ArrayList<mlist> list = new ArrayList<>();
    String loadmore = "";
    ProgressDialog waitingDialog;

    String clickdurl;

    boolean iffail = false;
    boolean ifseadching = false;
    View nosearchview;
    View nofoundview;
    View searching;
    int ii = 0;


    BaseQuickAdapter.RequestLoadMoreListener lml = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            try {
                if (getifnextpage()) {
                    getpage(getloadmore());
                } else
                    adapter.loadMoreEnd();
            } catch (Exception e) {
                e.printStackTrace();
                adapter.loadMoreFail();
            }

        }
    };

    public boolean getifnextpage() {
        return loadmore.length() > 5;
    }

    public String getloadmore() {
        return loadmore;
    }


    public void totop() {
        recyclerView.smoothScrollToPosition(0);
    }

    public void clean() {
        adapter.setNewData(null);
        list.clear();
        iffail = false;
        ifseadching = false;
        ii = 0;
    }

    public baseFragment() {

        adapter = new mListAdapter();
        adapter.addData(list);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击事件
                try {
                    waitingDialog.setTitle("下载");
                    waitingDialog.setMessage("获取中...");
                    waitingDialog.setIndeterminate(true);
                    waitingDialog.setCancelable(false);
                    waitingDialog.show();
                    clickdurl = list.get(position).getdurl();
                    downloadclick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }




    Handler showlist = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.addData((mlist) msg.obj);
            list.add((mlist) msg.obj);
            adapter.loadMoreComplete();
        }

    };

    Handler failload = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.loadMoreFail();
        }

    };
    Handler failpage = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.setEmptyView(nofoundview);
        }

    };
    Handler searchingpage = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.setEmptyView(searching);
        }

    };

    public void getpage(final String url) throws Exception {
         /*
         这里需要子类来覆写
          */
    }

    public void setsearchingpage() {
        if (recyclerView != null) {
            ifseadching = true;
            Message message = searchingpage.obtainMessage();
            message.sendToTarget();
        }
    }

    public void ifnopage() {
        if (ii == 0 && recyclerView != null) {
            iffail = true;
            Message message = failpage.obtainMessage();
            message.sendToTarget();
        }
    }

    public String getClickdurl() {
        return clickdurl;
    }

    public void downloadclick() throws Exception {
        try {
            Message message = showdownloadh.obtainMessage();
            message.obj = clickdurl;
            message.sendToTarget();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showdownload(String string) {
        Message message = showdownloadh.obtainMessage();
        message.obj = string;
        message.sendToTarget();
    }

     Handler showdownloadh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            waitingDialog.cancel();
            try {
                Uri uri = Uri.parse(msg.obj.toString());
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.list, container, false);

        nosearchview = getLayoutInflater(savedInstanceState).inflate(R.layout.nosearch, container, false);
        nofoundview = getLayoutInflater(savedInstanceState).inflate(R.layout.nofound, container, false);
        searching = getLayoutInflater(savedInstanceState).inflate(R.layout.searching, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        waitingDialog = new ProgressDialog(this.getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        adapter = new mListAdapter();
        adapter.addData(list);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击事件
                clickdurl = list.get(position).getdurl();
                builder.setTitle("选择操作");
                builder.setMessage(list.get(position).getname());
                builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        waitingDialog.setTitle("下载");
                        waitingDialog.setMessage("获取中...");
                        waitingDialog.setIndeterminate(true);
                        waitingDialog.setCancelable(false);
                        waitingDialog.show();
                        try {
                            downloadclick();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        recyclerView.setAdapter(adapter);
        if (adapter.getEmptyView() != null) {
            ViewGroup pare = (ViewGroup) adapter.getEmptyView().getParent();
            if (pare != null)
                pare.removeView(adapter.getEmptyView());
        }
        if (ifseadching)
            adapter.setEmptyView(searching);
        if (iffail)
            adapter.setEmptyView(nofoundview);
        else if (!ifseadching)
            adapter.setEmptyView(nosearchview);

        adapter.setOnLoadMoreListener(lml, recyclerView);


        return view;
    }


}
