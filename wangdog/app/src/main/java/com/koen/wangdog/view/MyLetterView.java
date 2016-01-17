package com.koen.wangdog.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.koen.wangdog.R;
import com.koen.wangdog.util.PixelUtil;

/**
 * Created by koen on 2016/1/17.
 */
public class MyLetterView extends View{

    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingListener;

    public static String[] letter = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#" };
    private int choose = -1;  // 选中哪一个。
    private Paint paint = new Paint();

    private TextView mTextDialog;

    public void setTextView(TextView tx) {
        this.mTextDialog = tx;
    }
    public MyLetterView(Context context) {
        super(context);
    }

    public MyLetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLetterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / letter.length; //每个单词的高度。

        for (int i = 0; i < letter.length; i++) {
            paint.setColor(getResources().getColor(R.color.color_bottom_text_normal));
            paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体类型
            paint.setAntiAlias(true);  //抗锯齿
            paint.setTextSize(PixelUtil.sp2px(12));
            // 选中的状态
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true); // 设置为粗体
            }

            // x坐标等于中间-字符串宽度的一半
            float xPos = width / 2 - paint.measureText(letter[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(letter[i], xPos, yPos, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        int oldChoose = choose;
        OnTouchingLetterChangedListener listener = onTouchingListener;
        int c = (int) (y / getHeight() * letter.length);

        if (action == MotionEvent.ACTION_UP) {
            setBackgroundDrawable(new ColorDrawable(0x00000000));
            choose = -1;
            invalidate();
            if (mTextDialog != null) {
                mTextDialog.setVisibility(View.INVISIBLE);
            }
        } else {
            setBackgroundResource(R.drawable.v2_sortlistview_sidebar_background);
            if (oldChoose != c) {
                if (c >= 0 && c < letter.length) {
                    if (listener != null) {
                        listener.onTouchingLetterChanged(letter[c]);
                    }
                }
                if (mTextDialog != null) {
                    mTextDialog.setText(letter[c]);
                    mTextDialog.setVisibility(View.VISIBLE);
                }

                choose = c;
                invalidate();
            }
        }
        return true;
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouching) {
        this.onTouchingListener = onTouching;
    }
}
