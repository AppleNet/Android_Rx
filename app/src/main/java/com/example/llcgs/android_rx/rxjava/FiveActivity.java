package com.example.llcgs.android_rx.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.bean.User;
import com.example.llcgs.android_rx.rxlifecycle.ActivityLifeCycleEvent;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * com.example.llcgs.android_rx.rxjava.FiveActivity
 *
 * @author liulongchao
 * @since 2017/6/12
 *
 *  RxJava 操作符--过滤操作
 *  1.debounce
 *  2.distinct
 *  3.elementat
 *  4.filter
 *  5.first
 *  6.ignoreelements -- 不发射任何数据
 *  7.last
 *  8.sample
 *  9.skip 跳过前N项数据不发送
 *  10.skiplast 跳过最后N项数据不发送
 *  11.take 只发射前N项数据
 *  12.takelast 只发射后N项数据
 */


public class FiveActivity extends BaseActivity {

    private String[] nbaArray = new String[]{
            "Jodn","Kobe","James","McGrady","Answers","Wade","Durant"
    };

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList userList = new ArrayList<>();


    private Button button;
    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five);
        editText = (EditText) findViewById(R.id.editText3);
        button = (Button) findViewById(R.id.button2);

        RxView.clicks(button).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                startActivity(new Intent(FiveActivity.this, SixActivity.class));
            }
        });

        list.add("Jodn");
        list.add("Jodn");
        list.add("Kobe");
        list.add("Kobe");
        list.add("James");
        list.add("James");
        list.add("McGrady");
        list.add("McGrady");

        userList.add(new User());
        userList.add(new User("McGrady","32"));
        userList.add(new User("Kobe"));
        userList.add(new User("Kobe", "35"));
        userList.add(new User("35"));
        userList.add(35);

        // debounce 仅在过了一段指定的时间还没发射数据时才发射一个数据

        Observable.fromArray(nbaArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(1, TimeUnit.SECONDS)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object s) throws Exception {

                    }
                });

        // distinct 只允许还没有发射过的数据项通过，发射不重复的数据
        Observable.just(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinct()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(@NonNull Object o) throws Exception {
                        return "name" + o +",";
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        RxTextView.text(editText).accept(editText.getText()+","+o);
                    }
                });

        // elementAt 只发射第N项数据
        Observable.fromArray(nbaArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .elementAt(2)
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(@NonNull Object o) throws Exception {
                        return "the index of " + o +" is "+ list.indexOf(o);
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Toast.makeText(FiveActivity.this, "toast: " + o, Toast.LENGTH_SHORT).show();
                    }
                });

        // filter 只发射通过了谓词测试的数据项  ofType 是filter操作符的一个特殊形式。它过滤一个Observable只返回指定类型的数据
        Observable.just(userList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .ofType(User.class)
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(@NonNull Object o) throws Exception {
                        return ((User)o).getName().equals("Kobe");
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Log.d("MainActivity", "index: " + o.toString());
                    }
                });

        // first 只发射第一项数据  last只发射最后一项
        Observable.fromArray(nbaArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                /*.first(0)*/
                .last(nbaArray.length-1)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {

                    }
                });


    }
}
