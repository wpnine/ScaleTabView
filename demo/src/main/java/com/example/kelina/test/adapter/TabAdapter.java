package com.example.kelina.test.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kelina.test.TabBean;

import java.util.List;

/**
  *@date on 2017/11/29
  *@author pengfeng
  *@describe tab适配器
  */
public class TabAdapter extends FragmentPagerAdapter {
        private List<TabBean> tabBeans;

        public TabAdapter(FragmentManager fm, List<TabBean> tabBeans) {
            super(fm);
            this.tabBeans = tabBeans;
        }

        @Override
        public Fragment getItem(int position) {
            return tabBeans.get(position).fragment;
        }

        @Override
        public int getCount() {
            return tabBeans.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabBeans.get(position).title;
        }
//
//
//        @Override
//        public Parcelable saveState() {
//            return null;
//        }
    }