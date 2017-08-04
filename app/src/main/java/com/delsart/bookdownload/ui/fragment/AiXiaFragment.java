package com.delsart.bookdownload.ui.fragment;


import android.os.Handler;

import com.delsart.bookdownload.service.AiXiaService;
import com.delsart.bookdownload.service.BaseService;

public class AiXiaFragment extends BaseFragment {

    @Override
    protected BaseService getService(Handler handler, String keywords) {
        return new AiXiaService(handler,keywords);
    }
}
