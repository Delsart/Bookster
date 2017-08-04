package com.delsart.bookdownload.ui.fragment;


import android.os.Handler;

import com.delsart.bookdownload.service.BaseService;
import com.delsart.bookdownload.service.ShuYuZheService;
import com.delsart.bookdownload.service.XiaoShuWuService;

public class XiaoShuWuFragment extends BaseFragment {

    @Override
    protected BaseService getService(Handler handler, String keywords) {
        return new XiaoShuWuService(handler,keywords);
    }
}
