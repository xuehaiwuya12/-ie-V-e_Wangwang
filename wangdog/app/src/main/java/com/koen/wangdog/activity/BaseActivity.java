package com.koen.wangdog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.koen.wangdog.DogApplication;
import com.koen.wangdog.config.DogConfig;
import com.koen.wangdog.util.CollectionUtils;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by koen on 2016/1/16.
 */
public class BaseActivity extends AppCompatActivity{

    BmobUserManager userManager;
    BmobChatManager chatManager;

    DogApplication mApplication;

    protected int mScreenWidth;
    protected int mScreenHeight;

    Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        userManager = BmobUserManager.getInstance(this);
        chatManager = BmobChatManager.getInstance(this);
        mApplication = DogApplication.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(),text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });
        }
    }

    public void ShowToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mToast == null) {
                    mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
                            Toast.LENGTH_LONG);
                } else {
                    mToast.setText(resId);
                }
                mToast.show();
            }
        });
    }

    public void ShowLog(String msg){
        Log.i("koen", msg);
    }

    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(this, cla));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
    }

    // 更新用户信息
    public void updateUserInfos() {
        BmobConfig.LIMIT_CONTACTS = DogConfig.LIMIT_CONTACTS;
        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {
                DogApplication.getInstance().setContactList(CollectionUtils.list2map(list));
            }

            @Override
            public void onError(int i, String s) {
                if(i == BmobConfig.CODE_COMMON_NONE) {
                    ShowLog(s);
                } else {
                    ShowLog("查询好友列表失败"+ s);
                }
            }
        });
    }


    /**
     * 更新用户的经纬度信息
     */
    public void updateUserLocation() {

    }
}
