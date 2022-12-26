package com.example.jucdemo;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

// 可冲入锁 ReentrantLock
public class ReentrantLockTest {
    private static Lock lock1 = new java.util.concurrent.locks.ReentrantLock();
    private static Lock lock2 = new java.util.concurrent.locks.ReentrantLock();


    public static void main(String[] arg) {
//        Thread thread1 = new Thread(new ReentrantLockRunable(lock1, lock2, 1));
//        Thread thread2 = new Thread(new ReentrantLockRunable(lock2, lock1, 2));
//        thread1.start();
//        thread2.start();
//        thread2.interrupt();

        Thread thread1 = new Thread(new ReentrantTryLockRunnable(lock1, lock2, 1));
        Thread thread2 = new Thread(new ReentrantTryLockRunnable(lock2, lock1, 2));
        thread1.start();
        thread2.start();
//        thread2.interrupt();
    }

    public static class ReentrantLockRunable implements Runnable {
        private Lock first;
        private Lock second;
        private int id;

        public ReentrantLockRunable(Lock lock1, Lock lock2, int id) {
            this.first = lock1;
            this.second = lock2;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("进入线程 " + id);
//                lock1.lock();
                first.lock();
                System.out.println("进入线程 " + id + " ;;; 获取锁 = " + first.toString());
                TimeUnit.MILLISECONDS.sleep(50);
                second.lock();
                System.out.println("进入线程 " + id + " ;;; 获取锁 = " + second.toString());
//                lock1.tryLock(1, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                first.unlock();
                second.unlock();
                System.out.println("退出线程 " + id);
            }

        }
    }

    public static class ReentrantTryLockRunnable implements Runnable {
        private Lock first;
        private Lock second;
        private int id;

        public ReentrantTryLockRunnable(Lock lock1, Lock lock2, int id) {
            this.first = lock1;
            this.second = lock2;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                // 获取不成功，休眠10ms
                while (!first.tryLock()) {
                    System.out.println("while() 线程 " + id + " ;;; 没有获取锁  " + first.toString());
                    Thread.sleep(100);
                }
                System.out.println("线程 " + id + " ;;; 获取锁 = " + first.toString());

                while (!second.tryLock()) {
                    System.out.println("while() 线程 " + id + " ;;; 没有获取锁 = " + second.toString());
                    first.unlock();
                    Thread.sleep(100);
                }
                System.out.println("线程 " + id + " ;;; 获取锁 = " + second.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                first.unlock();
                second.unlock();
                System.out.println(Thread.currentThread().getName()+"正常结束!");
            }
        }
    }


}
