package com.example.llcgs.android_rx.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxlifecycle.ActivityLifeCycleEvent;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * com.example.llcgs.android_rx.rxjava.FourActivity
 *
 * @author liulongchao
 * @since 2017/6/12
 * <p>
 * RxJava 操作符--变换操作
 * <p>
 * 1.map
 * 2.flatmap
 * 3.concatmap
 * 3.switchmap
 * 4.scan
 * 5.groupby
 * 6.buffer
 * 7.window
 * 8.cast
 */


public class FourActivity extends BaseActivity {

    private String[] nbaArray = new String[]{
            "Jodn", "Kobe", "James", "Wade", "Durant"
    };
    private String[] ageArray = new String[]{
            "51", "36", "30", "32", "27"
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
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(FourActivity.this, FiveActivity.class));
            }
        });

        /**
         *  map 操作符对原始Observable发射的每一项数据应用一个你选择的函数，然后返回一个发射这些结果的Observable
         *   1.just 简单原样发射，将数组或者集合当作单个数据
         *   2.fromArray 逐一发射
         * */

        Observable.just(nbaArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<String[]>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .map(new Function<String[], String>() {
                    @Override
                    public String apply(@NonNull String[] o) throws Exception {
                        return o[1];
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String o) throws Exception {

                    }
            });


        Observable.fromArray(nbaArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String strings) throws Exception {
                        return strings + ", age:" + ageArray[Arrays.asList(nbaArray).indexOf(strings)];
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.d("MainActivity", "result:" + s);
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
         *  通过ObservableSource 可以切换到不同的线程中执行这些操作。这样就会产生无序发放的情况
         *
         *   flatMap() 和 map() 有一个相同点：它也是把传入的参数转化之后返回另一个对象。但需要注意，和 map() 不同的是， flatMap() 中返回的是个 Observable 对象，并且这个 Observable 对象并不是被直接发送到了 Subscriber 的回调方法中。
         *   flatMap() 的原理是这样的：
         *      1. 使用传入的事件对象创建一个 Observable 对象；
         *      2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
         *      3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法
         *
         * */
        Observable.fromArray(nbaArray)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String s) throws Exception {
                        return Observable.just("flatMap: " + s + ", age:" + ageArray[Arrays.asList(nbaArray).indexOf(s)]);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        // result
                        list.add(s);
                        adapter.setNewData(list);
                    }
                });

        /**
         * concatmap
         *  concatMap和flatMap最大的区别是concatMap发射的数据集是有序的，flatMap发射的数据集是无序的
         *
         * */
        Observable.fromArray(nbaArray)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .concatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String s) throws Exception {
                        return Observable.just("concatMap: " + s + ", age:" + ageArray[Arrays.asList(nbaArray).indexOf(s)]);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        list.add(s);
                        adapter.setNewData(list);
                    }
                });

        /**
         * switchmap
         *   和 flatMap  很像，除了一点：当原始Observable发射一个新的数据（Observable）时，它将取消订阅并停止监视产生执之前那个数据的Observable，只监视当前这一个
         *
         * */
        Observable.fromArray(nbaArray)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String s) throws Exception {
                        return Observable.just("switchMap: " + s + ", age:" + ageArray[Arrays.asList(nbaArray).indexOf(s)]);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        list.add(s);
                        adapter.setNewData(list);
                    }
                });

        /**
         * groupby
         *  它返回Observable的一个特殊子类 GroupedObservable  ，实现了 GroupedObservable  接口的对象有一个额外的方法 getKey  ，这个Key用于将数据分组到指定的Observable
         * */
        Observable.fromArray(nbaArray)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .groupBy(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        return "groupBy: " + s + ", age:" + ageArray[Arrays.asList(nbaArray).indexOf(s)];
                    }
                })
                .subscribe(new Consumer<GroupedObservable<String, String>>() {
                    @Override
                    public void accept(@NonNull GroupedObservable<String, String> stringStringGroupedObservable) throws Exception {
                        list.add(stringStringGroupedObservable.getKey());
                        adapter.setNewData(list);
                    }
                });

        /**
         * scan
         *  Scan  操作符对原始Observable发射的第一项数据应用一个函数，然后将那个函数的结果作为自己的第一项数据发射。它将函数的结果同第二项数据一起填充给这个函数来产生它自己的第二项数据
         * */
        Disposable disposable = Observable.fromArray(nbaArray)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .scan(new BiFunction<String, String, String>() {
                    @Override
                    public String apply(@NonNull String s, @NonNull String s2) throws Exception {
                        Log.d("MainActivity", "s:" + s + ", s2:" + s2);
                        return /*"scan:" + s + ", age:" + ageArray[Arrays.asList(nbaArray).indexOf(s)] + ", s2:"+s2*/ "scan:" + s + s2;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        list.add((String) s);
                        adapter.setNewData(list);
                    }
                });

        disposable.dispose();
    }

}
