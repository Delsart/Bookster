package com.delsart.bookdownload.searchengine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.delsart.bookdownload.R;
import com.delsart.bookdownload.listandadapter.mListAdapter;
import com.delsart.bookdownload.listandadapter.mlist;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Delsart on 2017/7/25.
 */


public class baseFragment extends Fragment {
    public RecyclerView recyclerView = null;
    private mListAdapter adapter;
    ArrayList<mlist> list = new ArrayList<>();
    String loadmore = "";
    ProgressDialog waitingDialog;
String url;
    String clickdurl;

    boolean delayload=false;
    boolean iffail = false;
    boolean ifseadching = false;
    View nosearchview;
    View nofoundview;
    View searching;
    View view=null;
    int ii = 0;
    ImageView pic;

    BaseQuickAdapter.RequestLoadMoreListener lml = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            try {
                if (getifnextpage()) {
                    getpage(getloadmore());
                } else
                    adapter.loadMoreEnd();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

public void get(String s) throws Exception {
    url=s;
    clean();
    if (view!=null)
        getpage(s);
    else
        delayload=true;
}

    public void totop() {
        recyclerView.smoothScrollToPosition(0);
    }

    public void clean() {
        adapter.setNewData(null);
        list.clear();
        iffail = false;
        ifseadching = false;
        ii = 0;
    }

    public baseFragment() {
        adapter = new mListAdapter();
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.setOnLoadMoreListener(lml, recyclerView);
    }




    Handler addlist = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.addData((mlist) msg.obj);
            list.add((mlist) msg.obj);
        }

    };
    Handler showlist = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            adapter.loadMoreComplete();
        }

    };

    Handler failload = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            adapter.loadMoreFail();
        }

    };
    Handler failpage = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            adapter.setEmptyView(nofoundview);
        }

    };
    Handler searchingpage = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            adapter.setEmptyView(searching);
        }

    };
    Handler showdownloadh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            waitingDialog.cancel();
            try {
                Uri uri = Uri.parse(msg.obj.toString());
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } };


    public void getpage(final String url) throws Exception {
         /*
         这里需要子类来覆写
          */
    }

    public void setsearchingpage() {
        if (recyclerView != null) {
            ifseadching = true;
            Message message = searchingpage.obtainMessage();
            message.sendToTarget();
        }
    }

    public void ifnopage() {
        if (ii == 0 && recyclerView != null) {
            iffail = true;
            Message message = failpage.obtainMessage();
            message.sendToTarget();
        }
    }


    public void downloadclick() throws Exception {
        try {
            Message message = showdownloadh.obtainMessage();
            message.obj = clickdurl;
            message.sendToTarget();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showdownload(String string) {
        Message message = showdownloadh.obtainMessage();
        message.obj = string;
        message.sendToTarget();
    }
    public void showdownload(String[] alist) {
        Message message = showdownloadpro.obtainMessage();
        message.obj = alist;
        message.sendToTarget();
    }
    Handler showdownloadpro = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            waitingDialog.cancel();
            try {
                AlertDialog.Builder singleChoiceDialog =
                        new AlertDialog.Builder(getActivity());
                final String[] a= (String[]) msg.obj;
                singleChoiceDialog.setTitle("选择下载对象");
                singleChoiceDialog.setItems( a,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse(a[which]);
                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                            }
                        });
                   singleChoiceDialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } };

    public boolean getifnextpage() {
        return loadmore.length() > 5;
    }

    public String getloadmore() {
        return loadmore;
    }
    Handler showpic = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pic.setImageBitmap((Bitmap)msg.obj);
        }

    };
    public void showpic(final String picurl) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("ssss", "run: "+picurl);
                    HttpURLConnection conn=(HttpURLConnection)new URL(picurl).openConnection();
                    conn.setConnectTimeout(6000);
                    conn.setDoInput(true);
                    InputStream is = conn.getInputStream();

                    Bitmap bitmap =BitmapFactory.decodeStream(is);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postScale(2f, 2f);
                    Message message = showpic.obtainMessage();
                    message.obj = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                            matrix, true);
                    message.sendToTarget();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }



    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.list, null, false);
        Log.d("sssss", "onCreateView: ");
        nosearchview =inflater.inflate(R.layout.nosearch, null, false);
        nofoundview = inflater.inflate(R.layout.nofound, null, false);
        searching =inflater.inflate(R.layout.searching, null, false);

        waitingDialog = new ProgressDialog(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayerType(View.LAYER_TYPE_HARDWARE,null);

        if (ifseadching)
            adapter.setEmptyView(searching);
        if (iffail)
            adapter.setEmptyView(nofoundview);
        else if (!ifseadching)
            adapter.setEmptyView(nosearchview);


        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击事件
                clickdurl = list.get(position).getdurl();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("查看");
                View contentview = inflater.inflate(R.layout.mdialog, null);
                LinearLayout linearLayout=(LinearLayout) contentview.findViewById(R.id.droot);
                pic =(ImageView)linearLayout.getChildAt(1);
                TextView name =(TextView)linearLayout.getChildAt(0);
                TextView time=  (TextView)linearLayout.getChildAt(2);
                TextView info= (TextView)linearLayout.getChildAt(3);
                name.setText(list.get(position).getname());
                time.setText(list.get(position).gettime());
                info.setText(list.get(position).getinfo().replace("\n\n","\n"));
                try {
                    showpic(list.get(position).getpic());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                builder.setView(contentview);
                builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        waitingDialog.setTitle("下载");
                        waitingDialog.setMessage("获取中...");
                        waitingDialog.setIndeterminate(true);
                        waitingDialog.setCancelable(false);
                        waitingDialog.show();
                        try {
                            downloadclick();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        if (delayload){
            try {
                getpage(url);
            } catch (Exception e) {
                e.printStackTrace();
            }}
        return view;
    }


}
