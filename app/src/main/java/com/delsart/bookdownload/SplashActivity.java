package com.delsart.bookdownload;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.delsart.bookdownload.ui.activity.MainActivity;

/**
 * Created by Delsart on 2017/8/11.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, MainActivity.class));

        finish();
    }
}
