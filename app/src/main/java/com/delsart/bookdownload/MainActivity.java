package com.delsart.bookdownload;


import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.delsart.bookdownload.about.aboutActivity;
import com.delsart.bookdownload.listandadapter.mpageAdapter;
import com.delsart.bookdownload.searchengine.m360d;
import com.delsart.bookdownload.searchengine.shuyuzhe;
import com.delsart.bookdownload.searchengine.xiaoshuwu;
import com.delsart.bookdownload.searchengine.qishu;
import com.delsart.bookdownload.searchengine.zhixuan;
import com.delsart.bookdownload.searchengine.zhoudu;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private com.delsart.bookdownload.searchengine.zhixuan zhixuan;
    private com.delsart.bookdownload.searchengine.zhoudu zhoudu;
    private shuyuzhe shuyuzhe;
    private m360d m360d;
    private xiaoshuwu xiaoshuwu;
    private qishu qishu;


    SharedPreferences firstime;
    SharedPreferences.Editor editor;
    mpageAdapter pageadapter = null;
    ViewPager viewPager;
    TabLayout tabLayout;
    SearchView searchView;
    SharedPreferences autoupdate;


    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            } catch (Exception e) {

            }
        }
        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MIUISetStatusBarLightMode(this, true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initview();
        firstime = getSharedPreferences("data", MODE_PRIVATE);
        editor = firstime.edit();
        if (firstime.getInt("first", 0) == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("使用须知");
            builder.setCancelable(false);
            builder.setMessage(R.string.firstlaunchshowtext);
            builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    editor.putInt("first", 1);
                    editor.apply();
                }
            });
            builder.show();
        }

        if (firstime.getInt("showrate", 1) > 3 && firstime.getBoolean("ifshowrate", true)) {
            editor.putInt("showrate", 1);
            editor.apply();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("嗨，还好吗？");
            builder.setMessage(R.string.showrate);
            builder.setNegativeButton("评分", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            builder.setPositiveButton("捐赠", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText("15874082586");
                    Snackbar.make(findViewById(R.id.main_content), "支付宝账号已复制", Snackbar.LENGTH_SHORT).show();

                }
            });
            builder.setNeutralButton("拒绝，并不再提醒", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.putBoolean("ifshowrate", false);
                    editor.apply();
                }
            });
            builder.show();
        } else {

            editor.putInt("showrate", firstime.getInt("showrate", 1) + 1);
            editor.apply();
        }

        autoupdate = getSharedPreferences("com.delsart.bookdownload_preferences", MODE_PRIVATE);
        if (autoupdate.getBoolean("autoUpdate", true)) {

            try {
                getupdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void getupdate() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://hereacg.org/bookster/update.json");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);

                    InputStream in = connection.getInputStream();
                    // 下面对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parseJSONWithJSONObject(response.toString());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonDatas) {
        final String jsonData = jsonDatas;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject jsonObject = new JSONObject(jsonData);

                    String version = jsonObject.getString("version");
                    String info = jsonObject.getString("info");
                    String time = jsonObject.getString("time");
                    String size = jsonObject.getString("size");
                    final String url = jsonObject.getString("url");
                    PackageManager manager;

                    PackageInfo infos = null;
                    manager = getPackageManager();
                    try {
                        infos = manager.getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    Log.d("sss", "run: " + version + gettrueversion(version) + "ssssss" + gettrueversion(infos.versionName));
                    if (gettrueversion(version) > gettrueversion(infos.versionName)) {
                        showupdate(version, time, size, info, url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public float gettrueversion(String s) {
        String s2 = "";
        for (char a : s.toCharArray()) {

            if ("0123456789.".indexOf(a) >= 0) {
                s2 = s2 + a;

            }
        }

        return Float.parseFloat(s2);
    }

    public void showupdate(final String version, final String time, final String size, final String info, final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("有新版本");
                builder.setMessage("版本号：" + version + "\n更新时间：" + time + "\nApk大小：" + size + "MB\n更新日志：" + info);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse(url);
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                });
                builder.setNeutralButton("关闭自动检查", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = autoupdate.edit();
                        editor.putBoolean("autoUpdate", false);
                        editor.apply();
                    }
                });
                builder.show();
            }
        });
    }

    private void initview() {
        getWindow().setBackgroundDrawable(null);

        zhixuan = new zhixuan();
        zhoudu = new zhoudu();
        shuyuzhe = new shuyuzhe();
        m360d = new m360d();
        xiaoshuwu = new xiaoshuwu();
qishu=new qishu();


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        pageadapter = new mpageAdapter(getSupportFragmentManager());
        pageadapter.addFragment(zhixuan, "知轩藏书");
        pageadapter.addFragment(zhoudu, "周读");
        pageadapter.addFragment(m360d, "360℃");
        pageadapter.addFragment(xiaoshuwu, "我的小书屋");
        pageadapter.addFragment(shuyuzhe, "书语者");
        pageadapter.addFragment(qishu, "奇书");


        viewPager.setAdapter(pageadapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.findFocus();
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        zhixuan.totop();
                        break;
                    case 1:
                        zhoudu.totop();
                        break;
                    case 2:
                        m360d.totop();
                        break;
                    case 3:
                        xiaoshuwu.totop();
                        break;
                    case 4:
                        shuyuzhe.totop();
                        break;
                    case 5:
                        qishu.totop();
                        break;
                }
            }
        });
        viewPager.setLayerType(View.LAYER_TYPE_NONE,null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            startActivity(new Intent(this, aboutActivity.class));
            return true;
        }
        if (id == R.id.setting) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        //Toolbar的搜索框
        final MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("搜索书籍或者作者");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //处理搜索结果
                try {
                    viewPager.setLayerType(View.LAYER_TYPE_HARDWARE,null);
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                    getSupportActionBar().setTitle("搜索：" + query);
                    zhixuan.get("http://www.zxcs8.com/?keyword=" + toUtf8(query));
                    zhoudu.get("http://www.ireadweek.com/index.php/Index/bookList.html?keyword=" + toUtf8(query));
                    m360d.get("http://www.360dxs.com/list.html?keyword=" + toUtf8(query));
                    shuyuzhe.get("https://book.shuyuzhe.com/search/" + toUtf8(query));
                    xiaoshuwu.get("http://mebook.cc/?s=" + toUtf8(query));
                    qishu.get("http://zhannei.baidu.com/cse/search?s=2672242722776283010&q="+toUtf8(query));

                    viewPager.setLayerType(View.LAYER_TYPE_NONE,null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
