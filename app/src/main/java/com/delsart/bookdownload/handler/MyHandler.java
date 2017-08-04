package com.delsart.bookdownload.handler;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;


public class MyHandler<T> extends Handler {

    private final WeakReference<T> mReference;

    public MyHandler(T t) {
        mReference = new WeakReference<>(t);
    }

    private OnHandleMessageCallback<T> onHandleMessageCallback;

    public void setOnHandleMessageCallback(OnHandleMessageCallback<T> onHandleMessageCallback) {
        this.onHandleMessageCallback = onHandleMessageCallback;
    }

    @Override
    public void handleMessage(Message msg) {
        T t = mReference.get();
        if (t != null && onHandleMessageCallback != null) {
            onHandleMessageCallback.handleMessage(t, msg);
        }
    }
}
