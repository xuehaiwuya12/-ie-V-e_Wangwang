package com.koen.wangdog.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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

import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;

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
}
