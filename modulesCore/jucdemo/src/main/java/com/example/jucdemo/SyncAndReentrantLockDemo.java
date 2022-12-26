package com.example.jucdemo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 题目：多线程之间按顺序调用，实现A->B->C三个线程启动，要求如下：
 * A打印5次，B打印10次，C打印15次
 * 紧接着
 * A打印5次，B打印10次，C打印15次
 * ...
 * 来10轮
 */
class ShareResource {
    // A用1表示, B:2 C:3
    private int number = 1;
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();


    public void print5() {
        lock.lock();
        try {
            // number为1，不满足条件，不进来，说明是1号线程进来，执行下面的1号线程打印语句
            while (number != 1) {
                // 说明不是1号干的打印，故等待
                c1.await();
            }
            // 1号线程打印完毕
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + " " +i);
            }
            // 通知2号线程，1号线程打印完毕，该2号线程进行打印
            number = 2;
            c2.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print10() {
        lock.lock();
        try {
            //number为2，说明是2号线程进来，执行下面的2号线程打印语句
            while (number != 2) {
                // 说明不是2号线程干的打印，故等待
                c2.await();
            }
            // 2号线程打印完毕
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + " " +i);
            }
            // 通知3号线程，2号线程打印完毕，该3号线程进行打印
            number = 3;
            c3.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print15() {
        lock.lock();
        try {
            //number为3，说明是3号线程进来，执行下面的3号线程打印语句
            while (number != 3) {
                // 说明不是3号线程干的打印，故等待
                c3.await();
            }
            // 3号线程打印完毕
            for (int i = 1; i <= 15; i++) {
                System.out.println(Thread.currentThread().getName() + " " +i);
            }
            // 通知1号线程，3号线程打印完毕，该1号线程进行打印
            number = 1;
            c1.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}

public class SyncAndReentrantLockDemo {
    public static void main(String[] arg) {
        final ShareResource shareResource = new ShareResource();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    shareResource.print5();
                }
            }
        }, "A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    shareResource.print10();
                }
            }
        }, "B").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    shareResource.print15();
                }
            }
        }, "C").start();
    }

}
