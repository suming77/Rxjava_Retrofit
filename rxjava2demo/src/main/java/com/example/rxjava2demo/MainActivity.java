package com.example.rxjava2demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * RxJava2的简单使用(一)
 * 我的博客：https://blog.csdn.net/m0_37796683
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "主线程==id:" + Thread.currentThread().getId());


        findViewById(R.id.btn_rxjava2).setOnClickListener(this);
        findViewById(R.id.btn_create).setOnClickListener(this);
        findViewById(R.id.btn_empty).setOnClickListener(this);
        findViewById(R.id.btn_error).setOnClickListener(this);
        findViewById(R.id.btn_never).setOnClickListener(this);
        findViewById(R.id.btn_just).setOnClickListener(this);
        findViewById(R.id.btn_fromArray).setOnClickListener(this);
        findViewById(R.id.btn_fromIterable).setOnClickListener(this);
        findViewById(R.id.btn_defer).setOnClickListener(this);
        findViewById(R.id.btn_timer).setOnClickListener(this);
        findViewById(R.id.btn_interval).setOnClickListener(this);
        findViewById(R.id.btn_intervalRange).setOnClickListener(this);
        findViewById(R.id.btn_range).setOnClickListener(this);

        //观察者不对被观察者发送的事件做出响应(但是被观察者还可以继续发送事件)
        //public final Disposable subscribe()

        //观察者对被观察者发送的任何事件都做出响应
        //public final void subscribe(Observer<? super T> observer)

        //表示观察者只对被观察者发送的Next事件做出响应
        //public final Disposable subscribe(Consumer<? super T> onNext)

        //表示观察者只对被观察者发送的Next & Error事件做出响应
        //public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError)

        //表示观察者只对被观察者发送的Next & Error & Complete事件做出响应
        //public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError,
        //Action onComplete)

        //表示观察者只对被观察者发送的Next & Error & Complete & onSubscribe事件做出响应
        //public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError,
        //Action onComplete, Consumer<? super Disposable> onSubscribe)
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rxjava2://RxJava2的简单使用(二)
                startActivity(new Intent(this, MainActivity02.class));
                break;
            case R.id.btn_create://
                create();
                break;
            case R.id.btn_empty://
                empty();
                break;
            case R.id.btn_error://
                error();
                break;
            case R.id.btn_never://
                never();
                break;
            case R.id.btn_just://
                just();
                break;
            case R.id.btn_fromArray://
                fromArray();
                break;
            case R.id.btn_fromIterable://
                fromIterable();
                break;
            case R.id.btn_defer://
                defer();
                break;
            case R.id.btn_timer://
                timer();
                break;
            case R.id.btn_interval://
                interval();
                break;
            case R.id.btn_intervalRange://
                intervalRange();
                break;
            case R.id.btn_range://
                range();
                break;
            default:
                break;
        }
    }

    /**
     * 基本创建
     * Observable.create
     * RxJava创建被观察者对象的最基本操作符
     */
    private void create() {
        //1、创建被观察者Observable
        //当 Observable 被订阅时，OnSubscribe 的 call() 方法会自动被调用，即事件序列就会依照设定依次被触发
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 通过 ObservableEmitter类对象产生事件并通知观察者
                //即观察者会依次调用对应事件的复写方法从而响应事件
                e.onNext("RxJava：e.onNext== 第一次");
                e.onNext("RxJava：e.onNext== 第二次");
                e.onNext("RxJava：e.onNext== 第三次");
                Log.e(TAG, "subscribe()==执行事件的线程==id:" + Thread.currentThread().getId());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())//指定subscribe()(发射事件的线程)在IO线程()
                .observeOn(AndroidSchedulers.mainThread());//指定订阅者接收事件的线程在主线程;

        //2、创建观察者Observer  并且定义响应事件
        Observer<String> observer = new Observer<String>() {
            //默认最先复写onSubscribe()
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

    private void empty() {
        //快速创建被观察者对象，仅发送onComplete()事件，直接通知完成。
        Observable.empty()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "empty：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "empty：onNext ==" + o.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "empty：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "empty：onComplete == ");
                    }
                });
    }

    private void error() {
        //快速创建被观察者对象，仅发送onError()事件，直接通知异常。
        Observable.error(new Throwable("只回调error"))
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "error：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "error：onNext ==" + o.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "error：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "error：onComplete == ");
                    }
                });
    }

    private void never() {
        //快速创建被观察者对象，不发送任何事件。
        Observable.never()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "never：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "never：onNext ==" + o.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "never：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "never：onComplete == ");
                    }
                });
    }

    /**
     * 通过just()创建传入任意类型的参数构建Observable被观察者，相当于执行了onNext(1)~onNext(5),通过链式编程订阅观察者
     */
    private void just() {
        Observable.just(1, 2, 3, 4, 5).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "just：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "just：onNext == " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "just：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "just：onComplete == ");
            }
        });
    }

    private void fromArray() {
        //设置需要传入的数组
        String[] strings = {"商品类", "非商品类"};
        //传入数组，被观察者创建后会将数组转换成Observable并且发送里面所有的数据
        Observable.fromArray(strings).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "fromArray：onSubscribe == 订阅");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "fromArray：onNext == " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "fromArray：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "fromArray：onComplete == ");
            }
        });
    }

    private void fromIterable() {
        //创建集合
        List<Goods> list = new ArrayList();
        for (int i = 0; i < 3; i++) {
            Goods g = new Goods("名称" + i);
            list.add(g);
        }
        //传入集合，被观察者创建后会将数组转换成Observable并且发送里面所有的数据
        Observable.fromIterable(list).subscribe(new Observer<Goods>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "fromIterable：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Goods goods) {
                Log.e(TAG, "fromIterable：onNext == " + goods.getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "fromIterable：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "fromIterable：onComplete == ");
            }
        });
    }

    //1.初始化i
    Integer i = 100;

    /**
     * 知道有观察者调用时，才动态创建被观察者对象并且发送事件
     */
    private void defer() {
        //2.通过defer()定义被观察者（此时被观察者对象还没创建）
        Observable<Integer> defer = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(i);
            }
        });

        //3.重新设置i值
        i = 200;

        //4.订阅观察者（此时才会调用defer,创建被观察者对象）
        defer.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "defer：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Integer i) {
                Log.e(TAG, "defer：onNext == " + i);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "defer：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "defer：onComplete == ");
            }
        });
    }

    private void timer() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.e(TAG, "timer：当前时间 ==" + dateFormat.format(System.currentTimeMillis()));
        Observable.timer(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "timer：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "timer：onNext ==" + aLong + "   时间 ==" + dateFormat.format(System.currentTimeMillis()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "timer：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "timer：onComplete == ");
                    }
                });
    }

    private void interval() {
        //initialDelay：表示延迟开始的时间, period：距离下一次发送事件的时间间隔, unit：时间单位

        //interval(long initialDelay, long period, TimeUnit unit)
        //在指定的延迟时间后，每隔多少时间发送一次事件，可以指定调度器
        //interval(long initialDelay, long period, TimeUnit unit, Scheduler scheduler)
        //每间隔多少时间发送一次事件，使用默认的线程
        //Observable<Long> interval(long period, TimeUnit unit)
        //每间隔多少时间发送一次事件，可以指定调度器
        //interval(long period, TimeUnit unit, Scheduler scheduler)

        Observable.interval(3, 1, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "interval：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "interval：onNext ==" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "interval：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "interval：onComplete == ");
                    }
                });
    }

    private void intervalRange() {
        //start:事件开始的数值大小，count：发送事件的次数，initialDelay：表示延迟开始的时间,
        // period：距离下一次发送事件的时间间隔, unit：时间单位，scheduler：调度器
        //intervalRange(long start, long count, long initialDelay, long period, TimeUnit unit)
        //intervalRange(long start, long count, long initialDelay, long period, TimeUnit unit, Scheduler scheduler)
        Observable.intervalRange(10, 5, 3, 1, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "intervalRange：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "intervalRange：onNext ==" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "intervalRange：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "intervalRange：onComplete == ");
                    }
                });
    }

    private void range() {
        //start：事件的开始值大小，count：发送的事件次数
        Observable.range(5, 4).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "range：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "range：onNext ==" + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "range：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "range：onComplete == ");
            }
        });
    }

}
