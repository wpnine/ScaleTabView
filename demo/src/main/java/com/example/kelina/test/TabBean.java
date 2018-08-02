package com.example.kelina.test;

/**
  *@date on 2017/11/29
  *@author pengfeng
  *@describe tab对象
  */
public class TabBean {
    public String title;   //标题
    public BaseFragment fragment;//子页面

    public TabBean(String title, BaseFragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }
}