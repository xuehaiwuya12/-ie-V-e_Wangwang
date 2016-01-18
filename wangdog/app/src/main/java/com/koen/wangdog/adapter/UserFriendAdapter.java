package com.koen.wangdog.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.koen.wangdog.R;
import com.koen.wangdog.bean.User;
import com.koen.wangdog.util.ImageUtil;

import java.util.List;

/**
 * Created by smit on 2016/1/18.
 */
public class UserFriendAdapter extends BaseAdapter implements SectionIndexer{

    private Context context;
    private List<User> dataList;

    public UserFriendAdapter(Context ct, List<User> datas) {
        this.context = ct;
        this.dataList = datas;
    }

    //更新list
    public void updateListView(List<User> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }

    public void remove(User user) {
        this.dataList.remove(user);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_user_friend, null);
            viewHolder = new ViewHolder();
            viewHolder.alpha = (TextView) convertView.
                    findViewById(R.id.alpha);
            viewHolder.name = (TextView) convertView.
                    findViewById(R.id.tv_friend_name);
            viewHolder.avatar = (ImageView) convertView.
                    findViewById(R.id.img_friend_avatar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User friend = dataList.get(position);
        String name = friend.getUsername();
        String avatar = friend.getAvatar();

        ImageUtil.setImage(context, viewHolder.avatar,avatar);
        viewHolder.name.setText(name);

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的char的位置，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.alpha.setVisibility(View.VISIBLE);
            viewHolder.alpha.setText(friend.getSortLetters());
        } else {
            viewHolder.alpha.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = dataList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return dataList.get(position).getSortLetters().charAt(0);
    }

    static class ViewHolder {
        TextView alpha;
        ImageView avatar;  //头像
        TextView name;
    }
}
