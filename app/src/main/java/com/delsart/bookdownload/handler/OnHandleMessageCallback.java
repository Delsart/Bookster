package com.delsart.bookdownload.handler;


import android.os.Message;

public interface OnHandleMessageCallback<T> {
    void handleMessage(T t, Message msg);
}
