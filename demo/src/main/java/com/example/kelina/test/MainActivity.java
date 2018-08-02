package com.example.kelina.test;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.kelina.test.adapter.TabAdapter;
import com.hungrytree.scaletab.ScaleTabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    ScaleTabLayout magicIndicator;
    private List<TabBean> mTabBeans = new ArrayList<>();
    private TabAdapter mViewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewPagerID);
        magicIndicator = (ScaleTabLayout) findViewById(R.id.tabLayoutID);
        initViews();
    }
    private void initViews() {
        mTabBeans.add(new TabBean("A页面", new TestFragment()));
        mTabBeans.add(new TabBean("B页面", new TestFragment()));
        mTabBeans.add(new TabBean("C", new TestFragment()));
        mTabBeans.add(new TabBean("D页面", new TestFragment()));
        mTabBeans.add(new TabBean("E", new TestFragment()));
        mTabBeans.add(new TabBean("F页面", new TestFragment()));
        mTabBeans.add(new TabBean("G", new TestFragment()));
        mViewPagerAdapter = new TabAdapter(getSupportFragmentManager(), mTabBeans);
        viewPager.setAdapter(mViewPagerAdapter);
        magicIndicator.setupWithViewPager(viewPager);
    }
}
