package com.koen.wangdog.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.koen.wangdog.DogApplication;
import com.koen.wangdog.R;
import com.koen.wangdog.activity.MainActivity;
import com.koen.wangdog.util.CollectionUtils;
import com.koen.wangdog.util.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

/**
 * 推送消息接收器
 * Created by koen on 2016/1/24.
 */
public class MyMessageReceiver extends BroadcastReceiver{

    // 事件监听
    public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

    public static final int NOTIFY_ID = 0x000;
    public static int mNewNum = 0;
    BmobUserManager userManager;
    BmobChatUser currentUser;

    // 如果你想发送自定义格式的消息，请使用sendJsonMessage方法来发送Json格式的字符串，然后解析

    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("msg");
        userManager = BmobUserManager.getInstance(context);
        currentUser = userManager.getCurrentUser();
        boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
        if (isNetConnected) {
            parseMessage(context, json);
        } else {
            for (int i = 0; i < ehList.size(); i ++) {
                ((EventListener) ehList.get(i)).onNetChange(isNetConnected);
            }
        }

    }

    /**
     * 解析Json字符串
     */
    private void parseMessage(final Context context, String json) {
        JSONObject jo;
        try {
            jo = new JSONObject(json);
            String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
            if (tag.equals(BmobConfig.TAG_OFFLINE)) { // 下线
                if (currentUser != null) {
                    if (ehList.size() > 0) {
                        for (EventListener handler : ehList) {
                            handler.onOffline();
                        }
                    } else {
                        // 清空数据
                        DogApplication.getInstance().logout();
                    }
                }
            } else {
                String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
                // 增加消息接收方的ObjectId
                // 目的是解决多账户登录同一设备时，无法接收到非当前登录用户的消息。
                final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
                String msgTime = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_MSGTIME);
                if (fromId != null && !BmobDB.create(context, toId).isBlackUser(fromId)) { //该消息发送方不为黑名单
                    if (TextUtils.isEmpty(tag)) { // 不携带tag标签，可接收陌生人的消息
                        BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {
                            @Override
                            public void onSuccess(BmobMsg bmobMsg) {
                                if (ehList.size() > 0) { // 有监听的时候，传递下去
                                    for (int i = 0; i < ehList.size(); i ++) {
                                        ((EventListener) ehList.get(i)).onMessage(bmobMsg);
                                    }
                                } else {
                                    boolean isAllow = DogApplication.getInstance().getmSpUtil().isAllowPushNotify();
                                    if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) { // 当前登录用户存在并且也等于接收方id
                                        mNewNum++;
                                        showMsgNotify(context, bmobMsg);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                BmobLog.i("获取接收的消息失败："+ s);
                            }
                        });

                    } else { // 带tag 标签
                        if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
                            // 保存好友请求到本地，并更新后台的未读字段
                            BmobInvitation message = BmobChatManager.getInstance(context)
                                    .saveReceiveInvite(json, toId);
                            if (currentUser != null) { // 有登陆用户
                                if (toId.equals(currentUser.getObjectId())) {
                                    if (ehList.size() > 0) { // 有监听的时候，传递下去
                                        for (EventListener handler : ehList) {
                                            handler.onAddUser(message);
                                        }
                                    } else {
                                        /*showOtherNotify(context, message.getFromname(),
                                                toId, message.getFromname() + "请求添加好友",
                                                NewFriendActivity.class);*/
                                    }
                                }
                            }
                        } else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {
                            String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
                            //收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
                            BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {
                                @Override
                                public void onSuccess(List<BmobChatUser> list) {
                                    DogApplication.getInstance().setContactList
                                            (CollectionUtils.list2map(BmobDB.create(context)
                                            .getContactList()));
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });
                            //显示通知
                            showOtherNotify(context, username, toId,  username+"同意添加您为好友", MainActivity.class);
                            //创建一个临时验证会话--用于在会话界面形成初始会话
                            BmobMsg.createAndSaveRecentAfterAgree(context, json);
                        } else if (tag.equals(BmobConfig.TAG_READED)) { //已读回执
                            String conversionId = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_CONVERSIONID);
                            if(currentUser!=null){
                                //更改某条消息的状态
                                BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
                                if(toId.equals(currentUser.getObjectId())){
                                    if (ehList.size() > 0) {// 有监听的时候，传递下去--便于修改界面
                                        for (EventListener handler : ehList)
                                            handler.onReaded(conversionId, msgTime);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    BmobChatManager.getInstance(context).updateMsgReaded(true, fromId, msgTime);
                    BmobLog.i("该消息发送方为黑名单用户");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //这里截取到的有可能是web后台推送给客户端的消息，也有可能是开发者自定义发送的消息，需要开发者自行解析和处理
            BmobLog.i("parseMessage错误："+e.getMessage());
        }
    }

    /**
     * 显示与聊天消息的通知
     */
    public void showMsgNotify(Context context, BmobMsg msg) {
        // 更新通知栏
        int icon = R.mipmap.ic_launcher;
        String trueMsg = "";
        if(msg.getMsgType()==BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")){
            trueMsg = "[表情]";
        }else if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
            trueMsg = "[图片]";
        }else if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
            trueMsg = "[语音]";
        }else if(msg.getMsgType()==BmobConfig.TYPE_LOCATION){
            trueMsg = "[位置]";
        }else{
            trueMsg = msg.getContent();
        }
        CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
        String contentTitle = msg.getBelongUsername() + "(" + mNewNum + "条新消息)";

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean isAllowVoice = DogApplication.getInstance().getmSpUtil().isAllowVoice();
        boolean isAllowVibrate = DogApplication.getInstance().getmSpUtil().isAllowVibrate();

        BmobNotifyManager.getInstance(context).showNotifyWithExtras
                (isAllowVoice, isAllowVibrate, icon, tickerText.toString(),
                        contentTitle, tickerText.toString(), intent);
    }

    /**
     * 显示其他tag的通知
     */
    public void showOtherNotify(Context context, String username, String toId,
                                String ticker, Class<?> cls) {

        boolean isAllow = DogApplication.getInstance().getmSpUtil().isAllowPushNotify();
        boolean isAllowVoice = DogApplication.getInstance().getmSpUtil().isAllowVoice();
        boolean isAllowVibrate = DogApplication.getInstance().getmSpUtil().isAllowVibrate();
        if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) {
            //同时提醒通知
           // BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,isAllowVibrate,R.mipmap.ic_launcher, ticker,username, ticker.toString(),NewFriendActivity.class);
        }

    }
}
