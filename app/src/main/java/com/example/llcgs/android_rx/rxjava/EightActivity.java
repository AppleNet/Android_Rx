package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxbinding.MyObserver;
import com.example.llcgs.android_rx.rxlifecycle.ActivityLifeCycleEvent;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;

/**
 * com.example.llcgs.android_rx.rxjava.EightActivity
 *
 * @author liulongchao
 * @since 2017/6/20
 * <p>
 * RxJava操作符--辅助操作
 * 1.Delay
 * 2.DO
 * 3.Materialize/Dematerialize
 * 4.ObserverOn
 * 5.Serialize
 * 6.Subscribe
 * 7.SubscribeOn
 * 8.TimeInterval
 * 9.Timeout
 * 10.Timestamp
 * 11.Using
 * 12.To
 */
public class EightActivity extends BaseActivity {

    private static final String TAG = EightActivity.class.getSimpleName();

    private String[] nbaArray = new String[]{
            "Jodn", "Wade", "James", "Bosh", "Kobe", "McGrady", "answer"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight);

        /**
         * Delay
         *  延迟一段指定的时间再发射来自Observable的发射物
         * Delay操作符让原始Observable在发射每项数据之前都暂停一段指定的时间段。
         * 效果是Observable发射的数据项在时间上向前整体平移了一个增量
         *
         * 注意：delay不会平移onError通知，它会立即将这个通知传递给订阅者，同时丢弃任何待发射的onNext通知。
         * 然而它会平移一个onCompleted通知。
         *
         * */
        Log.d(TAG, "currentTime:" + System.currentTimeMillis());
        Observable.just("Jordon", "Wade", "James", "Bosh", "Kobe", "McGrady", "answer")
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                // 延迟两秒发射
                .delay(2, TimeUnit.SECONDS)
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String o) throws Exception {
                        return Observable.just(o);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String o) throws Exception {
                        Log.d(TAG, "currentTime:" + System.currentTimeMillis());
                        Log.d(TAG, "nba star：" + o);
                    }
                });

        /**
         * Do
         *  注册一个动作作为原始Observable生命周期事件的一种占位符
         *
         *  doOnEach：为 Observable注册这样一个回调，当Observable没发射一项数据就会调用它一次，包括onNext、onError和 onCompleted
         *  doOnNext：只有执行onNext的时候会被调用
         *  doOnSubscribe： 当观察者（Sunscriber）订阅Observable时就会被调用
         *  doOnUnsubscribe： 当观察者取消订阅Observable时就会被调用；Observable通过onError或者onCompleted结束时，会反订阅所有的Subscriber
         *  doOnCompleted：当Observable 正常终止调用onCompleted时会被调用。
         *  doOnError： 当Observable 异常终止调用onError时会被调用。
         *  doOnTerminate： 当Observable 终止之前会被调用，无论是正常还是异常终止
         *  finallyDo： 当Observable 终止之后会被调用，无论是正常还是异常终止
         *
         * */
        Observable.fromArray(nbaArray)
                // 当观察者取消订阅Observable时就会被调用；Observable通过onError或者onCompleted结束时，会反订阅所有的Subscriber
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String o) throws Exception {
                        Log.d(TAG, "nba star:doOnSubscribe-" + o);
                    }
                });

        /**
         * Materialize/Dematerialize 感觉没什么卵用
         *     materialize将来自原始Observable的通知（onNext/onError/onComplete）都转换为一个Notification对象，然后再按原来的顺序一次发射出去。
         *     Dematerialize操作符是Materialize的逆向过程，它将Materialize转换的结果还原成它原本的形式（ 将Notification对象还原成Observable的通知）
         * */
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("where amazing happens");
                e.onComplete();
            }
        })
                .materialize()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<Notification<String>>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Notification<String>>() {
                    @Override
                    public void accept(@NonNull Notification<String> stringNotification) throws Exception {
                        Log.d(TAG, "座右铭：" + ((Notification) stringNotification).getValue());
                    }
                });

        /**
         * Serialize
         *   强制一个Observable连续调用并保证行为正确且是同步的
         *   ""
         * */
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("Where");
                e.onNext("amazing");
                e.onComplete();
                e.onNext("happens");
                e.onComplete();
            }

        })      // 同步执行？？？？
                .serialize()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .subscribeOn(Schedulers.io())
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.d(TAG, "s:" + s);
                    }
                });

        /**
         * TimeInterval
         *  将一个发射数据的Observable转换为发射那些数据发射时间间隔的Observable
         * */
        Observable.fromArray(nbaArray)
                .timeInterval()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<Timed<String>>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Timed<String>>() {
                    @Override
                    public void accept(@NonNull Timed<String> stringTimed) throws Exception {
                        Log.d(TAG, "value: " + stringTimed.value() + ", time: " + stringTimed.time() + ", unit: " + stringTimed.unit());
                    }
                });

        /**
         * Timeout
         *    对原始Observable的一个镜像，如果过了一个指定的时长仍没有发射数据，它会发一个错误通知
         * */
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("play football");
                //Thread.sleep(4000);
                e.onNext("paly baseketball");
                e.onComplete();
            }

        })      // 休眠了四秒 再发射paly baseketball timeout限制的是3秒 所以 onError发射通知结束
                .timeout(3, TimeUnit.SECONDS)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<String>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onNext(@NonNull String s) {
                        Log.d(TAG, "s: " + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                });

        /**
         * Timestamp
         *  给Observable发射的数据项附加一个时间戳
         * */
        Observable.fromIterable(Arrays.asList(nbaArray))
                .timestamp()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<Timed<String>>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Timed<String>>() {
                    @Override
                    public void accept(@NonNull Timed<String> stringTimed) throws Exception {
                        Log.d(TAG, "timestamp():" + "value: " + stringTimed.value() + ", time: " + stringTimed.time() + ", unit: " + stringTimed.unit());
                    }
                });

        /**
         * using
         *  创建一个只在Observable生命周期内存在的一次性资源
         *   before using被 use using冲掉了
         * */
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("before using");
            }
        }).using(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "use using";
            }
        }, new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull String s) throws Exception {
                return Observable.just("s: " + s);
            }
            // 订阅到的是new Callable<String>() 发射的结果
        }, new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "using accept:" + s);
            }
            // 订阅到的是 Function<String, ObservableSource<String>>() 这个Observable发射的结果
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "subscribe accept:" + s);
            }
        });

        /**
         * to
         *  --toList
         *   将Observable转换为另一个对象或数据结构
         * */
        Observable.fromArray(nbaArray)
                // 将数组转换成List
                .toList()
                .map(new Function<List<String>, Map<Integer, String>>() {
                    @Override
                    public Map<Integer, String> apply(@NonNull List<String> strings) throws Exception {
                        Map<Integer, String> items = new HashMap<>();
                        for (String s : strings) {
                            items.put(Arrays.asList(nbaArray).indexOf(s), "NBA All-Star:" + s);
                        }
                        return items;
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<Map<Integer, String>>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<Integer, String>>() {
                    @Override
                    public void accept(@NonNull Map<Integer, String> integerStringMap) throws Exception {

                        Set<Integer> integers = integerStringMap.keySet();
                        for (Integer integer : integers) {
                            Log.d(TAG, "toList-integerStringMap.keySet(): " + integerStringMap.get(integer));
                        }

                        Set<Map.Entry<Integer, String>> entries = integerStringMap.entrySet();
                        for (Map.Entry<Integer, String> next : entries) {
                            Log.d(TAG, "toList-integerStringMap.entrySet(): " + next.getKey() + "," + next.getValue());
                        }

                        for (String s : integerStringMap.values()) {
                            Log.d(TAG, "toList: " + s);
                        }
                    }
                });

        /**
         * to
         *  --getIterator
         *  getIterator  操作符只能用于 BlockingObservable  的子类，要使用它，你首先必须把原始的Observable转换为一个 BlockingObservable
         *
         * */
        Observable.fromArray(nbaArray).blockingIterable();

        /**
         * to
         *  --toMap 收集原始Observable发射的所有数据项到一个Map（默认是HashMap）然后发射这个Map。你可以提供一个用于生成Map的Key的函数，还可以提供一个函数转换数据项到Map存储的value值
         *
         * */
        Observable.fromArray(nbaArray)
                .toMap(/**生成key函数*/new Function<String, Integer>() {
                    @Override
                    public Integer apply(@NonNull String s) throws Exception {
                        return Arrays.asList(nbaArray).indexOf(s);
                    }
                }, /**生成value函数*/new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        return "NBA All-Star: " + s;
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .subscribeOn(Schedulers.io())
                .compose(this.<Map<Integer, String>>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<Integer, String>>() {
                    @Override
                    public void accept(@NonNull Map<Integer, String> integerStringMap) throws Exception {
                        for (String s : integerStringMap.values()) {
                            Log.d(TAG, "toList: " + s);
                        }
                    }
                });

        /**
         * to
         *  --toMultiMap 类似于 toMap  ，不同的是，它生成的这个Map同时还是一个 ArrayList
         * */
        Observable.fromArray(nbaArray)
                .toMultimap(new Function<String, Integer>() {
                    @Override
                    public Integer apply(@NonNull String s) throws Exception {
                        return null;
                    }
                }, new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        return null;
                    }
                }, new Callable<Map<Integer, Collection<String>>>() {
                    @Override
                    public Map<Integer, Collection<String>> call() throws Exception {
                        return null;
                    }
                }, new Function<Integer, Collection<? super String>>() {
                    @Override
                    public Collection<? super String> apply(@NonNull Integer integer) throws Exception {
                        return null;
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(this.<Map<Integer, Collection<String>>>bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<Integer, Collection<String>>>() {
                    @Override
                    public void accept(@NonNull Map<Integer, Collection<String>> integerCollectionMap) throws Exception {
                        // key int value collection
                    }
                });

    }
}
