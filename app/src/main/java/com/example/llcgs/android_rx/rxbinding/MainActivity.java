package com.example.llcgs.android_rx.rxbinding;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxbus.RxBus;
import com.example.llcgs.android_rx.rxbus.event.UserEvent;
import com.example.llcgs.android_rx.rxjava.EightActivity;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 *  RxJava与RxAndroid的区别在于 RxAndroid增加了主线程回调
 *  通过额外封装的AndroidSchedulers 和 HandlerScheduler extends Scheduler
 *    HandlerScheduler中封装了handler 通过handler发送消息给主线程，
 *    实现了Android的设计原理，子线程发消息通知主线程更新UI
 * */

public class MainActivity extends BaseActivity {

    private Button button;
    private CheckBox checkBox;
    private EditText editText;
    private EditText editText2;
    private TextView textView;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initRxClickListener();

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        button = (Button) findViewById(R.id.button);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        textView = (TextView) findViewById(R.id.textView);
    }

    private void initRxClickListener() {


        RxBus.getInstance().tObservable(UserEvent.class)
                //在io线程进行订阅，可以执行一些耗时操作
                .subscribeOn(Schedulers.io())
                // 在主线程进行观察，可做UI更新操作
                .observeOn(AndroidSchedulers.mainThread())
                // 以下两种方式 都可以接收发送过来的UserEvent
//                .subscribe(new Consumer<UserEvent>() {
//                    @Override
//                    public void accept(@NonNull UserEvent userEvent) throws Exception {
//
//                    }
//                })
                .subscribe(new MyObserver<UserEvent>() {
                    @Override
                    public void onNext(@NonNull UserEvent userEvent) {
                        Log.d("MainActivity", "userEvent:"+userEvent.getName() + "," + userEvent.getPwd());
                        try {
                            RxTextView.text(editText).accept(userEvent.getName());
                            RxTextView.text(editText2).accept(userEvent.getPwd());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        RxView.clicks(button).throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        RxView.enabled(button).accept(false);
                    }
                }).subscribe(new MyObserver<Object>() {
            @Override
            public void onNext(@NonNull Object o) {
                Toast.makeText(MainActivity.this, "只能点击一次, 开始倒计时", Toast.LENGTH_SHORT).show();
                Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                        .take(10)
                        .subscribe(new MyObserver<Long>() {
                            @Override
                            public void onNext(@NonNull Long aLong) {
                                try {
                                    RxTextView.text(textView).accept("remian: " + (10 - aLong));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });

        RxCompoundButton.checkedChanges(checkBox)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean o) {
                        try {
                            RxView.enabled(button).accept(o);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        RxTextView.textChanges(editText)
                .flatMap(new Function<CharSequence, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull CharSequence charSequence) throws Exception {
                        String ch = charSequence.toString();
                        if (ch.startsWith("a")) {
                            ch = "m" + ch;
                        }
                        return Observable.just(ch);
                    }
                }).subscribe(new MyObserver<String>() {
            @Override
            public void onNext(@NonNull String s) {
                textView.setText(s);
            }
        });

        // 监听多个edittext
        Observable.combineLatest(RxTextView.textChanges(editText), RxTextView.textChanges(editText2), new BiFunction<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(@NonNull CharSequence name, @NonNull CharSequence password) throws Exception {
                Log.d("MainActivity", "name:"+name.toString());
                Log.d("MainActivity", "password:"+password.toString());
                return (name.toString().equalsIgnoreCase("jdon") && password.toString().equalsIgnoreCase("123456"));
            }
        }).subscribe(new MyObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                Log.d("MainActivity", "aBoolean: "+aBoolean);
                if(aBoolean){
                    //Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    //Intent intent = new Intent(MainActivity.this, SevenActivity.class);
                    Intent intent = new Intent(MainActivity.this, EightActivity.class);
                    startActivity(intent);
                }
            }
        });

        RxView.clicks(fab).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<Object>() {
                    @Override
                    public void onNext(@NonNull Object o) {
                        Snackbar.make(fab, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
