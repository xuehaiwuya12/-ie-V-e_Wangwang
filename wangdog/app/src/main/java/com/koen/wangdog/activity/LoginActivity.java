package com.koen.wangdog.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.koen.wangdog.R;
import com.koen.wangdog.bean.User;
import com.koen.wangdog.config.DogConstants;
import com.koen.wangdog.util.CommonUtils;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/1/16.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    EditText edit_username, edit_password;
    Button btn_login;
    TextView btn_register;

    private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_login);
        init();
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(DogConstants.ACTION_REGISTER_SUCCESS_FINISH);
        registerReceiver(receiver, filter);
    }

    private void init() {
        edit_username = (EditText) findViewById(R.id.et_username);
        edit_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (TextView) findViewById(R.id.btn_register);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && DogConstants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_register) {
            /*Intent intent = new Intent(this, );
            startActivity(intent);*/
        } else {
            boolean isNetConnect = CommonUtils.isNetworkAvailable(this);
            if (!isNetConnect) {
                ShowToast(R.string.network_tips);
                return;
            }
            login();
        }

    }

    private void login() {
        String name = edit_username.getText().toString();
        String password = edit_password.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ShowToast(R.string.toast_error_username_null);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ShowToast(R.string.toast_error_password_null);
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登陆...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        userManager.login(user, new SaveListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage("正在获取好友列表....");
                    }
                });
                //更新好友资料
                updateUserInfos();
                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
                ShowToast(s);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
