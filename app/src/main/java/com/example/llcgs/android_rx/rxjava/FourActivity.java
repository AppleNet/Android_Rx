package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.llcgs.android_rx.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * com.example.llcgs.android_rx.rxjava.FourActivity
 *
 * @author liulongchao
 * @since 2017/6/12
 *
 * RxJava 操作符--变换操作
 *
 *  1.map
 *  2.flatmap
 *  3.concatmap
 *  3.switchmap
 *  4.scan
 *  5.groupby
 *  6.buffer
 *  7.window
 *  8.cast
 */


public class FourActivity extends AppCompatActivity {

    private String [] nbaArray = new String[]{
            "Jodn","Kobe","James","Wade","Durant"
    };
    private String [] ageArray = new String[]{
            "51","36","30","32","27"
    };

    private RecyclerView recyclerView;
    private MyAdapter adapter = new MyAdapter();
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter.addData(list);
        recyclerView.setAdapter(adapter);

        /**
         *  map 操作符对原始Observable发射的每一项数据应用一个你选择的函数，然后返回一个发射这些结果的Observable
         *   1.just 简单原样发射，将数组或者集合当作单个数据
         *   2.fromArray 逐一发射
         * */

        Observable.just(nbaArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<String[], String>() {
                    @Override
                    public String apply(@NonNull String[] strings) throws Exception {
                        //
                        return strings[0];
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {

                    }
                });

        Observable.fromArray(nbaArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        return s + ", age:" + ageArray[Arrays.asList(nbaArray).indexOf(s)];
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.d("MainActivity", "result:"+s);
                        list.add(s);
                        adapter.setNewData(list);
                    }
                });

        /***
         * flatmap 将一个发射数据的Observable变换为多个Observables，然后将它们发射的数据合并后放进一个单独的Observable
         *  无序发送
         *  flatMap发送的是合并后的Observables，map操作符发送的是应用函数后返回的结果集
         *  如果任何一个通过这个 flatMap  操作产生的单独的Observable调用 onError  异常终止了，这个Observable自身会立即调用 onError  并终止
         *
         * */
        Observable.fromArray(nbaArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String s) throws Exception {
                        return Observable.just("flatMap: "+s + ", age:" + ageArray[Arrays.asList(nbaArray).indexOf(s)]);
                    }
                }, true)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        // result
                        list.add(s);
                        adapter.setNewData(list);
                    }
                });
    }
}
