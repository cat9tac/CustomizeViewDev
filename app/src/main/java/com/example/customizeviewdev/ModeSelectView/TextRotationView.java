package com.example.customizeviewdev.ModeSelectView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.example.customizeviewdev.R;

public class TextRotationView extends View {
    public static final String TAG = "MyTextView";

    private float mTextSize;
    private int  mTextColor;
    private String mText;
    private int mRotation;
    private Context mContext;
    private Paint mPaint;

    public TextRotationView(Context context) {
        this(context,null);
    }

    public TextRotationView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextRotationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.TextRotationView, 0, 0);
        mTextColor = typedArray.getColor(R.styleable.TextRotationView_text_color, mContext.getResources().getColor(R.color.selected_color));
        mTextSize = typedArray.getDimension(R.styleable.TextRotationView_text_size, mContext.getResources().getDimension(R.dimen.normal_text_size));
        mRotation = typedArray.getInteger(R.styleable.TextRotationView_text_rotation,0);
        mText = typedArray.getString(R.styleable.TextRotationView_text_content);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mTextColor);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");

        if(mText == null){
            return;
        }
        Rect mBounds = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
        canvas.save();
        canvas.drawPoint(getWidth()/2,getHeight()/2,mPaint);
        canvas.rotate(mRotation,getWidth()/2,getHeight()/2 );
        canvas.drawText(mText,getWidth()/2,getHeight()/2+ mBounds.height()/2, mPaint);
        canvas.restore();
    }
}
