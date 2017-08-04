package com.delsart.bookdownload.ui.fragment;


import android.os.Handler;

import com.delsart.bookdownload.service.AiXiaService;
import com.delsart.bookdownload.service.BaseService;
import com.delsart.bookdownload.service.M360DService;

public class M360DFragment extends BaseFragment {

    @Override
    protected BaseService getService(Handler handler, String keywords) {
        return new M360DService(handler,keywords);
    }
}
