package com.example.customizeviewdev.ModeSelectView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class ModeSelectView extends LinearLayout {

    private final static String TAG = "ModeSelectView";
    private final static int MODE_1 = 1;
    private final static int MODE_2 = 0;
    private Context mContext;
    private String[] modeStrs = new String[]{"模式1", "模式2"};
    private int mMode = 1;

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(HORIZONTAL);
    }

    public ModeSelectView(Context context) {
        this(context, null);
    }

    public ModeSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ModeSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }


}
