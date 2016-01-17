package com.koen.wangdog.util;

import android.content.Context;
import android.content.res.Resources;

import com.koen.wangdog.DogApplication;

/**
 * Created by Administrator on 2016/1/17.
 */
public class PixelUtil {

    /**
     * The context.
     */
    private static Context mContext = DogApplication.getInstance();
    /**
     * sp转px.
     *
     * @param value the value
     * @return the int
     */
    public static int sp2px(float value) {
        Resources r;
        if (mContext == null) {
            r = Resources.getSystem();
        } else {
            r = mContext.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }
}
