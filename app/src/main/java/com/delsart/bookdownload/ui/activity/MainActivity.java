package com.delsart.bookdownload.ui.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.delsart.bookdownload.MyApplication;
import com.delsart.bookdownload.R;
import com.delsart.bookdownload.adapter.PagerAdapter;
import com.delsart.bookdownload.ui.fragment.AiXiaFragment;
import com.delsart.bookdownload.ui.fragment.BaseFragment;
import com.delsart.bookdownload.ui.fragment.BlahFragment;
import com.delsart.bookdownload.ui.fragment.DongManZhiJiaFragment;
import com.delsart.bookdownload.ui.fragment.M360DFragment;
import com.delsart.bookdownload.ui.fragment.QiShuFragment;
import com.delsart.bookdownload.ui.fragment.ShuYuZheFragment;
import com.delsart.bookdownload.ui.fragment.XiaoShuWuFragment;
import com.delsart.bookdownload.ui.fragment.ZhiXuanFragment;
import com.delsart.bookdownload.ui.fragment.ZhouDuFragment;
import com.delsart.bookdownload.utils.StatusBarUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences firstime;
    private SharedPreferences.Editor editor;
    private PagerAdapter mPagerAdapter;
    private SearchView searchView;
    private SharedPreferences autoupdate;
    String lastKeyWords = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.DayTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoupdate = getSharedPreferences("com.delsart.bookdownload_preferences", MODE_PRIVATE);
        StatusBarUtils.MIUISetStatusBarLightMode(this, true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        showInfoDialog();


        if (autoupdate.getBoolean("autoUpdate", true)) {

//            try {
//                getUpdate();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    private void showInfoDialog() {
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
                    if (AlipayZeroSdk.hasInstalledAlipayClient(MyApplication.getContext()))
                        AlipayZeroSdk.startAlipayClient(MainActivity.this, "a6x02835mi3wh18ivz0mbdb");
                    else
                        Toast.makeText(getApplicationContext(), "没有安装支付宝", Toast.LENGTH_SHORT).show();

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
    }

    private void getUpdate() throws Exception {
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
                    PackageManager manager = getPackageManager();
                    PackageInfo infos = manager.getPackageInfo(getPackageName(), 0);
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

    List<BaseFragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();

    private void initView() {
        getWindow().setBackgroundDrawable(null);
        addpage(new AiXiaFragment(), "爱下");
        addpage(new ZhiXuanFragment(), "知轩藏书");
        addpage(new ZhouDuFragment(), "周读");
        addpage(new ShuYuZheFragment(), "书语者");
        addpage(new DongManZhiJiaFragment(), "动漫之家");
        addpage(new M360DFragment(), "360℃");
        addpage(new XiaoShuWuFragment(), "我的小书屋");
        addpage(new QiShuFragment(), "奇书");
        addpage(new BlahFragment(), "blah");

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.findFocus();
                mPagerAdapter.setTop(tabLayout.getSelectedTabPosition());
            }
        });

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false)) {
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.DarkColor));
            tabLayout.setTabTextColors(tabLayout.getTabTextColors().getDefaultColor(), getResources().getColor(R.color.DarkColor));
        }
    }

    private void addpage(BaseFragment fragment, String s) {

        if (autoupdate.getBoolean(s, true)) {
            fragments.add(fragment);
            titles.add(s);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.Theme:
                SharedPreferences.Editor meditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
                    meditor.putBoolean("dark_theme", false);
                else
                    meditor.putBoolean("dark_theme", true);
                meditor.apply();
                Intent intent = getIntent();

                startActivity(intent);
                this.overridePendingTransition(R.anim.enter_anim,0);
                finish();

                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            menu.findItem(R.id.Theme).setTitle("今夜白");
        final MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("搜索书籍或者作者");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery(lastKeyWords, false);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //处理搜索结果
                try {
                    lastKeyWords = query;
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                    setTitle("搜索：" + query);
                    for (BaseFragment fragment : mPagerAdapter.getFragments()) {
                        fragment.startSearch(query);
                    }
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
}
