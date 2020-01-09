package com.example.customizeviewdev.ZoomScaleView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.example.customizeviewdev.R;

import java.math.BigDecimal;

public class ScaleView extends View {

    private final static String TAG = "ScaleView";


    //线高度
    private final int mMidlineHeight = 72;//中间线36dp
    private final int mFirstLineHeight = 38;//中间线19dp
    private final int mSecondLineHeight = 28;//中间线14dp
    private final int mNormalLineHeight = 18;//普通线高度9dp
    //刻度间隔
    private final int mSpacing = 24/*12*/;
    private final float mMinZoom = 1.0f;
    private final float mLength;
    private float mMaxZoom = 8.0f;

    private Context mContext;

    private int mHeight;
    private int mWidth;
    //绘制参数
    private float mStartX;
    private int mMidlineColor;//中间线颜色
    private int mNormaLineColor;//普通线颜色
    private Paint mLinePaint;

    private float mZoomValue;
    private static Context context;


    private int mTouchSlop;
    private OnScaleViewSlideListener mListener;
    private int mLineTemp = 0;
    private float mMovePercent = 0f;
    private float mMoved = 0f;
    //用于计算绘制帧率
    private long startTime = 0;
    private int frameCount = 0;


    public interface OnScaleViewSlideListener {
        void onZoomChanged(float value);

        void changeState(boolean onMove);
    }

    public ScaleView(Context context) {
        this(context, null);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttrs(attrs);
        initPaint();
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        Log.d(TAG, "mTouchSlop = " + mTouchSlop);
        //手动设置初始zoom值
        mZoomValue = 6.5f;
        setZoomValue(mZoomValue);
        mLength = mSpacing * getLineNum(mMaxZoom);
    }

    private void initPaint() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(3);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //ACTION_DOWN时的点
                mDownX = event.getX();
                mIsTouch = true;
                moving = 0;
                if (mListener != null) {
                    mListener.changeState(true);
                }
                mLineTemp = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mScrollX = event.getX();
                moving = (mScrollX - mDownX) * 0.8f;
                if (moving < 0 && Math.abs(moving + mMoved) > mLength) {
                    moving = -mLength - mMoved;
                } else if (moving > 0 && (moving + mMoved) > 0) {
                    moving = 0 - mMoved;
                }
                mMovePercent = Math.abs(moving + mMoved) % mSpacing / mSpacing;
                if (moving < 0) {
                    mLineTemp = Math.abs((int) ((moving + mMoved) / mSpacing));
                } else {
                    mLineTemp = getCeilInt(Math.abs(moving + mMoved) / mSpacing);
                }

                if (mListener != null) {
                    mListener.onZoomChanged(getZoomFromMoving(moving + mMoved));
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mIsTouch = false;
                if (mListener != null) {
                    mListener.changeState(false);
                }
                mMoved = mMoved + moving;
                moving = 0;
                mZoomValue = getZoomFromMoving(mMoved);
                //invalidate();
                break;
            default:
                break;
        }

        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //  Log.d(TAG, "onDraw");

        mWidth = getWidth();
        mHeight = getHeight();
        //计算绘制帧率
        caculateFrameRate(true);

        //保存canvas初始状态
        int initState = canvas.save();


        float startLineY;
        float endLineY;
        Log.d(TAG, "\nmLineTemp = " + mLineTemp
                + "    mMovePercent = " + mMovePercent
                + "    mMoved = " + mMoved
                + "    moving = " + moving
                + "    zoom = " + getZoomFromMoving(moving + mMoved)
        );

        mStartX = mWidth / 2 + mMoved + moving;
        mLinePaint.setColor(mNormaLineColor);

        canvas.translate(mStartX, 0);
        //开始画刻度
        int lineNum = getLineNum(mMaxZoom);
        float lineHeight;
        for (int i = 0; i <= lineNum; i++) {
            if (i == mLineTemp) {
                lineHeight = mMidlineHeight - (mMidlineHeight - mFirstLineHeight) * (moving <= 0 ? mMovePercent : (1 - mMovePercent));
                startLineY = mHeight / 2 - lineHeight / 2;
                endLineY = mHeight / 2 + lineHeight / 2;
            } else if (Math.abs(mLineTemp - i) == 1) {
                // lineHeight = mFirstLineHeight - (mFirstLineHeight - mSecondLineHeight) * mMovePercent * (i < 0 ? moving <= 0 ? 1 : -1 : moving <= 0 ? -1 : 1);
                if (i < mLineTemp) {
                    if (moving <= 0) {
                        lineHeight = mFirstLineHeight - (mFirstLineHeight - mSecondLineHeight) * mMovePercent;
                    } else {
                        lineHeight = mFirstLineHeight + (mMidlineHeight - mFirstLineHeight) * (1 - mMovePercent);
                    }
                } else {
                    if (moving < 0) {
                        lineHeight = mFirstLineHeight + (mMidlineHeight - mFirstLineHeight) * mMovePercent;
                    } else {
                        lineHeight = mFirstLineHeight - (mFirstLineHeight - mSecondLineHeight) * (1 - mMovePercent);
                    }
                }
                startLineY = mHeight / 2 - lineHeight / 2;
                endLineY = mHeight / 2 + lineHeight / 2;
            } else if (Math.abs(mLineTemp - i) == 2) {
                if (i < mLineTemp) {
                    if (moving <= 0) {
                        lineHeight = mSecondLineHeight - (mSecondLineHeight - mNormalLineHeight) * mMovePercent;
                    } else {
                        lineHeight = mSecondLineHeight + (mFirstLineHeight - mSecondLineHeight) * (1 - mMovePercent);
                    }
                } else {
                    if (moving < 0) {
                        lineHeight = mSecondLineHeight + (mFirstLineHeight - mSecondLineHeight) * mMovePercent;
                    } else {
                        lineHeight = mSecondLineHeight - (mSecondLineHeight - mNormalLineHeight) * (1 - mMovePercent);
                    }
                }
                startLineY = mHeight / 2 - lineHeight / 2;
                endLineY = mHeight / 2 + lineHeight / 2;
            } else {
                lineHeight = mNormalLineHeight;
                startLineY = mHeight / 2 - lineHeight / 2;
                endLineY = mHeight / 2 + lineHeight / 2;
            }

            canvas.drawLine(0, startLineY, 0, endLineY, mLinePaint);
            canvas.translate(mSpacing, 0);
        }
        canvas.save();

        //恢复到画刻度前的初始canvas状态
        canvas.restoreToCount(initState);
        //接着移动到屏幕中间画黄色中间线
        mLinePaint.setColor(mMidlineColor);
        mLinePaint.setStrokeWidth(3);
        startLineY = mHeight / 2 - mMidlineHeight / 2;
        endLineY = mHeight / 2 + mMidlineHeight / 2;
        canvas.translate(mWidth / 2, 0);
        canvas.drawLine(0, startLineY, 0, endLineY, mLinePaint);
    }


    private float mDownX;
    private float mScrollX;
    private boolean mIsTouch = false;

    float moving = 0;


    private void update() {
        if (mZoomValue <= 1.0f) {
            mZoomValue = 1.0f;
        } else if (mZoomValue >= mMaxZoom) {
            mZoomValue = mMaxZoom;
        }
        if (mListener != null) {
            mListener.onZoomChanged(mZoomValue);
        }
        invalidate();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ScaleView, 0, 0);

        mMidlineColor = typedArray.getColor(R.styleable.ScaleView_selected_color, mContext.getResources().getColor(R.color.selected_color));
        mNormaLineColor = typedArray.getColor(R.styleable.ScaleView_normal_color, mContext.getResources().getColor(R.color.normal_color));

        //mMidlineHeight = (int) typedArray.getDimension(R.styleable.ScaleView_selected_height, mContext.getResources().getDimension(R.dimen.selected_height));
        //mNormalLineHeight = (int) typedArray.getDimension(R.styleable.ScaleView_normal_height, mContext.getResources().getDimension(R.dimen.normal_height));

    }


    public void setZoomValue(float zoom) {
        mZoomValue = accurateFloat(zoom);
        mMoved = getZoomOffset(zoom);
        if (mListener != null) {
            mListener.onZoomChanged(zoom);
        }
        invalidate();
    }

    public void setMaxZoom(float maxZoom) {
        mMaxZoom = maxZoom;
    }


    public void setScaleViewSlideListener(OnScaleViewSlideListener listener) {
        mListener = listener;
    }


    private int lineNumMathfun(float zoom, boolean up) {
        if (up) {
            return (int) Math.floor(accurateFloat(zoom));
        } else {
            return (int) Math.round(accurateFloat(zoom));
        }
    }

    private int getLineNum(float zoom) {
        return getLineNum(zoom, false);
    }

    private int getLineNum(float zoom, boolean up) {
        int result = 0;
        if (zoom <= 1.0f) {
            result = 0;
        } else if (zoom <= 2.0f) {
            result = lineNumMathfun((zoom - 1.0f) / 0.1f, up);
        } else if (zoom <= 3.0f) {
            result = 10 + lineNumMathfun((zoom - 2.0f) / 0.125f, up);
        } else if (zoom <= 6.0f) {
            result = 18 + lineNumMathfun((zoom - 3.0f) / 0.25f, up);
        } else if (zoom <= 7.0f) {
            result = 30 + lineNumMathfun((zoom - 6.0f) / 0.5f, up);
        } else if (zoom <= 8.0f) {
            result = 32 + lineNumMathfun(zoom - 7.0f, up);
        }
        return result;
    }


    private float getZoomOffset(float zoom) {
        float offset = 0;
        if (zoom <= mMinZoom) {
            offset = 0;
        } else if (zoom <= 2.0f) {
            offset = (zoom - mMinZoom) * 10 * mSpacing;
        } else if (zoom <= 3.0) {
            offset = (2.0f - mMinZoom) * 10 * mSpacing
                    + (zoom - 2.0f) * 10 * mSpacing / 1.25f;
        } else if (zoom <= 6.0) {
            offset = (2.0f - mMinZoom) * 10 * mSpacing
                    + (3.0f - 2.0f) * 10 * mSpacing / 1.25f
                    + (zoom - 3.0f) * 10 * mSpacing / 2.5f;
        } else if (zoom <= 7.0) {
            offset = (2.0f - mMinZoom) * 10 * mSpacing
                    + (3.0f - 2.0f) * 10 * mSpacing / 1.25f
                    + (6.0f - 3.0f) * 10 * mSpacing / 2.5f
                    + (zoom - 6.0f) * 10 * mSpacing / 5;
        } else if (zoom < 8.0) {
            offset = (2.0f - mMinZoom) * 10 * mSpacing
                    + (3.0f - 2.0f) * 10 * mSpacing / 1.25f
                    + (6.0f - 3.0f) * 10 * mSpacing / 2.5f
                    + (7.0f - 6.0f) * 10 * mSpacing / 5
                    + (zoom - 7.0f) * 10 * mSpacing / 10;
        } else {
            offset = (2.0f - mMinZoom) * 10 * mSpacing
                    + (3.0f - 2.0f) * 10 * mSpacing / 1.25f
                    + (6.0f - 3.0f) * 10 * mSpacing / 2.5f
                    + (7.0f - 6.0f) * 10 * mSpacing / 5
                    + (8.0f - 7.0f) * 10 * mSpacing / 10;
        }
        return -offset;
    }


    /**
     * 根据action_down 时的zoomValue 和移动距离算出 移动时的zoomvalue
     *
     * @param moving 移动的距离
     * @param round  是否向上四舍五入
     * @return
     */
    private float getZoomFromMoving(float moving, boolean round) {
        float movedZoom = 1.0f;
        float move = Math.abs(moving);
        float divisor = round ? 2.0f : 1.0f;
        if (move >= mSpacing * getLineNum(8.0f)) {
            movedZoom = 8.0f;
            move = 0;
        } else if (move >= mSpacing * getLineNum(7.0f)) {
            movedZoom = 7.0f;
            move -= mSpacing * getLineNum(7.0f);
        } else if (move >= mSpacing * getLineNum(6.0f)) {
            movedZoom = 6.0f;
            move -= mSpacing * getLineNum(6.0f);
        } else if (move >= mSpacing * getLineNum(3.0f)) {
            movedZoom = 3.0f;
            move -= mSpacing * getLineNum(3.0f);
        } else if (move >= mSpacing * getLineNum(2.0f)) {
            movedZoom = 2.0f;
            move -= mSpacing * getLineNum(2.0f);
        }
        while (true) {
            if (movedZoom < 2.0 && move > mSpacing / divisor) {
                move -= mSpacing;
                movedZoom += 0.1f;
            } else if (movedZoom >= 2.0 && movedZoom < 3.0 && move > mSpacing / 1.25f / divisor) {
                move -= mSpacing / 1.25f;
                movedZoom += 0.1f;

            } else if (movedZoom >= 3.0 && movedZoom < 6.0 && move > mSpacing / 2.5f / divisor) {
                move -= mSpacing / 2.5f;
                movedZoom += 0.1f;
            } else if (movedZoom >= 6.0 && movedZoom < 7.0 && move > mSpacing / 5 / divisor) {
                move -= mSpacing / 5f;
                movedZoom += 0.1f;

            } else if (movedZoom >= 7.0 && movedZoom < 8.0 && move > mSpacing / 10 / divisor) {
                move -= mSpacing / 10f;
                movedZoom += 0.1f;

            } else {
                break;
            }
        }

        if (movedZoom < 1.0f) {
            movedZoom = 1.0f;
        }
        if (movedZoom > 8.0f) {
            movedZoom = 8.0f;
        }
        return accurateFloat(movedZoom);
    }

    private float getZoomFromMoving(float moving) {
        return getZoomFromMoving(moving, true);
    }


    private void caculateFrameRate(boolean caculate) {
        if (!caculate) {
            return;
        }
        if (frameCount == 0) {
            startTime = System.currentTimeMillis();
        }
        frameCount++;
        if (System.currentTimeMillis() - startTime >= 1000) {
            Log.d(TAG, "frame rate is " + frameCount);
            frameCount = 0;
        }
    }

    public static float accurateFloat(float f, int scale, int roundMode) {
        BigDecimal bigDecimal = new BigDecimal(f);
        return bigDecimal.setScale(scale, roundMode).floatValue();
    }


    public static float accurateFloat(float f) {
        return accurateFloat(f, 1, BigDecimal.ROUND_HALF_UP);
    }

    public static int getCeilInt(float f) {
        BigDecimal bigDecimal = new BigDecimal(f);
        return bigDecimal.setScale(0, BigDecimal.ROUND_CEILING).intValue();
    }


}
