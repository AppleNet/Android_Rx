package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxbinding.MyObserver;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * com.example.llcgs.android_rx.rxjava.SevenActivity
 *
 * @author liulongchao
 * @since 2017/6/16
 *
 * RxJava 操作符--错误处理
 *  1.catch
 *  2.retry
 *
 */


public class SevenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);

        /**
         *  onErrorReturn 让Observable遇到错误时发射一个特殊的项并且正常终止
         *  后者会忽略前者的onError调用，不会将错误传递给观察者，作为替代，它会发发射一个特殊的项并调用观察者的onCompleted方法
         *
         * */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> subscriber) throws Exception {
                for(int i = 0;i < 10; i++){
                    if(i>3){
                        //会忽略onError调用，不会将错误传递给观察者
                        subscriber.onError(new Throwable("i太大了"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onComplete();
            }
        }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(@NonNull Throwable throwable) throws Exception {
                //作为替代，它会发发射一个特殊的项并调用观察者的onCompleted方法。
                //  将i太大了打印出来
                throwable.printStackTrace();
                return 10;
            }
        }).subscribe(new MyObserver<Integer>() {
            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d("SevenActivity-onNext:", "" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("SevenActivity-onError:", "" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("SevenActivity-Complete:", "Complete");
            }
        });

        /**
         * onErrorResumeNext(Observable) 让Observable在遇到错误时开始发射第二个Observable的数据序列
         *
         * 当原Observable发射onError消息时，会忽略onError消息，不会传递给观察者；
         * 然后它会开始另一个备用的Observable，继续发射数据
         *
         * */
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> subscriber) throws Exception {
                for(int i = 0;i < 10; i++){
                    if(i>3){
                        //会忽略onError调用，不会将错误传递给观察者
                        subscriber.onError(new Throwable("i太大了"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onComplete();
            }
        }).onErrorResumeNext(new ObservableSource<Integer>() {
            @Override
            public void subscribe(@NonNull Observer<? super Integer> observer) {
                for (int i = 10; i < 13; i++){
                    observer.onNext(i);
                }
                observer.onComplete();
            }
        }).subscribe(new MyObserver<Integer>() {
            @Override
            public void onNext(@NonNull Integer integer) {

            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {

            }
        });

        /**
         * onErrorResumeNext(Func1):
         * 和onErrorResumeNext(Observable)相似，但他能截取到原Observable的onError消息
         *
         * */


    }
}
