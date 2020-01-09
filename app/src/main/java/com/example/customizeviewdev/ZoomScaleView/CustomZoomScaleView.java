package com.example.customizeviewdev.ZoomScaleView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.customizeviewdev.R;

import androidx.annotation.Nullable;

public class CustomZoomScaleView  extends LinearLayout  {

    private Context mContext;
    private ScaleView mScaleView;
    private TextView mTvZoom;

    private boolean mIsOpen = false;
    private OnZoomChangeListener mZoomChangeListener;


    public CustomZoomScaleView(Context context) {
        this(context, null);
    }

    public CustomZoomScaleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomZoomScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.custom_zoom_view_layout, this);
        mScaleView = findViewById(R.id.zoom_scale_view);
        mScaleView.setScaleViewSlideListener(new ScaleView.OnScaleViewSlideListener() {
            @Override
            public void onZoomChanged(float value) {
                if (mTvZoom != null) {
                    mTvZoom.setText(getZoomStr(value));
                    if (mZoomChangeListener != null) {
                        mZoomChangeListener.onZoomChange(value);
                    }
                }
            }

            @Override
            public void changeState(boolean onMove) {
                if(mZoomChangeListener !=null) {
                    mZoomChangeListener.changeState(onMove);
                }
            }
        });
        mTvZoom = findViewById(R.id.tv_zoom_value);
        mTvZoom.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsOpen) {
                    mScaleView.setVisibility(GONE);
                    mIsOpen = false;
                } else {
                    mScaleView.setVisibility(VISIBLE);
                    mIsOpen = true;
                }
            }
        });


    }

    public void setOrientation(int orientation, boolean animation) {
        if (mTvZoom != null) {
            mTvZoom.setRotation(-orientation);
        }
    }

    public void setZoomMax(Float zoomMax) {
        if (mScaleView != null) {
            mScaleView.setMaxZoom(zoomMax);
        }
    }

    public void reSet() {
        if (mTvZoom != null) {
            mTvZoom.setText("1X");
        }
        if (mScaleView != null) {
            mScaleView.setZoomValue(1.0f);
        }
    }


    public void setOnZoomChangeListener(OnZoomChangeListener onZoomChangeListener) {
        mZoomChangeListener = onZoomChangeListener;
    }

    public void setZoom(float zoomValue) {
        if (mTvZoom != null) {
            mTvZoom.setText(getZoomStr(zoomValue));
        }
        if (mScaleView != null) {
            mScaleView.setZoomValue(zoomValue);
        }
    }

    public interface OnZoomChangeListener {
        void onZoomChange(float change);

        void changeState(boolean move);
    }

    public void hideScaleView() {
        mScaleView.setVisibility(GONE);
        mIsOpen = false;
    }

    public String getZoomStr(float value) {
        String zoomStr = String.valueOf(ScaleView.accurateFloat(value));
        if (zoomStr.length() >= 3) {
            char c = zoomStr.charAt(2);
            if (c == '0') {
                zoomStr = zoomStr.charAt(0) + "X";
            }
        } else if (zoomStr.length() == 1) {
            zoomStr = zoomStr + "X";
        }
        return zoomStr;

    }


}
