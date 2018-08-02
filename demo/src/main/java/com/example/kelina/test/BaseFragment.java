package com.example.kelina.test;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseFragment extends Fragment{

    public static <T extends Fragment>T newInstance(Class clz,
                                                    Bundle args) {
        T fragment = null;
        try {
            fragment = (T)clz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    protected FragmentActivity mActivity;
    protected View mRootView;
    protected Context context;
    protected boolean isVisibleToUser;
    public BaseFragment(){
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = getActivity();
        }
    }
    protected boolean isWatch=true;
    protected abstract int getContentViewLayoutId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=getContext();
        mRootView =  inflater.inflate(getContentViewLayoutId(), container, false);
        initViews();
        initData();
        return mRootView;
    }

    @Override
    public void startActivity(Intent intent) {
        getActivity().startActivity(intent);
    }

    /**
     * 在ViewPager的时候会调用这个方法。
     * @param isVisible
     */
    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisible);
        String className = getClass().getSimpleName();
        this.isVisibleToUser = isVisible;
                if (isVisibleToUser&&isAdded()){
                    refreshData();
                }


    }

    @Override
    public void onHiddenChanged(final boolean hidden) {
        super.onHiddenChanged(hidden);
                if (!hidden){
                    refreshData();
                }
    }

    public abstract void initViews();

    public abstract void initData();

    public abstract void refreshData();


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;  //清除掉相应的界面
    }


}


