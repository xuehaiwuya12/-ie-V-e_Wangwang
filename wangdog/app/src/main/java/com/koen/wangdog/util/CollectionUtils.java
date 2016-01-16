package com.koen.wangdog.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;

/**
 * Created by Administrator on 2016/1/16.
 */
public class CollectionUtils {

    /** list转map
     *  以用户名为key
     * @return Map<String,BmobChatUser>
     * @throws
     */
    public static Map<String,BmobChatUser> list2map(List<BmobChatUser> users){
        Map<String,BmobChatUser> friends = new HashMap<String, BmobChatUser>();
        for(BmobChatUser user : users){
            friends.put(user.getUsername(), user);
        }
        return friends;
    }
}
