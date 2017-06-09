package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxbinding.MyObserver;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * com.example.llcgs.android_rx.rxjava.ThirdActivity
 *
 * @author liulongchao
 * @since 2017/6/9
 */


public class ThirdActivity extends AppCompatActivity {

    private String memoryCache = "memory";

    private TextView textview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        textview = (TextView) findViewById(R.id.textView2);

        Observable<String> memory = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d("MainActivity", "--------------memoryCache: " + memoryCache);
                if (!TextUtils.isEmpty(memoryCache)){
                    e.onNext(memoryCache);
                }else {
                    e.onComplete();
                }
            }
        });

        Observable<String> disk = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String cachePref = "";
                Log.d("MainActivity", "--------------cachePref: " + cachePref);
                if (!TextUtils.isEmpty(cachePref)) {
                    e.onNext(cachePref);
                } else {
                    e.onComplete();
                }
            }
        });

        Observable<String> network = Observable.just("netWork");

        /**
         * 依次检查memory、disk和network中是否存在 memory 数据，任何一步一旦发现数据后面的操作都不执行
         *
         *  memory这个Observable中发现了memory 所以后续终止 不执行
         *
         * */
        Observable.concat(memory, disk, network)
                .first("memory")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.d("MainActivity", "--------------subscribe: " + s);
                        RxTextView.text(textview).accept(s);
                    }
                });

        /**
         *  “x秒后执行y操作 使用timer
         *
         * */
        RxView.clicks(textview)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //.timer(2, TimeUnit.SECONDS)
                .subscribe(new MyObserver<Object>() {
                    @Override
                    public void onNext(@NonNull Object aLong) {
                        Log.d("MainActivity", "aLong: "+aLong);
                        try {
                            Toast.makeText(ThirdActivity.this, "2秒了", Toast.LENGTH_SHORT).show();
                            RxTextView.text(textview).accept(String.valueOf(aLong));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
