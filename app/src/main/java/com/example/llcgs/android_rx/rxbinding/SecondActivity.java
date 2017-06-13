package com.example.llcgs.android_rx.rxbinding;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;
import com.example.llcgs.android_rx.rxbus.RxBus;
import com.example.llcgs.android_rx.rxbus.event.UserEvent;
import com.example.llcgs.android_rx.rxjava.ThirdActivity;
import com.jakewharton.rxbinding2.support.design.widget.RxTextInputLayout;
import com.jakewharton.rxbinding2.widget.RxTextSwitcher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * com.example.llcgs.android_rx.rxbinding.SecondActivity
 *
 * @author liulongchao
 * @since 2017/6/8
 */


public class SecondActivity extends BaseActivity implements ViewSwitcher.ViewFactory {

    private TextSwitcher textSwitcher;
    final String[] array = new String[]{"wade","jodn","james"};
    private int curStr;

    private TextInputLayout userNameInput;
    private TextInputLayout userPwdInput;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initViews();
        try {
            initRx();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews(){
        textSwitcher = (TextSwitcher) findViewById(R.id.textswitcher);
        textSwitcher.setFactory(this);
        userNameInput = (TextInputLayout) findViewById(R.id.userNameInput);
        userPwdInput = (TextInputLayout) findViewById(R.id.userPwdInput);
    }

    private void initRx() throws Exception {
        RxTextInputLayout.hint(userNameInput).accept("用户名");
        RxTextInputLayout.hint(userPwdInput).accept("密码");
        next(null);
        Observable.interval(1, TimeUnit.SECONDS)
                .take(array.length)
                // 订阅
                .subscribeOn(Schedulers.io())
                // 观察
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<Long>() {
                    @Override
                    public void onNext(@NonNull Long aLong) {
                        try {
                            if(aLong == 0){
                                RxTextSwitcher.text(textSwitcher).accept(array[0]);
                            } else if (aLong == 1){
                                RxTextSwitcher.text(textSwitcher).accept(array[1]);
                            } else if (aLong == 2){
                                RxTextSwitcher.text(textSwitcher).accept(array[2]);
                            }
                            UserEvent mcGrady = new UserEvent("McGrady", "32");
                            RxBus.getInstance().post(mcGrady);
                            Log.d("MainActivity", "userEvent:" + mcGrady.getName() + "," + mcGrady.getPwd());
                            if (aLong == 2){
                                startActivity(new Intent(SecondActivity.this, ThirdActivity.class));
                            }
                            //SecondActivity.this.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public void next(View view){
        textSwitcher.setText(array[curStr++ % array.length]);
    }

    @Override
    public View makeView() {
        TextView textView=new TextView(SecondActivity.this);
        textView.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.BLUE);
        return textView;
    }
}
