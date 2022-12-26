package com.example.jucdemo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionTest {

    static ReentrantLock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();
    public static void main(String[] args) throws InterruptedException {
        System.out.println("主线程1 lock.isLocked() = " + lock.isLocked());
        // 主线获取锁lock,往下执行
        lock.lock();
        System.out.println("主线程2 lock.isLocked() = " + lock.isLocked());
        new Thread(new SignalThread()).start();
        System.out.println("主线程等待通知");
        try {
            // 主线程进入等待状态，同时释放锁lock。子线程SignalThread获取锁，可以执行lock.lock()下面的代码
            condition.await();
            System.out.println("主线程3 lock.isLocked() = " + lock.isLocked());
        } finally {
            System.out.println("主线程4 lock.isLocked() = " + lock.isLocked());
            lock.unlock();
            System.out.println("主线程5 lock.isLocked() = " + lock.isLocked());
        }
        System.out.println("主线程恢复运行");
    }
    static class SignalThread implements Runnable {

        @Override
        public void run() {
            System.out.println("子线程1 lock.isLocked() = " + lock.isLocked());
            lock.lock();
            System.out.println("子线程2 lock.isLocked() = " + lock.isLocked());
            try {
                // 通知主线程condition无需等待
                System.out.println("子线程3 lock.isLocked() = " + lock.isLocked());
                condition.signal();
                System.out.println("子线程4 lock.isLocked() = " + lock.isLocked());
                System.out.println("子线程通知");
            } finally {
                System.out.println("子线程5 lock.isLocked() = " + lock.isLocked());
                lock.unlock();
                System.out.println("子线程6 lock.isLocked() = " + lock.isLocked());
            }
        }
    }
}
