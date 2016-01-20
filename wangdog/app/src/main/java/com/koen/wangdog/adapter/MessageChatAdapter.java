package com.koen.wangdog.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;

/**
 * Created by koen on 2016/1/20.
 * 用于显示聊天内容
 */
public class MessageChatAdapter extends BaseListAdapter<BmobMsg>{

    // 共计8种Item的类型
    // 文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    // 图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    // 位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    // 语音
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;

    String currentObjectId = "";

    public MessageChatAdapter(Context context, List<BmobMsg> list) {
        super(context, list);
        currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
    }

    @Override
    public int getItemViewType(int position) {

        BmobMsg msg = list.get(position);
        if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
        } else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_LOCATION: TYPE_RECEIVER_LOCATION;
        }else if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_VOICE: TYPE_RECEIVER_VOICE;
        }else{
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    @Override
    public View bindView(int position, View converView, ViewGroup parent) {
        return null;
    }


    private View createViewByType() {
        return null;
    }
}
