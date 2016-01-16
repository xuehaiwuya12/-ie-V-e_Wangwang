package com.koen.wangdog.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.koen.wangdog.R;
import com.koen.wangdog.bean.User;
import com.koen.wangdog.util.CommonUtils;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/1/16.
 */
public class RegisterActivity extends BaseActivity{

    Button btn_register;
    EditText et_username, et_password, et_email;
    BmobChatUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_register);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_email = (EditText) findViewById(R.id.et_email);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                register();
            }
        });
    }

    private void register() {
        String name = et_username.getText().toString();
        String password = et_password.getText().toString();
        String pwd_again = et_email.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ShowToast(R.string.toast_error_username_null);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ShowToast(R.string.toast_error_password_null);
            return;
        }
        if (!pwd_again.equals(password)) {
            ShowToast(R.string.toast_error_comfirm_password);
            return;
        }
        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            ShowToast(R.string.network_tips);
            return;
        }

        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        final User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        user.setSex(true);
        user.setDeviceType("android");
        user.setInstallId(BmobInstallation.getInstallationId(this));
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                ShowToast("注册成功");
                // 将设备与username进行绑定
                userManager.bindInstallationForRegister(user.getUsername());
                sendBroadcast(new Intent(RegisterActivity.this, MainActivity.class));
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                ShowToast("注册失败:" + s);
                progress.dismiss();
            }
        });
    }
}
