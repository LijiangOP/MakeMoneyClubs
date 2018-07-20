package com.zhengdao.zqb.utils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/24 15:44
 */
public class RxUtils {

    /**
     * 子线程操作，结果回调回主线程
     *
     * @param task
     * @param <T>
     */
    public static <T> void doTask(final Task<T> task) {
        try {
            Observable.create(new Observable.OnSubscribe<T>() {
                @Override
                public void call(Subscriber<? super T> subscriber) {
                    task.doOnIOThread();
                    subscriber.onNext(task.getT());
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<T>() {
                        @Override
                        public void call(T t) {
                            task.doOnUIThread();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract static class Task<T> {
        private T t;

        public Task() {

        }

        public void setT(T t) {
            this.t = t;
        }

        public T getT() {
            return t;
        }

        public abstract void doOnUIThread();

        public abstract void doOnIOThread();
    }
}
