package com.delsart.bookdownload.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.delsart.bookdownload.ui.fragment.BaseFragment;

import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private final List<BaseFragment> mFragments;
    private final List<String> mFragmentTitles;

    public PagerAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
        super(fm);
        this.mFragments = fragments;
        this.mFragmentTitles = titles;
    }

    public void setTop(int pos) {
        mFragments.get(pos).toTop();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public void remove() {
        if (mFragments.size() > 0) {
            mFragments.remove(0);
            mFragmentTitles.remove(0);
            notifyDataSetChanged();
        }
    }

    public List<BaseFragment> getFragments() {
        return mFragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}