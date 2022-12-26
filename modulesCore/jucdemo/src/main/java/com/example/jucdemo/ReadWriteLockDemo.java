package com.example.jucdemo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 多个线程同时读一个资源类没有任何问题，所以为了满足并发量，读取共享资源应该可以同时进行
 * 但是如果有1个线程想去写共享资源，就不应该再有其他线程可以对该资源进行读或写
 * <p>
 * 总结：
 * 读-读能共存
 * 读-写不能共存
 * 写-写不能共存
 * <p>
 * 读写锁：
 * 写的时候只有1个线程进去持有锁:原子+独占,整个过程必须是一个完整的统一体，中间不许被分割，被打断
 * 读的时候可以多个线程进去
 */
public class ReadWriteLockDemo {

    static class MyCache {
        private volatile Map<String, Object> map = new HashMap<>();
        //因为lock只能保证1个线程读或者写操作，无法满足多个读线程在操作，故不能用
//        private Lock lock = new ReentrantLock();
        private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

        //写操作
        public void put(String key, Object value) {
            // 写锁
            try {
                rwLock.writeLock().lock();
                System.out.println("线程" + Thread.currentThread().getName() + " 正在写入数据：" + key);
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                    map.put(key, value);
                    System.out.println("线程" + Thread.currentThread().getName() + " 写入完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                rwLock.writeLock().unlock();
            }

        }

        // 读操作
        public void get(String key) {
            try {
                rwLock.readLock().lock();
                System.out.println("线程" + Thread.currentThread().getName() + " 正在读取：" + key);
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                    Object result = map.get(key);
                    System.out.println("线程" + Thread.currentThread().getName() + " 读取完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rwLock.readLock().unlock();
            }

        }
    }


    public static void main(String[] arg) {
        final MyCache myCache = new MyCache();


//        for (int i = 0; i < 5; i++) {
//            final int tempInt = i;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    myCache.get(tempInt + "");
//                }
//            }, tempInt + "").start();
//        }

        for (int i = 0; i < 5; i++) {
            final int tempInt = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myCache.put(tempInt + "", tempInt + "");
                }
            }, tempInt + "").start();
        }

        for (int i = 0; i < 5; i++) {
            final int tempInt = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myCache.get(tempInt + "");
                }
            }, tempInt + "").start();
        }

    }
}
