package com.koen.wangdog.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.koen.wangdog.R;
import com.koen.wangdog.bean.FaceText;

import java.util.List;

/**
 * Created by koen on 2016/1/24.
 */
public class EmoteAdapter extends BaseArrayListAdapter{

    public EmoteAdapter(Context context, List<FaceText> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_face_text, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.
                    findViewById(R.id.v_face_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FaceText faceText = (FaceText) getItem(position);
        String key = faceText.text.substring(1);
        Drawable drawable = mContext.getResources().getDrawable
                (mContext.getResources().getIdentifier
                        (key, "drawable", mContext.getPackageName()));
        holder.imageView.setImageDrawable(drawable);
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
