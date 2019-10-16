package com.example.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * RxJava2的简单使用
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "主线程==id:" + Thread.currentThread().getId());

        //1、创建被观察者Observable
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("RxJava：e.onNext== 第一次");
                e.onNext("RxJava：e.onNext== 第二次");
                e.onNext("RxJava：e.onNext== 第三次");
                Log.e(TAG, "subscribe()==执行事件的线程==id:" + Thread.currentThread().getId());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())//指定subscribe()(发射事件的线程)在IO线程()
                .observeOn(AndroidSchedulers.mainThread());//指定订阅者接收事件的线程在主线程;

        //2、创建观察者Observer
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe == 订阅");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext == " + s);
                Log.e(TAG, "onNext()==回调事件的线程==id:" + Thread.currentThread().getId());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete == ");
            }
        };

        //3、订阅(观察者观察被观察者)
        observable.subscribe(observer);
    }
}
