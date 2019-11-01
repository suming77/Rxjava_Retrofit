package com.example.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;

/**
 * RxJava2的简单使用(四)
 * 我的博客：https://blog.csdn.net/m0_37796683
 */
public class RxJavaActivity04 extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RxJavaActivity04.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava04);

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.btn_filter).setOnClickListener(this);
        findViewById(R.id.btn_ofType).setOnClickListener(this);
        findViewById(R.id.btn_elementAt).setOnClickListener(this);
        findViewById(R.id.btn_distinct).setOnClickListener(this);
        findViewById(R.id.btn_debounce).setOnClickListener(this);
        findViewById(R.id.btn_first).setOnClickListener(this);
        findViewById(R.id.btn_last).setOnClickListener(this);
        findViewById(R.id.btn_skip).setOnClickListener(this);
        findViewById(R.id.btn_take).setOnClickListener(this);
        findViewById(R.id.btn_groupBy).setOnClickListener(this);
        findViewById(R.id.btn_cast).setOnClickListener(this);
        findViewById(R.id.btn_scan).setOnClickListener(this);
        findViewById(R.id.btn_join).setOnClickListener(this);
        findViewById(R.id.btn_groupJoin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back://返回
                finish();
                break;
            case R.id.btn_filter://
                filter();
                break;
            case R.id.btn_ofType://
                ofType();
                break;
            case R.id.btn_elementAt://
                elementAt();
                break;
            case R.id.btn_distinct://
                distinct();
                break;
            case R.id.btn_debounce://
                debounce();
                break;
            case R.id.btn_first://
                first();
                break;
            case R.id.btn_last://
                last();
                break;
            case R.id.btn_skip://
                skip();
                break;
            case R.id.btn_take://
                take();
                break;
            case R.id.btn_groupBy://
                groupBy();
                break;
            case R.id.btn_cast://
                cast();
                break;
            case R.id.btn_scan://
                scan();
                break;
            case R.id.btn_join://
                join();
                break;
            case R.id.btn_groupJoin://
                groupJoin();
                break;
            default:
                break;
        }
    }

    /**
     * filter():对源Observable产生的结果按照执行条件过虑，只有满足条件才会提交给订阅者
     */
    private void filter() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        //条件：能否被2整除
                        return integer % 2 == 0;
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "filter：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "filter：onNext == " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "filter：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "filter：onComplete == ");
            }
        });
    }

    /**
     * ofType():根据类型对源Observable(被观察者)产生的结果进行过滤，只有满足条件才会提交给观察者。
     */
    private void ofType() {
        Observable.just(1, "大闸蟹", true, 0.23f, 5L, new Goods("商品名"))
                .ofType(Goods.class).subscribe(new Observer<Goods>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "ofType：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Goods goods) {
                Log.e(TAG, "ofType：onNext == " + goods.getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "ofType：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "ofType：onComplete == ");
            }
        });
    }

    /**
     * elementAt():把源Observable（被观察者）生产的结果中，只提交索引指示的结果给Observer（观察者）
     */
    private void elementAt() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .elementAt(7)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "elementAt：accept == " + integer);
                    }
                });
    }


    /**
     * distinct()：对源Observable的结果进行过滤，去掉重复出现的结果，只输出不重复的结果给Observer观察者，简单来说就是去重。
     */
    private void distinct() {
        Observable.just(1, 2, 2, 3, 4, 4, 4)
                .distinct()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "distinct：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "distinct：onNext == " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "distinct：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "distinct：onComplete == ");
                    }
                });
    }

    /**
     * debounce():只接收到倒计时时间外的源Observable(被观察者)发出的事件，每次发送的事件都会重置倒计时时间。
     */
    private void debounce() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                try {//每隔0,100,200……900毫秒发送的数据为0,1,2……9
                    for (int i = 0; i < 10; i++) {
                        e.onNext(i);
                        Thread.sleep(i * 100);
                    }
                } catch (Exception exception) {
                    e.onError(exception);
                }
            }
        }).debounce(300, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "debounce：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "debounce：onNext ==" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "debounce：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "debounce：onComplete == ");
                    }
                });
    }

    /**
     * first():将源Observable(被观察者)产生的第一次结果提交给Observer(被观察者)，first()可以用elementAt(0)和take(1)替代。
     */
    private void first() {
        Observable.just(1, 2, 3)
                .first(0)//defaultItem：默认值
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "first：accept ==" + integer);
                    }
                });
    }

    /**
     * last():与first()相对，将源Observable(被观察者)产生的最后一次结果提交给Observer(被观察者)
     */
    private void last() {
        Observable.just(1, 2, 3, 4)
                .last(-1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "last：accept ==" + integer);
                    }
                });
    }

    /**
     * skip():针对源Observable(被观察者)产生的结果跳过N个不处理，而把后面的结果提交给Observer(观察者)处理。
     */
    private void skip() {
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .skip(3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "skip：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "skip：onNext ==" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "skip：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "skip：onComplete == ");
                    }
                });
    }

    /**
     * take():与skip()相对，把前面n个源Observable(被观察者)产生的结果提交给Observer(观察者)处理。
     */
    private void take() {
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .take(3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "take：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "take：onNext ==" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "take：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "take：onComplete == ");
                    }
                });
    }


    /**
     * groupBy():对于源Observable产生的结果进行分组，形成一个类型为GroupedObservable的结果集，
     * GroupedObservable存在一个方法为getKey()，通过该方法获取结果集的key值。
     * <p>
     * 注意：由于结果集GroupedObservable把分组结果缓存起来，如果对每个GroupedObservable都不进行处理
     * （既不订阅也没有别的操作）就有可能出现内存泄漏，如果某个GroupedObservable不处理那可以使用take(0)处理掉。
     */
    private void groupBy() {
        //每隔一秒发送一次事件，一共发送五次
        Observable.interval(1, TimeUnit.SECONDS)
                .take(5)
                .groupBy(new Function<Long, Object>() {
                    @Override
                    public Object apply(Long aLong) throws Exception {
                        return aLong * 10;
                    }
                }).subscribe(new Observer<GroupedObservable<Object, Long>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "groupBy：onSubscribe == 订阅");
            }

            @Override
            public void onNext(GroupedObservable<Object, Long> objectLongGroupedObservable) {
                Log.e(TAG, "groupBy：onNext == key:" + objectLongGroupedObservable.getKey());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "groupBy：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "groupBy：onComplete == ");
            }
        });
    }

    /**
     * cast():主要是用于类型转换的，传入的参数类型为.class,如果源Observable的源类型不能转为指定的.class,
     * 则会抛出ClassCastException类型转换异常。
     */
    private void cast() {
        Observable.just(1, 2, 3, 4, 5, 6)
                .cast(Integer.class)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "cast：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "cast：onNext == " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "cast：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "cast：onComplete == ");
                    }
                });
    }

    /**
     * scan():通过遍历源Observable产生的结果，依次每个结果按照指定的规则进行计算，计算后的结果作为下一项迭代的参数，
     * 每一次迭代项都会把计算结果输出给订阅者。
     */
    private void scan() {
        Observable.just(1, 2, 3)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer sum, Integer integer) throws Exception {
                        //sum是上次计算的记过，integer是本次计算的参数
                        Log.e(TAG, "scan：apply == sum + integer = " + sum + " + " + integer);
                        return sum + integer;
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "scan：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "scan：onNext == " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "scan：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "scan：onComplete == ");
            }
        });
    }

    /**
     * join():类似combineLatest(),把两个Observable产生的进行合并，合并成的结果组成一个新的Observable，
     * 但是join()操作符，可以控制每个Observable产生结果的生命周期，在每个结果的生命周期内，
     * 可以与另一个Observable产生的结果按照一定的规则进行合并。
     */
    private void join() {
        //每隔一秒产生0,5,10,15,20事件队列
        Observable<Long> observable1 = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 5;
                    }
                }).take(5);

        //延时500毫秒，每隔一秒，产生0,10,20,30,40 事件队列
        Observable<Long> observable2 = Observable.interval(500, 1000, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return aLong * 10;
                    }
                }).take(5);


        observable1.join(observable2, new Function<Long, ObservableSource<Long>>() {
            @Override
            public ObservableSource<Long> apply(Long aLong) {
                //使Observable延时600毫秒执行(控制observable1的生命周期)
                return Observable.just(aLong).delay(600, TimeUnit.MILLISECONDS);
            }
        }, new Function<Long, ObservableSource<Long>>() {
            @Override
            public ObservableSource<Long> apply(Long aLong) {
                //使Observable延时600毫秒执行(控制observable2的生命周期)
                return Observable.just(aLong).delay(600, TimeUnit.MILLISECONDS);
            }
        }, new BiFunction<Long, Long, Long>() {
            @Override
            public Long apply(Long aLong, Long aLong2) {
                //合并逻辑
                Log.e(TAG, "BiFunction：apply == aLong1 + aLong2：" + aLong + " + " + aLong2);
                return aLong + aLong2;
            }
        }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "join：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "join：onNext == " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "join：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "join：onComplete == ");
            }
        });
    }

    /**
     * groupJoin():类似于join()，也是把两个Observable产生的结果进行合并，组成一个新的Observable，
     * 可以控制每个Observable产生结果的生命周期，在每个结果的生命周期内，与另一个Observable的结果进行合
     * 并按照一定的规则进行合并。与join()不同的是第四个参数传入的函数不一致。
     */
    private void groupJoin() {
        //每隔1秒，产生0,5,10,15,20事件队列
        Observable<Long> observable1 = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return aLong * 5;
                    }
                }).take(5);

        //延迟0.5秒，每秒产生0,10,20,30,40事件队列
        Observable<Long> observable2 = Observable.interval(500, 1000, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        return aLong * 10;
                    }
                }).take(5);

        observable1.groupJoin(observable2, new Function<Long, ObservableSource<Long>>() {
            @Override
            public ObservableSource<Long> apply(Long aLong) {
                //使Observable延时1600毫秒执行(控制observable1的生命周期)
                return Observable.just(aLong).delay(1600, TimeUnit.MILLISECONDS);
            }
        }, new Function<Long, ObservableSource<Long>>() {
            @Override
            public ObservableSource<Long> apply(Long aLong) {
                //使Observable延时600毫秒执行(控制observable2的生命周期)
                return Observable.just(aLong).delay(600, TimeUnit.MILLISECONDS);
            }
        }, new BiFunction<Long, Observable<Long>, Observable<Long>>() {
            @Override
            public Observable<Long> apply(final Long aLong, Observable<Long> longObservable) {
                return longObservable.map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong2) {
                        //合并逻辑
                        Log.e(TAG, "BiFunction：apply == aLong1 + aLong2：" + aLong + " + " + aLong2);
                        return aLong + aLong2;
                    }
                });
            }
        }).subscribe(new Observer<Observable<Long>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "groupJoin：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Observable<Long> longObservable) {
                longObservable.subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        Log.e(TAG, "groupJoin：onNext == " + aLong);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "groupJoin：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "groupJoin：onComplete == ");
            }
        });
    }
}
