package com.delsart.bookdownload.listandadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delsart on 2017/7/22.
 */

public class mpageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();
    public mpageAdapter(FragmentManager fm) {
        super(fm);
    }


    public void clean(){
        mFragments.clear();
        mFragmentTitles.clear();
    }
    public void remove(int position) {
        mFragments.remove(position);
        mFragmentTitles.remove(position);
notifyDataSetChanged();

    }


    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}