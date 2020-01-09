package com.example.customizeviewdev;

import android.content.Context;
import android.widget.TextView;

public class Test {
    private static Test sInstance;  // 单例

    private Context mContext;  // 单例持有Context
    private TextView mTextView;  // 单例持有视图控件

    private Test(Context context) {
        mContext = context;  // 容易导致内存泄漏
//        mContext = context.getApplicationContext();  // 正确写法
    }

    public static Test getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Test(context);
        }
        return sInstance;
    }

    // 持有视图控件，容易产生内存泄露
    public void setRetainedTextView(TextView tv) {
        mTextView = tv;
        mTextView.setText("hello");
    }

    // 删除引用，防止泄露
    public void removeRetainedTextView() {
        mTextView = null;
    }
}