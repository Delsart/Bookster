package com.delsart.bookdownload.ui.fragment;


import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.delsart.bookdownload.MsgType;
import com.delsart.bookdownload.bean.NovelBean;
import com.delsart.bookdownload.handler.OnHandleMessageCallback;
import com.delsart.bookdownload.service.BaseService;
import com.delsart.bookdownload.service.DongManZhiJiaService;
import com.delsart.bookdownload.service.ZhouDuService;

import java.util.ArrayList;

public class DongManZhiJiaFragment extends BaseFragment {

    @Override
    protected BaseService getService(Handler handler, String keywords) {
        return new DongManZhiJiaService(handler, keywords);
    }

    @Override
    public void runService() {
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
                                mAdapter.loadMoreEnd();
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
}
