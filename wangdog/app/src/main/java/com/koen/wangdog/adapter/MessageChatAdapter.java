package com.koen.wangdog.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koen.wangdog.R;
import com.koen.wangdog.util.FaceTextUtil;
import com.koen.wangdog.util.ImageUtil;
import com.koen.wangdog.util.TimeUtil;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;

/**
 * Created by koen on 2016/1/20.
 * 用于显示聊天内容
 */
public class MessageChatAdapter extends BaseListAdapter<BmobMsg> {

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
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE : TYPE_RECEIVER_IMAGE;
        } else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_LOCATION : TYPE_RECEIVER_LOCATION;
        } else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
        } else {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        BmobMsg item = list.get(position);
        if (convertView == null) {
            convertView = createViewByType(item, position);
        }
        // 文本类型
        ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend); //失败重发
        final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);//发送状态
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
        //图片
        ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
        final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);//进度条
        //位置
        TextView tv_location = ViewHolder.get(convertView, R.id.tv_location);
        //语音
        final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);
        //语音长度
        final TextView tv_voice_length = ViewHolder.get(convertView, R.id.tv_voice_length);

        // 点击头像进入个人资料
        String avatar = item.getBelongAvatar();
        if (avatar != null && !avatar.equals("")) {
            ImageUtil.setImage(mContext, iv_avatar, avatar);
        }
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 进入用户详细信息页面。。
                 */
            }
        });

        tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item.getMsgTime())));

        if (getItemViewType(position) == TYPE_SEND_TXT
                //||getItemViewType(position)==TYPE_SEND_IMAGE//图片单独处理
                || getItemViewType(position) == TYPE_SEND_LOCATION
                || getItemViewType(position) == TYPE_SEND_VOICE) {  //只有自己发送的信息才有重发机制
            if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) { //发送成功
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_send_status.setVisibility(View.GONE);
                    tv_voice_length.setVisibility(View.VISIBLE);
                } else {
                    tv_send_status.setVisibility(View.VISIBLE);
                    tv_send_status.setText("已发送");
                }
            } else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) { //服务器无响应或者查询失败等原因造成的发送失败，均需要重发
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.VISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
                if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_voice_length.setVisibility(View.GONE);
                }
            } else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {//对方已接收到
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_send_status.setVisibility(View.GONE);
                    tv_voice_length.setVisibility(View.VISIBLE);
                } else {
                    tv_send_status.setVisibility(View.VISIBLE);
                    tv_send_status.setText("已阅读");
                }
            } else if (item.getStatus() == BmobConfig.STATUS_SEND_START) {//开始上传
                progress_load.setVisibility(View.VISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
                if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_voice_length.setVisibility(View.GONE);
                }
            }
        }
        // 根据类型显示内容
        final String text = item.getContent();
        switch (item.getMsgType()) {
            case BmobConfig.TYPE_TEXT:
                try {
                    SpannableString spannableString = FaceTextUtil.toSpannableString(mContext, text);
                    tv_message.setText(spannableString);
                } catch (Exception e) {}
                break;
            case BmobConfig.TYPE_IMAGE: // 图片类

                break;

            case BmobConfig.TYPE_LOCATION:

                break;

            case BmobConfig.TYPE_VOICE:

                break;
        }

        return convertView;
    }


    private View createViewByType(BmobMsg message, int position) {
        int type = message.getMsgType();
        if (type == BmobConfig.TYPE_IMAGE) {
            return getItemViewType(position) == TYPE_RECEIVER_IMAGE ?
                    mInflater.inflate(R.layout.item_chat_received_image, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_image, null);
        } else if (type == BmobConfig.TYPE_LOCATION) {//位置类型
            return getItemViewType(position) == TYPE_RECEIVER_LOCATION ?
                    mInflater.inflate(R.layout.item_chat_received_location, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_location, null);
        } else if (type == BmobConfig.TYPE_VOICE) {//语音类型
            return getItemViewType(position) == TYPE_RECEIVER_VOICE ?
                    mInflater.inflate(R.layout.item_chat_received_voice, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_voice, null);
        } else {//剩下默认的都是文本
            return getItemViewType(position) == TYPE_RECEIVER_TXT ?
                    mInflater.inflate(R.layout.item_chat_received_message, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_message, null);
        }
    }
}
