package com.delsart.bookdownload.handler;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.delsart.bookdownload.MyApplication;
import com.delsart.bookdownload.ui.activity.MainActivity;

/**
 * Created by Delsart on 2017/8/7.
 */

public class MyCrashHandler implements Thread.UncaughtExceptionHandler {

    private static MyCrashHandler instance;
    Thread.UncaughtExceptionHandler mDefaultHandler;
    public static MyCrashHandler getInstance() {
        if (instance == null) {
            instance = new MyCrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
         mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
       Looper.prepare();
        StringBuffer sb=new StringBuffer();
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            sb.append(stackTraceElement.toString());
            sb.append("\nfile:  ");
            sb.append(stackTraceElement.getFileName());
            sb.append("\nclass:   ");
            sb.append(stackTraceElement.getClassName());
            sb.append("\nmethod:   ");
            sb.append(stackTraceElement.getMethodName());
            sb.append("\nline:   ");
            sb.append(stackTraceElement.getLineNumber());
            sb.append("\n—————————————————\n");
        }
        Toast.makeText(MyApplication.getContext(),"Oops!出现错误!\n已将错误日志复制到粘贴板，请向作者反馈\n\n"+sb,Toast.LENGTH_LONG).show();
        Toast.makeText(MyApplication.getContext(),"Oops!出现错误!\n已将错误日志复制到粘贴板，请向作者反馈\n\n"+sb,Toast.LENGTH_LONG).show();
        ClipboardManager cmb = (ClipboardManager)MyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(sb);

        //android.os.Process.killProcess(android.os.Process.myPid());
        //System.exit(0);
        Looper.loop();



    }
}
