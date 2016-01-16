package com.koen.wangdog.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.koen.wangdog.DogApplication;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;

/**
 * Created by koen on 2016/1/16.
 */
public class BaseActivity extends AppCompatActivity{

    BmobUserManager userManager;
    BmobChatManager chatManager;

    DogApplication mApplication;

    protected int mScreenWidth;
    protected int mScreenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        userManager = BmobUserManager.getInstance(this);
        
    }

    private void init() {

    }
}
