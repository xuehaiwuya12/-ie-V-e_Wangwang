package com.koen.wangdog.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koen.wangdog.R;
import com.koen.wangdog.adapter.EmoViewPagerAdapter;
import com.koen.wangdog.adapter.EmoteAdapter;
import com.koen.wangdog.adapter.MessageChatAdapter;
import com.koen.wangdog.bean.FaceText;
import com.koen.wangdog.receiver.MyMessageReceiver;
import com.koen.wangdog.util.CommonUtils;
import com.koen.wangdog.util.FaceTextUtil;
import com.koen.wangdog.view.EmoticonsEditText;
import com.koen.wangdog.view.XList.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.inteface.EventListener;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

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

    MessageChatAdapter messageChatAdapter;
    NewBroadcastReceiver receiver;
    List<FaceText> emos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatManager = BmobChatManager.getInstance(this);
        MsgPagerNum = 0;
        // 组装聊天对象
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();
        initNewBroadcastReceiver();
        initView();
    }



    private void initView() {
        mListView = (XListView) findViewById(R.id.mListView);
        initBottomView();
        initXListView();
       // initVoiceView();
    }
    /**
     * 初始化语音布局
     *
     * @Title: initVoiceView
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
   /* private void initVoiceView() {
        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }*/


    private void initBottomView() {
        // 最左边
        btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
        btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
        btn_chat_add.setOnClickListener(this);
        btn_chat_emo.setOnClickListener(this);
        // 最右边
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);
        // 最下面
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);
        initAddView();
        initEmoView();

        // 最中间
        // 语音框
        btn_speak = (Button) findViewById(R.id.btn_speak);
        // 输入框
        edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
        edit_user_comment.setOnClickListener(this);
        edit_user_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initAddView() {
        tv_picture = (TextView) findViewById(R.id.tv_picture);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_picture.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
    }

    /**
     * 初始化表情布局
     */
    private void initEmoView() {
        pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtil.faceTexts;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
    }

    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this, list);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
                        int start = edit_user_comment.getSelectionStart();
                        CharSequence content = edit_user_comment.getText().insert(start, key);
                        edit_user_comment.setText(content);

                        // 定位光标位置
                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + key.length());
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
        return view;
    }

    private void initXListView() {
        // 不允许加载更多
        mListView.setPullLoadEnable(false);
        // 允许下拉
        mListView.setPullRefreshEnable(true);
        // 设置监听器
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mListView.setDividerHeight(0);
        // 加载数据
        initOrRefresh();
        mListView.setSelection(messageChatAdapter.getCount() - 1);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInputView();
                layout_more.setVisibility(View.GONE);
                layout_add.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.GONE);
                btn_chat_send.setVisibility(View.GONE);
                return false;
            }
        });

        // 重发按钮的点击事件
        messageChatAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
                new MessageChatAdapter.onInternalClickListener() {
                    @Override
                    public void onClickListener(View parentV, View v, Integer position, Object values) {
                        showResendDialog(parentV, v, values);
                    }
                });
    }

    /**
     * 显示重发按钮
     */
    public void showResendDialog(final View parentV, View v, final Object vlaues) {
    }

    private void initNewBroadcastReceiver() {
        // 注册接收信息广播
        receiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        // 设置广播的优先级大于MainActivity,这样如果消息来的时候正在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 刷新界面
     */
    private void initOrRefresh() {
        if (messageChatAdapter != null) {
            if (MyMessageReceiver.mNewNum != 0) {
                // 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
                int news = MyMessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int size = initMsgData().size();
                for (int i = (news - 1); i > 0; i--) {
                    // 添加最后一条消息到界面显示
                    messageChatAdapter.add(initMsgData().get(size - (i + 1)));
                }
                mListView.setSelection(messageChatAdapter.getCount() -1);
            } else {
                messageChatAdapter.notifyDataSetChanged();
            }
        } else {
            messageChatAdapter = new MessageChatAdapter(this, initMsgData());
            mListView.setAdapter(messageChatAdapter);
        }
    }

    /**
     * 加载消息历史，从数据库中读出
     */
    private List<BmobMsg> initMsgData() {
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId, MsgPagerNum);
        return list;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_user_comment:// 点击文本输入框
                mListView.setSelection(mListView.getCount() - 1);
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_chat_emo:// 点击笑脸图标
                if (layout_more.getVisibility() == View.GONE) {
                   //showEditState(true);
                } else {
                    if (layout_add.getVisibility() == View.VISIBLE) {
                        layout_add.setVisibility(View.GONE);
                        layout_emo.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }

                break;
            case R.id.btn_chat_add:// 添加按钮-显示图片、拍照、位置
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emo.setVisibility(View.GONE);
                    hideSoftInputView();
                } else {
                    if (layout_emo.getVisibility() == View.VISIBLE) {
                        layout_emo.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }

                break;
            case R.id.btn_chat_voice:// 语音按钮
                edit_user_comment.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
               // showEditState(false);
                break;
            case R.id.btn_chat_send:// 发送文本
                final String msg = edit_user_comment.getText().toString();
                if (msg.equals("")) {
                    ShowToast("请输入发送消息!");
                    return;
                }
                boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
                if (!isNetConnected) {
                    ShowToast(R.string.network_tips);
                    // return;
                }
                // 组装BmobMessage对象
                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
                message.setExtra("Bmob");
                // 默认发送完成，将数据保存到本地消息表和最近会话表中
                //manager.sendTextMessage(targetUser, message);
                // 刷新界面
              //  refreshMessage(message);

                break;
            case R.id.tv_camera:// 拍照
               // selectImageFromCamera();
                break;
            case R.id.tv_picture:// 图片
               // selectImageFromLocal();
                break;
            case R.id.tv_location:// 位置
               // selectLocationFromMap();
                break;
            default:
                break;
        }
    }

    @Override
    public void onMessage(BmobMsg bmobMsg) {
        /*Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);*/
    }

    @Override
    public void onReaded(String s, String s1) {

    }

    @Override
    public void onNetChange(boolean b) {
        if (!b) {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {

    }

    @Override
    public void onOffline() {

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
                // 添加到当前页面
                messageChatAdapter.add(msg);
                // 定位
                mListView.setSelection(messageChatAdapter.getCount() - 1);
                // 取消当天聊天对象的未读标示
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
            abortBroadcast();
        }
    }
}
