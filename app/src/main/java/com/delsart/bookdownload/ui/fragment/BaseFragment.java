package com.delsart.bookdownload.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.delsart.bookdownload.MsgType;
import com.delsart.bookdownload.R;
import com.delsart.bookdownload.adapter.MyItemAdapter;
import com.delsart.bookdownload.bean.DownloadBean;
import com.delsart.bookdownload.bean.NovelBean;
import com.delsart.bookdownload.handler.MyHandler;
import com.delsart.bookdownload.handler.OnHandleMessageCallback;
import com.delsart.bookdownload.service.BaseService;

import java.util.ArrayList;

public abstract class BaseFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MyItemAdapter mAdapter;
    private BaseService mService;
    private ArrayList<NovelBean> mList = new ArrayList<>();
    private ProgressDialog waitingDialog;
    private View mNoFoundView;
private View mSearchingView;
    protected abstract BaseService getService(Handler handler, String keywords);

    private final MyHandler<BaseFragment> mHandler = new MyHandler<>(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mNoSearchView = inflater.inflate(R.layout.view_no_search, null, false);
        mNoFoundView = inflater.inflate(R.layout.view_no_found, null, false);
        mSearchingView = inflater.inflate(R.layout.view_searching, null, false);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.recycler_view, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new MyItemAdapter();
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        mAdapter.setEmptyView(mNoSearchView);
        mRecyclerView.setAdapter(mAdapter);
        initLoadMore();
        setOnClickEvent();
        return view;
    }

    private void initLoadMore() {
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mService.get();
            }
        });
    }

    private void setOnClickEvent() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("查看");
                View contentview = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog, null);
                LinearLayout linearLayout = (LinearLayout) contentview.findViewById(R.id.droot);
                TextView name = (TextView) linearLayout.getChildAt(0);
                ImageView pic = (ImageView) linearLayout.getChildAt(1);
                TextView info = (TextView) linearLayout.getChildAt(2);
                name.setText(mList.get(position).getName());
                info.setText(mList.get(position).getShowText());
                Glide.with(getContext()).load(mList.get(position).getPic()).into(pic);
                builder.setView(contentview);
                builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        waitingDialog = new ProgressDialog(getContext());
                        waitingDialog.setTitle("下载");
                        waitingDialog.setMessage("获取中...");
                        waitingDialog.setIndeterminate(true);
                        waitingDialog.setCancelable(false);
                        waitingDialog.show();
                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        doOnClickDownload(mService.getDownloadurls(mList.get(position).getDownload_from_url()));
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }}).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
    }


    public void doOnClickDownload(final ArrayList<DownloadBean> urls) {
        String[] sList = new String[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            sList[i] = urls.get(i).getType();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Looper.prepare();
        builder.setTitle("选择下载对象");
        builder.setItems(sList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              String url=  urls.get(which).getUrl();
                if (!url.equals("")) {
                    Uri uri = Uri.parse(url);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
        waitingDialog.dismiss();
        Looper.loop();
    }


    public void startSearch(String keywords) {
        mList.clear();
        mAdapter.setNewData(null);
        mService = getService(mHandler, keywords);
        mAdapter.setEmptyView(mSearchingView);
        runService();
    }


    private void runService() {
        mService.get();
        mHandler.setOnHandleMessageCallback(new OnHandleMessageCallback<BaseFragment>() {
            @Override
            public void handleMessage(BaseFragment fragment, Message msg) {
                switch (msg.what) {
                    case MsgType.ERROR:
                        mAdapter.loadMoreFail();
                        mAdapter.setEmptyView(mNoFoundView);
                        break;
                    case MsgType.SUCCESS:
                        ArrayList<NovelBean> data = (ArrayList<NovelBean>) msg.obj;
                        if (data != null) {
                            mList.addAll(data);
                            if (data.size() > 0) {
                                mAdapter.addData(data);
                                mAdapter.loadMoreComplete();
                            } else {
                                mAdapter.setEmptyView(mNoFoundView);
                                mAdapter.loadMoreEnd();
                            }
                        } else {
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "未知错误", Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }

    public void toTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }
}
