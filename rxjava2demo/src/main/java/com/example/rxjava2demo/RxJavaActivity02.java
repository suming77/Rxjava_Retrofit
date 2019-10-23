package com.example.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * RxJava2的简单使用(二)
 * 我的博客：https://blog.csdn.net/m0_37796683
 */
public class RxJavaActivity02 extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RxJavaActivity02.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava02);

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.btn_map).setOnClickListener(this);
        findViewById(R.id.btn_flatMap).setOnClickListener(this);
        findViewById(R.id.btn_concatMap).setOnClickListener(this);
        findViewById(R.id.btn_buffer).setOnClickListener(this);
        findViewById(R.id.btn_concatAndConcatArray).setOnClickListener(this);
        findViewById(R.id.btn_merge).setOnClickListener(this);
        findViewById(R.id.btn_concatDelayError).setOnClickListener(this);
        findViewById(R.id.btn_mergeDelayError).setOnClickListener(this);
        findViewById(R.id.btn_startWithAndStartWithArray).setOnClickListener(this);
        findViewById(R.id.btn_zip).setOnClickListener(this);
        findViewById(R.id.btn_combineLatest).setOnClickListener(this);
        findViewById(R.id.btn_reduce).setOnClickListener(this);
        findViewById(R.id.btn_collect).setOnClickListener(this);
        findViewById(R.id.btn_count).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back://返回
                finish();
                break;
            case R.id.btn_map://
                map();
                break;
            case R.id.btn_flatMap://
                flatMap();
                break;
            case R.id.btn_concatMap://
                concatMap();
                break;
            case R.id.btn_buffer://
                buffer();
                break;
            case R.id.btn_concatAndConcatArray://
                concatAndConcatArray();
                break;
            case R.id.btn_merge://
                merge();
                break;
            case R.id.btn_concatDelayError://
                concatDelayError();
                break;
            case R.id.btn_mergeDelayError://
                mergeDelayError();
                break;
            case R.id.btn_startWithAndStartWithArray://
                startWithAndStartWithArray();
                break;
            case R.id.btn_zip://
                zip();
                break;
            case R.id.btn_combineLatest://
                combineLatest();
                break;
            case R.id.btn_reduce://
                reduce();
                break;
            case R.id.btn_collect://
                collect();
                break;
            case R.id.btn_count://
                count();
                break;
            default:
                break;
        }
    }

    /**
     * map():对被观察者发送的每个事件都通过指定函数的处理，从而转变成另一种事件。
     */
    private void map() {
        //链式编程
        Observable.just(1, 2, 3, 4, 5)
                //使用Map操作符中的Function函数对被观察者发送的事件统一作出处理
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        //对被观察者just发送的结果，都全部乘以10处理
                        return integer * 10;
                    }
                }).subscribe(new Consumer<Integer>() {//订阅
            @Override
            public void accept(Integer integer) throws Exception {//接受事件结果，是处理后的结果
                Log.e(TAG, "map：accept == " + integer);
            }
        });
    }

    /**
     * flatMap():将Observable(被观察者)产生的结果拆分和单独转换变成多个Observable，
     * 然后把多个Observable“扁平化”整合成新的一个Observable，并依次提交产生的结果给订阅者。
     */
    private void flatMap() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) {
                e.onNext("A");
                e.onNext("B");
                e.onNext("C");
                e.onComplete();
            }
        }).flatMap(new Function<String, ObservableSource<String>>() {
            @Override//通过flatMap将被观察者生产的事件进行拆分，再将新的事件转换成一个新的Observable发送
            public ObservableSource<String> apply(String s) {
                List<String> list = new ArrayList<>();
                Log.e(TAG, "flatMap：apply == 事件" + s);
                //将一个事件拆分成两个子事件，例如将A事件拆分成A0,A1两个事件，然后再整个合成一个Observable通过fromIterable发送给订阅者
                for (int j = 0; j < 5; j++) {
                    list.add("拆分后的子事件" + s + j);
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                Log.e(TAG, "flatMap：accept == " + s);
            }
        });
    }

    /**
     * concatMap():与flatMap()类似，把Observable产生的事件转换成多个Observable(被观察者)，
     * 然后把Observable整合成一个Observable，并依次提交生产的结果给订阅者。
     */
    private void concatMap() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) {
                e.onNext("A");
                e.onNext("B");
                e.onComplete();
            }
        }).concatMap(new Function<String, ObservableSource<String>>() {
            @Override//通过flatMap将被观察者生产的事件进行拆分，再将新的事件转换成一个新的Observable发送
            public ObservableSource<String> apply(String s) {
                List<String> strings = new ArrayList<>();
                Log.e(TAG, "concatMap：apply == 事件" + s);
                for (int j = 0; j < 2; j++) {
                    strings.add("拆分后的子事件" + s + j);
                }
                return Observable.fromIterable(strings);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                Log.e(TAG, "concatMap：accept == " + s);
            }
        });
    }

    /**
     * buffer()：将Observable(被观察者)需要发送的事件周期性收集到列表中，并把这列表提交给Observer(订阅者)，
     * 订阅者处理后，清空buffer列表，同时接收下一次的结果交给订阅者，周而复始。
     */
    private void buffer() {
        //缓存事件时，在被观察者中第一次获取3个事件数放到缓存区域，即"A"，"B"，"C"，发送给订阅者；
        //第二次获取时，因为步长=2，所以事件往后移动2个，即指针往后移动2位，从"C"开始取三个事件，即"C","D","E"发送给订阅者；
        //第三次获取时，事件再在第二次的基础上往后移动2个，即到了"E"，取三个事件，事件不足，只能够取"E"发送给订阅者
        Observable.just("A", "B", "C", "D", "E")//这里演示发送5个事件
                .buffer(3, 2)//缓存列表数量为3个，步长为2
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "buffer：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        Log.e(TAG, "buffer：onNext == 缓存事件数：" + strings.size());
                        for (int j = 0; j < strings.size(); j++) {
                            Log.e(TAG, "buffer：子事件==" + strings.get(j));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "buffer：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "buffer：onComplete == ");
                    }
                });
    }

    /**
     * concat()与concatArray():组合多个被观察者的一起发送数据，合并后安先后顺序执行。
     * 区别：concat()的观察者数量最多是4个，而concatArray()的个数没有限制
     */
    private void concatAndConcatArray() {
        Observable.concat(Observable.just(1, 2), Observable.just(3, 4), Observable.just(5, 6), Observable.just(7, 8))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "concat：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "concat：onNext ==" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "concat：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "concat：onComplete == ");
                    }
                });

        Observable.concatArray(Observable.just("一", "二"), Observable.just("三", "四"),
                Observable.just("五", "六"), Observable.just("七", "八"), Observable.just("九", "十"))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "concatArray：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "concatArray：onNext ==" + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "concatArray：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "concatArray：onComplete == ");
                    }
                });
    }

    /**
     * Merge()与MergeArray()组合多个被观察者发送数据，按照时间先后顺序来执行。
     * 与concat()与concatArray()组合类似
     */
    private void merge() {
        //起始值为1，发送3个事件，第一个事件延迟1秒发送，事件间隔为1秒
        Observable<Long> observable1 = Observable.intervalRange(1, 3, 1, 1, TimeUnit.SECONDS);
        //起始值为10，发送3个事件，第一个事件延迟1秒发送，事件间隔为1秒
        Observable<Long> observable2 = Observable.intervalRange(10, 3, 1, 1, TimeUnit.SECONDS);

        Observable.merge(observable1, observable2)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "merge：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "merge：onNext ==" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "merge：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "merge：onComplete == ");
                    }
                });
    }

    /**
     * concat()与concatArray()组合中，如果某一个被观察者发出onError()事件，则会马上停止其他事件的发送。
     * 如果需要onError()事件推迟到其他事件发送完成才出发的话则需要用到concatDelayError()方法
     */
    private void concatDelayError() {
        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("第一个事件");
                e.onNext("第二个事件");
                e.onError(new Exception("中途抛出异常"));
                e.onComplete();
            }
        });
        Observable<String> observable2 = Observable.just("第三个事件");

        //中途抛出异常列子：
        Observable.concat(observable1, observable2).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "concatDelayError：onSubscribe == 订阅");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "concatDelayError：onNext ==" + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "concatDelayError：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "concatDelayError：onComplete == ");
            }
        });

        //中途抛出异常列子：
        Observable.concatArrayDelayError(observable1, observable2).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "concatDelayError：onSubscribe == 订阅");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "concatDelayError：onNext ==" + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "concatDelayError：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "concatDelayError：onComplete == ");
            }
        });
    }

    /**
     * mergeDelayError()把错误放到所有结果都合并完成之后才执行
     */
    private void mergeDelayError() {
        //创建被观察者-异常
        Observable<Object> errorObservable = Observable.error(new Exception("抛出异常"));

        //产生0,2,4的事件序列，每隔1秒发送事件，一共发送3次
        Observable<Object> observable1 = Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(new Function<Long, Object>() {
                    @Override
                    public Object apply(Long aLong) throws Exception {
                        return aLong * 2;
                    }
                }).take(3)/*.mergeWith(errorObservable.delay(4, TimeUnit.SECONDS))*/;

        //产生0,10,20的事件序列，每隔1秒发送事件，一共发送3次，
        Observable<Long> observable2 = Observable.interval(1, 1, TimeUnit.SECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 10;
                    }
                }).take(3);

        Observable.mergeDelayError(observable1, errorObservable, observable2)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "mergeDelayError：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "mergeDelayError：onNext ==" + o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "mergeDelayError：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "mergeDelayError：onComplete == ");
                    }
                });

    }

    /**
     * startWith():在源Observable提交结果之前，插入指定的某些数据
     * 注意调用顺序：先调用最后加入的数据
     */
    private void startWithAndStartWithArray() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Array:1");
        Observable.just("一", "二", "三", "四")
                .startWith(strings)//插入单个集合
                .startWith("startWith:2")//插入单个数据
                .startWithArray("startWithArray:3", "startWithArray:4")//插入多个数据
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "startWith：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "startWith：onNext 结果== " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "startWith：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "startWith：onComplete == ");
                    }
                });
    }


    /**
     * zip():把两个Observable提交的结果，严格按照顺序进行合并，最终合并的数量等于多个Observable数量最少的数量。
     */
    private void zip() {
        //设置需要传入的被观察者数据
        Observable<Integer> observable1 = Observable.just(1, 2, 3, 4);
        Observable<String> observable2 = Observable.just("A", "B", "C");

        //回调apply()方法，并在里面自定义合并结果的逻辑
        // BiFunction<Integer, String, String>，第一个类型为observable1的参数类型，第二个类型为observable2的参数类型，第三个为合并后的参数类型
        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String str) throws Exception {
                return integer + str;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "zip：onSubscribe == 订阅");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "zip：onNext == " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "zip：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "zip：onComplete == ");
            }
        });
    }


    /**
     * combineLatest():将两个Observable产生的结果进行合并，合并的结果将组成一个新的Observable发送给订阅者。
     * 两个Observable中任意的一个Observable产生的结果，都与另一个Observable最后的结果，按照一定的规则进行合并。
     */
    private void combineLatest() {
        //产生0,10,20的事件序列，每隔1秒发送事件，一共发送3次
        Observable<Long> observable1 = Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 10;
                    }
                }).take(3);

        //产生0,1,2,3,4的事件序列，起始值为0，一共发送4次，延迟1秒后开始发送，每隔1秒发送事件
        Observable<Long> observable2 = Observable.intervalRange(0, 4, 1, 1, TimeUnit.SECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong * 1;
                    }
                });

        Observable.combineLatest(observable1, observable2, new BiFunction<Long, Long, Long>() {
            @Override
            public Long apply(Long o1, Long o2) throws Exception {
                Log.e(TAG, "combineLatest：apply: o1+o2：" + o1 + "+" + o2);
                //observable1的最后的一个数据都与observable2的每一个数据相加
                return o1 + o2;
            }
        }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "combineLatest：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "combineLatest：onNext 合并的结果== " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "combineLatest：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "combineLatest：onComplete == ");
            }
        });
    }

    /**
     * reduce()把观察者需要发送的事件聚合成一个事件并且发送。
     * 本质上都是前两个数据聚合，再与后一个数据聚合，依次类推。
     */
    private void reduce() {
        Observable.just(1, 2, 3, 4, 5)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.e(TAG, "reduce：accept 计算结果== " + integer + "*" + integer2);
                        //按先后顺序，两个事件聚合处理后 ，将结果再与下一事件聚合处理，依次类推
                        return integer * integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "reduce：accept 合并的结果== " + integer);
            }
        });
    }

    /**
     * collect():将Observable发送的数据收集到一个容器中
     */
    private void collect() {
        //第一个参数：声明容器的类型，第二个参数：处理数据的逻辑，加入容器中
        Observable.just(1, 2, 3, 4, 5).collect(new Callable<ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> call() throws Exception {
                return new ArrayList<>();
            }
        }, new BiConsumer<ArrayList<Integer>, Integer>() {
            @Override
            public void accept(ArrayList<Integer> list, Integer integer) throws Exception {
                Log.e(TAG, "collect：accept 加入容器的数据== " + integer);
                list.add(integer);
            }
        }).subscribe(new Consumer<ArrayList<Integer>>() {
            @Override
            public void accept(ArrayList<Integer> integers) throws Exception {
                Log.e(TAG, "collect：accept 最后结果== " + integers);
            }
        });
    }

    /**
     * 统计发送者的事件数
     */
    private void count() {
        Observable.just(1, 2, 3, 4, 5)
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long l) throws Exception {
                        Log.e(TAG, "reduce：accept 事件数== " + l);
                    }
                });
    }

}
