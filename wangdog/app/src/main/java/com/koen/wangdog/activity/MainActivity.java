package com.koen.wangdog.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Button;
import com.koen.wangdog.R;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.EventListener;

public class MainActivity extends DetectActivity implements EventListener {

    private Button[] mTabs;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onMessage(BmobMsg bmobMsg) {

    }

    @Override
    public void onReaded(String s, String s1) {

    }

    @Override
    public void onNetChange(boolean b) {

    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {

    }

    @Override
    public void onOffline() {

    }
}
