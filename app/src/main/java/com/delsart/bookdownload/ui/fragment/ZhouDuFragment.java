package com.delsart.bookdownload.ui.fragment;


import android.os.Handler;

import com.delsart.bookdownload.service.BaseService;
import com.delsart.bookdownload.service.M360DService;
import com.delsart.bookdownload.service.ZhouDuService;

public class ZhouDuFragment extends BaseFragment {

    @Override
    protected BaseService getService(Handler handler, String keywords) {
        return new ZhouDuService(handler,keywords);
    }
}
