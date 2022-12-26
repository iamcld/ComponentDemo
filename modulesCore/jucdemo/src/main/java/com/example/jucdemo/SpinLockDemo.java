package com.example.jucdemo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自旋锁
 */
public class SpinLockDemo {
    //原子引用线程，初始值为null
    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    // 自选锁：加锁
    public void myLock() {
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + " come in");
        // 第一次执行该语句时，由于atomicReference初始化时atomicReference值为null，期望值也为null，比较成功，将atomicReference设置为thread
        //
        // 反复的循环比较
        // 1、AA线程第一次加锁时，因为atomicReference值为null，此时compareAndSet设置成功值变成thread，返回true,故条件语句不会成立
        // 2、BB线程第一次加锁时，由于atomicReference值为thread
        while (!atomicReference.compareAndSet(null, thread)) {

        }
    }

    // 自选锁：解锁
    public void myUnlock() {
        Thread thread = Thread.currentThread();
        // 将atomicReference设置为null
        atomicReference.compareAndSet(thread, null);
        System.out.println(Thread.currentThread().getName() + " invoked myunlock");

    }

    public static void main(String[] arg) {
        final SpinLockDemo spinLockDemo = new SpinLockDemo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 加锁， 此时条件while(!atomicReference.compareAndSet(null, thread)))不会成立，
                    // 将atomicReference设置为Thread
                    spinLockDemo.myLock();
                    // 该线程占用此锁5秒钟，BB线程执行spinLockDemo.myLock()时，由于atomicReference为Thread,
                    // 故while(!atomicReference.compareAndSet(null, thread)))会成立，所以循环等待
                    TimeUnit.MILLISECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 解锁时，将atomicReference
                spinLockDemo.myUnlock();
            }
        },"AA").start();

        // 保证AA上面的线程先启动
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                // 由于AA线程占用了锁，故BB线程需要一直循环等待AA线程释放锁
                spinLockDemo.myLock();


                spinLockDemo.myUnlock();
            }
        },"BB").start();
    }
}
