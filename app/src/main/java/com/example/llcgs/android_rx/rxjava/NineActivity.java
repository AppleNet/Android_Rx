package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * com.example.llcgs.android_rx.rxjava.NineActivity
 *
 * @author liulongchao
 * @since 2017/6/23
 * * RxJava 操作符--条件和布尔操作
 * 1.all
 * 2.contains
 * 3.amb
 */


public class NineActivity extends BaseActivity {

    private static final String TAG = NineActivity.class.getSimpleName();

    private Integer[] array = new Integer[]{
            -5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nine);

        /*
        * All
        *   判定是否Observable发射的所有数据都满足某个条件
        *
        * */
        Observable.fromArray(array).all(new Predicate<Integer>() {
            @Override
            public boolean test(@NonNull Integer integer) throws Exception {
                return integer > 0;
            }
        }).map(new Function<Boolean, Integer>() {
            @Override
            public Integer apply(@NonNull Boolean aBoolean) throws Exception {
                return null;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {

            }
        });

        /*
        * amb
        *   给定两个或多个Observables，它只发射首先发射数据或通知的那个Observable的所有数据
        * */
        Observable<Integer> delay3 = Observable.just(1, 2, 3).delay(3000, TimeUnit.MILLISECONDS);
        Observable<Integer> delay2 = Observable.just(4, 5, 6).delay(2000, TimeUnit.MILLISECONDS);
        Observable<Integer> delay1 = Observable.just(7, 8, 9).delay(1000, TimeUnit.MILLISECONDS);

        Observable.ambArray(delay1, delay2, delay3).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.d(TAG, "integer=" + integer); // 7,8,9
            }
        });

        /*
        * contains
        *   判定一个Observable是否发射一个特定的值
        * */
        Observable.fromArray(array)
                // 如果原始Observable没有发射任何数据正常终止（以 onCompleted  d的形式）， DefaultIfEmpty  返回的Observable就发射一个你提供的默认值。
                .defaultIfEmpty(1)
                // 用于判断原始Observable是否没有发射任何数据
                //.isEmpty()
                .contains(10).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                Log.d(TAG, aBoolean ? "包含10" : "不包含10");
            }
        });

        /*
        * SequenceEqual
        *   判定两个Observables是否发射相同的数据序列
        *       如果两个序列是相同的（相同的数据，相同的顺序，相同的终止状态），它就发射true，否则发射false
        * */
        Observable.just("").sequenceEqual(new ObservableSource<String>() {
            @Override
            public void subscribe(@NonNull Observer<? super String> observer) {
                observer.onNext("NBA");
                observer.onComplete();
            }
        }, new ObservableSource<String>() {
            @Override
            public void subscribe(@NonNull Observer<? super String> observer) {
                observer.onNext("NBA");
                observer.onComplete();
            }
        }, new BiPredicate<String, String>() {
            @Override
            public boolean test(@NonNull String s, @NonNull String s2) throws Exception {
                return s.equals(s2);
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                Log.d(TAG, aBoolean ? "相同" : "不相同");
            }
        });

        /*
        * skipUntil
        *   丢弃原始Observable发射的数据，直到第二个Observable发射了一项数据,它开始发射原始Observable
        * */
        Observable.just("Wade").skipUntil(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("McGrady");
                e.onComplete();
            }
        })).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                addDisposable(disposable);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "skipUntil: "+s);
            }
        });

        /*
        * SkipWhile
        *   丢弃Observable发射的数据，直到一个指定的条件不成立
        * */
        Observable.just("Bosh").skipWhile(new Predicate<String>() {
            @Override
            public boolean test(@NonNull String s) throws Exception {
                return s.equals("James");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "SkipWhile:" + s);
            }
        });

        /*
        * TakeUntil
        *   订阅并开始发射原始Observable，它还监视你提供的第二个Observable。如果第二个Observable发射了一项数据或者发射了一个终止通知， TakeUntil 返回的Observable会停止发射原始Observable并终止
        * */
        Observable.fromArray("NBA").takeUntil(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onComplete();
            }
        })).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "TakeUntil: "+s);
            }
        });

        /*
        * TakeWhile
        *   发射Observable发射的数据，直到一个指定的条件不成立 它停止发射原始Observable，并终止自己的Observable
        * */
        Observable.just("Kobe").takeWhile(new Predicate<String>() {
            @Override
            public boolean test(@NonNull String s) throws Exception {
                return s.equals("Jodn");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "TakeWhile: "+s);
            }
        });

    }
}
