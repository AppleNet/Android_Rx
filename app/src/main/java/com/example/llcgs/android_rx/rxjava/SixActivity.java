package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;

/**
 * com.example.llcgs.android_rx.rxjava.SixActivity
 *
 * @author liulongchao
 * @since 2017/6/13
 *
 * RxJava 操作符--结合操作
 *
 * 1.and/then/when-- 感觉很鸡肋
 * 2.combinelatest
 * 3.join
 * 4.merge
 * 5.startwith
 * 6.switch
 * 8.zip
 */


public class SixActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six);


    }
}
