package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxbinding.MyObserver;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * com.example.llcgs.android_rx.rxjava.SevenActivity
 *
 * @author liulongchao
 * @since 2017/6/16
 * <p>
 * RxJava 操作符--错误处理
 * 1.catch
 * 2.retry
 */


public class SevenActivity extends BaseActivity {

    private static final String TAG = SevenActivity.class.getSimpleName();

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
            public void subscribe(@NonNull ObservableEmitter<Integer> subscriber) {
                for (int i = 0; i < 10; i++) {
                    if (i > 3) {
                        //会忽略onError调用，不会将错误传递给观察者
                        subscriber.onError(new Throwable("i太大了"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onComplete();
            }
        }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(@NonNull Throwable throwable) {
                //作为替代，它会发发射一个特殊的项并调用观察者的onCompleted方法。
                //  将i太大了打印出来
                //throwable.printStackTrace();
                return 10;
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new MyObserver<Integer>() {
            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onErrorReturn_onNext:" + "" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onErrorReturn_onError" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onErrorReturn_Complete");
            }
        });

        /**
         * onErrorResumeNext(Observable) 让Observable在遇到错误时开始发射第二个Observable的数据序列
         *
         * 当原Observable发射onError消息时，会忽略onError消息，不会传递给观察者；
         * 然后它会开始另一个备用的Observable，继续发射数据
         *
         * */
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<Integer> subscriber) throws Exception {
//                for(int i = 0;i < 10; i++){
//                    if(i>3){
//                        //会忽略onError调用，不会将错误传递给观察者
//                        subscriber.onError(new Throwable("i太大了"));
//                    }
//                    subscriber.onNext(i);
//                }
//                subscriber.onComplete();
//            }
//        }).onErrorResumeNext(new ObservableSource<Integer>() {
//            @Override
//            public void subscribe(@NonNull Observer<? super Integer> observer) {
//                for (int i = 10; i < 13; i++){
//                    observer.onNext(i);
//                }
//                observer.onComplete();
//            }
//        }).subscribe(new MyObserver<Integer>() {
//            @Override
//            public void onNext(@NonNull Integer integer) {
//                Log.d(TAG, "onErrorResumeNext_Observable_onNext:" + integer);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//                Log.d(TAG, "onErrorResumeNext_Observable_onError:" + e.getMessage());
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onErrorResumeNext_Observable_onComplete:" + "onComplete");
//            }
//        });
//
//        /**
//         * onErrorResumeNext(Func1):
//         * 和onErrorResumeNext(Observable)相似，但他能截取到原Observable的onError消息
//         *
//         * */
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<Integer> subscriber) throws Exception {
//                for(int i = 0;i < 10; i++){
//                    if(i>3){
//                        //会忽略onError调用，不会将错误传递给观察者
//                        subscriber.onError(new Throwable("i太大了"));
//                    }
//                    subscriber.onNext(i);
//                }
//                subscriber.onComplete();
//            }
//        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
//            @Override
//            public ObservableSource<? extends Integer> apply(@NonNull Throwable throwable) throws Exception {
//                return null;
//            }
//        }).subscribe(new MyObserver<Integer>() {
//            @Override
//            public void onNext(@NonNull Integer integer) {
//                Log.d(TAG, "onErrorResumeNext_Function_onNext:" + integer);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//                Log.d(TAG, "onErrorResumeNext_Function_onError:" + e.getMessage());
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onErrorResumeNext_onComplete：" + "onComplete");
//            }
//        });
//
//        /**
//         * onExceptionResumeNext：
//         *    和onErrorResumeNext类似，可以说是onErrorResumeNext的特例，
//         *    区别是如果onError收到的Throwable不是一个Exception，它会将错误传递给观察者的onError方法，不会使用备用的Observable。
//         *
//         * */
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<Integer> subscriber) throws Exception {
//                for(int i = 0;i < 10; i++){
//                    if(i>3){
//                        //会忽略onError调用，不会将错误传递给观察者
//                        subscriber.onError(new Throwable("i太大了"));
//                    }
//                    subscriber.onNext(i);
//                }
//                subscriber.onComplete();
//            }
//        }).onExceptionResumeNext(new ObservableSource<Integer>() {
//            @Override
//            public void subscribe(@NonNull Observer<? super Integer> observer) {
//                for (int i = 10; i < 13; i++){
//                    observer.onNext(i);
//                }
//                observer.onComplete();
//            }
//        }).subscribe(new MyObserver<Integer>() {
//            @Override
//            public void onNext(@NonNull Integer integer) {
//
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//            }
//
//            @Override
//            public void onComplete() {
//            }
//        });
//
//        /**
//         * Retry
//         *  如果原始Observable遇到错误，重新订阅它期望它能正常终止
//         *  ①. retry()
//         *     无限次尝试重新订阅
//         *  ②. retry(count)
//         *     最多count次尝试重新订阅
//         *  ③. retry(Func2)
//         * */
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
//
//            }
//        }).retry()
//          .subscribe(new MyObserver<Integer>() {
//            @Override
//            public void onNext(@NonNull Integer integer) {
//
//            }
//        });

        /**
         * retryWhen
         *  retryWhen和retry类似，区别是，retryWhen将onError中的Throwable传递给一个函数，这个函数产生另一个Observable，retryWhen观察它的结果再决定是不是要重新订阅原始的Observable。
         *  如果这个Observable发射了一项数据，它就重新订阅，如果这个Observable发射的是onError通知，它就将这个通知传递给观察者然后终止。
         *
         * */


    }
}
