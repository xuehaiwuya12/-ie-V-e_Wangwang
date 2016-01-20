package com.koen.wangdog.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koen.wangdog.R;
import com.koen.wangdog.view.EmoticonsEditText;
import com.koen.wangdog.view.XList.XListView;

import java.util.EventListener;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;

/**
 * Created by koen on 2016/1/20.
 */
public class ChatActivity extends DetectActivity implements View.OnClickListener,
        XListView.IXListViewListener, EventListener{

    private Button btn_chat_emo, btn_chat_send, btn_chat_add,
            btn_chat_keyboard, btn_speak, btn_chat_voice;

    XListView mListView;

    EmoticonsEditText edit_user_comment;

    String targetId = "";

    BmobChatUser targetUser;

    private static int MsgPagerNum;

    private LinearLayout layout_more, layout_emo, layout_add;

    private ViewPager pager_emo;

    private TextView tv_picture, tv_camera, tv_location;

    // 语音相关
    RelativeLayout layout_record;
    TextView tv_voice_tips;
    ImageView iv_record;

    private Drawable[] drawable_Anims; //话筒动画

    BmobRecordManager recordManager;  //负责录音和存储

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatManager = BmobChatManager.getInstance(this);
        MsgPagerNum = 0;
        // 组装聊天对象
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 新消息广播接收者
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            //收到这个广播的时候，message已经在消息表中，可直接获取
            if (TextUtils.isEmpty(from) && TextUtils.isEmpty(msgId)
                    && TextUtils.isEmpty(msgTime)) {
                BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
                if (!from.equals(targetId)) { //如果不是当前正在聊天对象的消息，不处理。
                    return;
                }






            }

        }
    }
}
