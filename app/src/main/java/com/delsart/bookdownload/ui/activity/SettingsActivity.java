package com.delsart.bookdownload.ui.activity;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import com.delsart.bookdownload.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.DarkThemeSettingTheme);
        else
            setTheme(R.style.settingTheme);
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.settinglayout);
    }



    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        finish();
        return super.onMenuItemSelected(featureId, item);

    }
}
