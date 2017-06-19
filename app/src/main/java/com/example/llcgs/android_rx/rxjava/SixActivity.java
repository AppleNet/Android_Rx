package com.example.llcgs.android_rx.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxbinding.MyObserver;
import com.example.llcgs.android_rx.rxbus.RxBus;
import com.example.llcgs.android_rx.rxbus.event.UserEvent;
import com.example.llcgs.android_rx.rxlifecycle.ActivityLifeCycleEvent;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;

/**
 * com.example.llcgs.android_rx.rxjava.SixActivity
 *
 * @author liulongchao
 * @since 2017/6/13
 * <p>
 * RxJava 操作符--结合操作
 * <p>
 * 1.and/then/when-- 感觉很鸡肋
 * 2.combinelatest
 * 3.join
 * 4.merge
 * 5.startwith
 * 6.switch
 * 8.zip
 */


public class SixActivity extends BaseActivity {

    private String[] nbaArray = null;
    private EditText editText4, editText5, editText6, editText7, editText8;
    private Button button3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six);

        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        editText6 = (EditText) findViewById(R.id.editText6);
        editText7 = (EditText) findViewById(R.id.editText7);
        editText8 = (EditText) findViewById(R.id.editText8);

        button3 = (Button) findViewById(R.id.button3);

        RxBus.getInstance().tObservable(UserEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        UserEvent userEvent = (UserEvent) o;
                        nbaArray = userEvent.getArray();
                    }
                });

        /**
         *  combineLatest
         *    当两个Observables中的任何一个发射了数据时，使用一个函数结合每个Observable发射的最近数据项，并且基于这个函数的结果发射数据
         *
         * */
        Observable.combineLatest(RxTextView.textChanges(editText4), RxTextView.textChanges(editText5),
                RxTextView.textChanges(editText6), RxTextView.textChanges(editText7),
                new Function4<CharSequence, CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull CharSequence charSequence, @NonNull CharSequence charSequence2, @NonNull CharSequence charSequence3, @NonNull CharSequence charSequence4) throws Exception {

                        return !TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(charSequence2) && !TextUtils.isEmpty(charSequence3) && !TextUtils.isEmpty(charSequence4);
                    }
                }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //

                }
            }
        });

        /**
         *  join
         *   join操作符把类似于combineLatest操作符，也是两个Observable产生的结果进行合并，合并的结果组成一个新的Observable，
         *   但是join操作符可以控制每个Observable产生结果的生命周期，在每个结果的生命周期内，可以与另一个Observable产生的结果按照一定的规则进行合并
         * */
        RxTextView.textChanges(editText4).join(RxTextView.textChanges(editText5), new Function<CharSequence, Observable<String>>() {
            @Override
            public Observable<String> apply(@NonNull CharSequence charSequence) throws Exception {
                return Observable.just(charSequence.toString());
            }
        }, new Function<CharSequence, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull CharSequence charSequence) throws Exception {
                return Observable.just(charSequence.toString());
            }
        }, new BiFunction<CharSequence, CharSequence, Object>() {
            @Override
            public Object apply(@NonNull CharSequence charSequence, @NonNull CharSequence charSequence2) throws Exception {
                return charSequence + "-" + charSequence2;
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                RxTextView.text(editText8).accept(o.toString());
            }
        });

        /**
         *  merge mergeWith mergeDelayError
         *   合并多个Observables的发射物
         *   merge  是静态方法， mergeWith  是对象方法，举个例子， Observable.merge(odds,evens)  等价于 odds.mergeWith(evens)  。
         *   如果传递给 merge  的任何一个的Observable发射了 onError  通知终止了， merge  操作符生成的Observable也会立即以 onError  通知终止。如果你想让它继续发射数据，在最后才报告错误，可以使用 mergeDelayError
         *   merge 交错发射 concat 顺序发射
         * */
        ArrayList<Observable<Object>> list = new ArrayList<>();
        list.add(RxView.clicks(editText4));
        list.add(RxView.clicks(editText5));
        list.add(RxView.clicks(editText6));
        list.add(RxView.clicks(editText7));
        Observable.merge(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Log.d("SixActivity", "merge: " + o.toString());
                    }
                });

        /**
         * startWith
         *  在数据序列的开头插入一条指定的项
         *
         * */
        Observable.fromArray(nbaArray)
                .startWith("NBA")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Log.d("SixActivity", "startWith: " + o.toString());
                    }
                });

        /**
         *  switch
         *      将一个发射多个小Observable的源Observable转化为一个Observable，然后发射这多个小Observable所发射的数据。
         *      当原始Observable发射了一个新的Observable时（不是这个新的Observable发射了一条数据时），它将取消订阅之前的那个Observable。
         *      这意味着，在后来那个Observable产生之后到它开始发射数据之前的这段时间里，前一个Observable发射的数据将被丢弃
         *
         *      Observable.just产生之后到它开始发射数据之前的这段时间里 Observable.create发射的数据将被丢弃
         *
         * */
        Observable.switchOnNext(Observable.create(new ObservableOnSubscribe<ObservableSource<String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ObservableSource<String>> e) throws Exception {
                e.onNext(Observable.just("where amazing happens"));
                e.onComplete();
            }
        })).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                Log.d("SixActivity", "switchOnNext:" + o.toString());
            }
        });

        /**
         *  zip
         *    通过一个函数将多个Observables的发射物结合到一起，基于这个函数的结果为每个结合体发射单个数据项
         *    Zip  操作符返回一个Obversable，它使用这个函数按顺序结合两个或多个Observables发射的数据项，然后它发射这个函数返回的结果。
         *    它按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据
         *
         * */
        Observable.zip(Observable.fromArray(nbaArray), Observable.just(0, 1, 2, 3, 4, 5, 6), new BiFunction<String, Integer, Boolean>() {
            @Override
            public Boolean apply(@NonNull String s, @NonNull Integer integer) throws Exception {
                return Arrays.asList(nbaArray).indexOf(s) == integer;
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSubscribe(new Consumer<Disposable>() {
              @Override
              public void accept(@NonNull Disposable disposable) throws Exception {
                  addDisposable(disposable);
              }
          })
          .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
          .flatMap(new Function<Object, ObservableSource<String>>() {
              @Override
              public ObservableSource<String> apply(@NonNull Object aBoolean) throws Exception {
                  boolean flag = (boolean) aBoolean;
                  if(flag){
                      return Observable.just("compare success");
                  }
                  return Observable.error(new Throwable("compare fail"));
              }
          }).subscribe(new MyObserver<String>() {
              @Override
              public void onNext(@NonNull String s) {
                  Log.d("SixActivity", "zip:" + s);
              }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("SixActivity", "zip-error:" + e.getMessage());
            }
        });

        //
        RxView.clicks(button3).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                startActivity(new Intent(SixActivity.this, SevenActivity.class));
            }
        });

    }
}
