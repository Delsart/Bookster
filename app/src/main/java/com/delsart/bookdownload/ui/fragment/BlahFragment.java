package com.delsart.bookdownload.ui.fragment;


import android.os.Handler;

import com.delsart.bookdownload.service.BaseService;
import com.delsart.bookdownload.service.BlahService;

public class BlahFragment extends BaseFragment {

    @Override
    protected BaseService getService(Handler handler, String keywords) {
        return new BlahService(handler,keywords);
    }
}
