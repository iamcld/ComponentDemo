package com.example.jucdemo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLockDemo {

    static Lock lock1 = new ReentrantLock();
    static Lock lock2 = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new ThreadDemo(lock1, lock2));//该线程先获取锁1,再获取锁2
        Thread thread1 = new Thread(new ThreadDemo(lock2, lock1));//该线程先获取锁2,再获取锁1
        thread.start();
        thread1.start();
//        thread.interrupt();//是第一个线程中断
    }

    static class ThreadDemo implements Runnable {
        Lock firstLock;
        Lock secondLock;

        public ThreadDemo(Lock firstLock, Lock secondLock) {
            this.firstLock = firstLock;
            this.secondLock = secondLock;
        }

        @Override
        public void run() {
            try {
//                firstLock.lockInterruptibly();
                firstLock.lock();
                System.out.println(Thread.currentThread().getName() + "持有 " + firstLock
                        + ", 尝试获得 " + secondLock);

                TimeUnit.SECONDS.sleep(1);//更好的触发死锁
//                secondLock.lockInterruptibly();
                secondLock.lock();


            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getName() + " 线程被中断结束!");
            } finally {
                System.out.println(Thread.currentThread().getName() + " unlock!");

                firstLock.unlock();
                secondLock.unlock();
                System.out.println(Thread.currentThread().getName() + " 正常结束!");
            }
        }
    }

    /**
     * linux ps -ef |grep xxx
     *
     * windows下的Java
     */


}
