package com.example.kelina.test;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kelina.test.adapter.MyRecyclerAdapter;

public class TestFragment extends BaseFragment {

    RecyclerView mRecyclerView;
    VpSwipeRefreshLayout swipeRefreshLayout;
    Handler mHandler = new Handler();
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.fragment_historty;
    }

    private void initSwipeRefresh() {
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout = mRootView.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }
        });
    }

    @Override
    public void initViews() {
        initSwipeRefresh();
    }

    @Override
    public void initData() {
        mRecyclerView = mRootView.findViewById(R.id.recyclerViewId);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(new MyRecyclerAdapter());
    }

    @Override
    public void refreshData() {

    }

}
