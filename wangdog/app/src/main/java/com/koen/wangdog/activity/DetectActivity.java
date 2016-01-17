package com.koen.wangdog.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.koen.wangdog.R;


/**
 * Created by koen on 2016/1/17.
 * 除去登陆和注册外，继承的基类，用于检测是否有其他设备登陆了同一个账号
 */
public class DetectActivity extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        checkLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    public void checkLogin() {
        if (userManager.getCurrentUser() == null) {
            ShowToast(R.string.your_account_have_be_landed);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    //隐藏软键盘
    public void hideSoftInputView() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
