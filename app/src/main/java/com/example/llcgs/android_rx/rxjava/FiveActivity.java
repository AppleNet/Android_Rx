package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;

/**
 * com.example.llcgs.android_rx.rxjava.FiveActivity
 *
 * @author liulongchao
 * @since 2017/6/12
 */


public class FiveActivity extends BaseActivity {

    private String[] nbaArray = new String[]{
            "","","",""
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five);

    }
}
