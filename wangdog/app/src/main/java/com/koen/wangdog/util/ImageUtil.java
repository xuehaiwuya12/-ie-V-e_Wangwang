package com.koen.wangdog.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.koen.wangdog.R;

/**
 * Created by koen on 2016/1/18.
 */

// 专门用于图片显示
public class ImageUtil {

    public static void setImage(Context context, ImageView imageView, String url) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .crossFade()
                .into(imageView);
    }
}
