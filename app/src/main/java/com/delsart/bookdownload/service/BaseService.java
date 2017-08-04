package com.delsart.bookdownload.service;


import android.os.Handler;

import com.delsart.bookdownload.bean.DownloadBean;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public abstract class BaseService {
    public abstract void get();
    public abstract ArrayList<DownloadBean> getDownloadurls(String url) throws InterruptedException;

    public BaseService(Handler handler, String keywords) {
    }
    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
