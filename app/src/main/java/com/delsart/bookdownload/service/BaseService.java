package com.delsart.bookdownload.service;


import android.os.Handler;

import com.delsart.bookdownload.bean.DownloadBean;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseService {
    public ExecutorService mExecutorService= Executors.newFixedThreadPool(4);
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

    public void reLoad(){
        get();
    }
    public abstract void get();

    public abstract ArrayList<DownloadBean> getDownloadurls(String url) throws InterruptedException;
}
