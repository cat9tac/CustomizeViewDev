package com.example.customizeviewdev.ModeSelectView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ModeSelectView extends View {

    private final static String TAG = "ModeSelectView";
    private final static int MODE_1 = 1;
    private final static int MODE_2 = 0;
    private Context mContext;
    private String[] modeStrs = new String[]{"模式1", "模式2"};
    private int mMode = 1;


    private Paint mPaint;


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
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(28);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //  Log.d(TAG, "onDraw");
        canvas.save();
        canvas.rotate(90,getWidth()/2,getHeight()/2);
        canvas.drawText("你好",getWidth()/2,getHeight()/2, mPaint);
        canvas.drawText("你好",getWidth()/2,getHeight()/2 +39, mPaint);
        canvas.restore();
        if(mMode == MODE_1) {

        }else {

        }

    }


}
