package com.koen.wangdog;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;

/**
 * Created by Administrator on 2016/1/16.
 */
public class DogApplication extends Application{

    public static DogApplication myApplication;
    public static DogApplication getInstance() {
        return myApplication;
    }

    //好友列表
    private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    public void setContactList(Map<String, BmobChatUser> contactList) {
        if (this.contactList != null) {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }

}
