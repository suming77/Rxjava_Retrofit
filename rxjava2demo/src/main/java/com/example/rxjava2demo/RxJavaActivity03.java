package com.example.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * RxJava2的简单使用(三)
 * 我的博客：https://blog.csdn.net/m0_37796683
 */
public class RxJavaActivity03 extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RxJavaActivity03.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava03);

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.btn_delay).setOnClickListener(this);
        findViewById(R.id.btn_doNext).setOnClickListener(this);
        findViewById(R.id.btn_doOnError).setOnClickListener(this);
        findViewById(R.id.btn_doOnFinally).setOnClickListener(this);
        findViewById(R.id.btn_onErrorReturn).setOnClickListener(this);
        findViewById(R.id.btn_onErrorResumeNext).setOnClickListener(this);
        findViewById(R.id.btn_onExceptionResumeNext).setOnClickListener(this);
        findViewById(R.id.btn_retry).setOnClickListener(this);
        findViewById(R.id.btn_retryWhen).setOnClickListener(this);
        findViewById(R.id.btn_repeat).setOnClickListener(this);
        findViewById(R.id.btn_repeatWhen).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back://返回
                finish();
                break;
            case R.id.btn_delay://
                delay();
                break;
            case R.id.btn_doNext://
                doNext();
                break;
            case R.id.btn_doOnError://
                doOnError();
                break;
            case R.id.btn_doOnFinally://
                doOnFinally();
                break;
            case R.id.btn_onErrorReturn://
                onErrorReturn();
                break;
            case R.id.btn_onErrorResumeNext://
                onErrorResumeNext();
                break;
            case R.id.btn_onExceptionResumeNext://
                onExceptionResumeNext();
                break;
            case R.id.btn_retry://
                retry();
                break;
            case R.id.btn_retryWhen://
                retryWhen();
                break;
            case R.id.btn_repeat://
                repeat();
                break;
            case R.id.btn_repeatWhen://
                repeatWhen();
                break;
            default:
                break;
        }
    }

    /**
     * 延迟一定时间Observable(被观察者)再发送事件
     */
    private void delay() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("抛出异常"));
                e.onNext(3);
                e.onComplete();
            }
        })
                .delay(2, TimeUnit.SECONDS, true)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "delay：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "delay：onNext == " + integer);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "delay：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "delay：onComplete == ");
                    }
                });
    }


    /**
     * do相关方法：doOnNext()，doOnEach()，doAfterNext()
     */
    private void doNext() {
        Observable.just(1, 2, 3)
                //执行next()事件前执行
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "doOnNext 事件前执行== " + integer);
                    }
                })
                //当Observable每次发送一个数据事件都会调用(onNext()前回调)
                .doOnEach(new Consumer<Notification<Integer>>() {
                    @Override
                    public void accept(Notification<Integer> integerNotification) {
                        Log.e(TAG, "doOnEach 每次都执行== " + integerNotification.getValue());
                    }
                })
                //执行next()事件后执行
                .doAfterNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        Log.e(TAG, "doAfterNext 事件后执行== " + integer);
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "doNext：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "doNext：onNext == " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "doNext：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "doNext：onComplete == ");
                    }
                });
    }

    /**
     * do相关方法:doOnError()，doAfterTerminate()
     */
    private void doOnError() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                e.onNext(1);
                e.onError(new Exception("抛出异常"));
                e.onNext(2);
                e.onComplete();
            }
        })
                //Observable发送错误事件时调用
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "doOnError 发送错误事件== " + throwable.getMessage());
                    }
                })
                //Observable发送事件完毕后，无论是正常发送还是异常终止都会执行
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doAfterTerminate == 事件结束");
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "doOnError：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "doOnError：onNext == " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "doOnError：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "doOnError：onComplete == ");
                    }
                });
    }

    /**
     * do相关方法:doOnSubscribe()，doOnComplete()，doFinally()
     */
    private void doOnFinally() {
        Observable.just(1, 2, 3)
                //观察者订阅时调用
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        Log.e(TAG, "doOnSubscribe == 订阅时执行");
                    }
                })
                //Observable正常发送事件完成后调用
                .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        Log.e(TAG, "doOnComplete == 事件完成执行");
                    }
                })
                //最后执行
                .doFinally(new Action() {
                    @Override
                    public void run() {
                        Log.e(TAG, "doFinally == 最后执行");
                    }
                })
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
    }

    /**
     * onErrorReturn():在Observable发生错误或者异常的时候，拦截错误并执行指定逻辑，
     * 返回一个与源Observable相同类型的结果（onNext()），最后回调onConplete()方法。
     */
    private void onErrorReturn() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                //发送1~10共10个事件
                for (int i = 0; i < 10; i++) {
                    if (i == 5) {//i==5抛出异常
                        e.onError(new Exception("抛出异常"));
                    }
                    e.onNext(i);
                }
            }
        })
                //拦截错误并执行指定逻辑，返回相同类型结果
                .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable) throws Exception {
                        return 404;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onErrorReturn：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onErrorReturn：onNext ==" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onErrorReturn：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onErrorReturn：onComplete == ");
                    }
                });
    }


    /**
     * onErrorResumeNext()：在Observable发生错误或者异常的时候，拦截错误并执行指定逻辑，
     * 返回一个与源Observable相同类型的新的Observable被观察者，最后回调onConplete()方法。
     * 与onErrorReturn()类似
     */
    private void onErrorResumeNext() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //发送2个事件
                e.onNext("一");
                e.onNext("二");
                //抛出异常
                e.onError(new Exception("抛出异常"));
            }
        })
                //拦截错误并执行指定逻辑,返回Observable
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends String>>() {
                    @Override
                    public ObservableSource<? extends String> apply(Throwable throwable) {
                        return Observable.just("404", "405", "406");
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onErrorResumeNext：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onErrorResumeNext：onNext ==" + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onErrorResumeNext：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onErrorResumeNext：onComplete == ");
                    }
                });
    }

    /**
     * onExceptionResumeNext()：在Observable发生者异常的时候，拦截错误并执行指定逻辑，
     * 返回一个与源Observable相同类型的新的Observable被观察者，最后回调onConplete()方法。
     * onErrorResumeNext()类似，注意这里仅仅指发生异常的时候，发送错误不会回调
     */
    private void onExceptionResumeNext() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //发送2个事件
                e.onNext("事件1");
                e.onNext("事件2");
                //抛出异常
                e.onError(new Exception("抛出异常"));
            }
        })
                //拦截异常并执行指定逻辑,返回多个Observable执行的事件
                .onExceptionResumeNext(new ObservableSource<String>() {
                    @Override
                    public void subscribe(Observer<? super String> observer) {
                        observer.onNext("404");
                        observer.onNext("405");
                        observer.onNext("406");
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onExceptionResumeNext：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onExceptionResumeNext：onNext ==" + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onExceptionResumeNext：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onExceptionResumeNext：onComplete == ");
                    }
                });
    }


    /**
     * retry()：当Observable发生错误或者异常时，重新尝试执行Observable的逻辑，如果经过n次重试后仍然出现错误或者异常，
     * 最后回调onError()方法，如果没有错误或者异常，则按照正常的流程走。
     */
    private void retry() {
        //无限次重试
        //retry();
        //指定重试次数
        //retry(long times);
        //无限重试，返回当前重试的次数和异常信息
        //retry(BiPredicate<? super Integer, ? super Throwable> predicate);
        //指定重试次数，返回异常信息
        //retry(long times, Predicate<? super Throwable> predicate);
        //无限重试，返回异常信息
        //retry(Predicate<? super Throwable> predicate);

        //times：设置的重试次数,Integer：当前重试的次数为第几次,predicate：异常信息
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("抛出异常"));
            }
        })
                //重新发送数据，这里重发2次，带有异常信息回调
                .retry(2, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) {
                        //false: 不重新发送数据，回调Observer的onError()方法结束
                        //true: 重新发送请求（最多发送2次）
                        return true;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "retry：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "retry：onNext ==" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "retry：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "retry：onComplete == ");
                    }
                });
    }

    /**
     * retryWhen():在源Observable出现错误或者异常时，通过回调第二个Observable来判断是否重新尝试执行源Observable的逻辑，
     * 如果第二个Observable没有出现错误或者异常，就会重新尝试执行源Observable的逻辑，否则会直接回调订阅者的onError()方法
     */
    private void retryWhen() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                e.onNext(66);
                e.onNext(88);
                e.onError(new Exception("抛出异常"));
            }
        })
                //发生错误或者异常时，重试，将原来Observable的错误或者异常事件，转换成新的Observable
                //如果新的Observable返回了onError()事件，则不再重新发送原来Observable的数据
                //如果新的Observable返回onNext事件，则会重新发送原来Observable的数据
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                //新观察者Observable发送onNext()事件，则源Observable重新发送数据。如果持续遇到错误则持续重试。
                                //return Observable.just(1);

                                //回调onError()事件，并且接收传过去的错误信息
                                return Observable.error(new Exception("抛出异常2"));
                            }
                        });
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "retryWhen：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "retryWhen：onNext ==" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "retryWhen：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "retryWhen：onComplete == ");
                    }
                });
    }

    /**
     * repeat()：无条件，重复发送被观察者事件。可设置重复的次数，如果不设置参数则会无限次重复发送。
     * 注意：如果中途抛出错误或者异常，则无法重复发送。
     */
    private void repeat() {
        Observable.just(1, 2)
                //重复2次
                .repeat(2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "repeat：onSubscribe == 订阅");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "repeat：onNext ==" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "repeat：onError == " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "repeat：onComplete == ");
                    }
                });
    }

    /**
     * repeatWhen():有条件，重复性发送原来的Observable被观察者事件
     */
    private void repeatWhen() {
        Observable.just(1004, 1005)
                .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
                    //如果新的Observable被观察者返回onComplete()或onError()事件，则不重新订阅、发送源Observable的事件
                    //如果新的Observable被观察者返回其他事件，则重新订阅、发送源Observable的事件
                    @Override
                    public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                        //flatMap用于接收上面的数据
                        return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Object o) throws Exception {
                                //通知原来的Observable，重新订阅和发送事件（发送什么数据不重要，这里仅做通知使用）
                                //return Observable.just(1);

                                //等于发送onComplete()方法，但是不会回调Observer的onComplete()
                                //return Observable.empty();

                                //回调onError()事件，并且接收传过去的错误信息
                                return Observable.error(new Exception("抛出异常"));
                            }
                        });
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "repeatWhen：onSubscribe == 订阅");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "repeatWhen：onNext ==" + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "repeatWhen：onError == " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "repeatWhen：onComplete == ");
            }
        });
    }

}
