package com.example.jucdemo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * SynchronousQueue与其他BlockingQueue不同，SynchronousQueue是一个不存储元素的BlockingQueue
 * 每一个put（入队）操作必须等待一个take（出队）操作，否则不能继续添加元素，反之亦然
 * 产生一个，消费一个
 */
public class SynchronousQueueDemo {
    // 默认大小为10
//    BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(4);

    public static void main(String[] arg) {
        // 队列不存储元素，
        final BlockingQueue<String> blockingQueue = new java.util.concurrent.SynchronousQueue<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + " put 1");
                    blockingQueue.put("1");

                    // 需要等待线程BB取出队列中元素1，才能执行该语句
                    System.out.println(Thread.currentThread().getName() + " put 2");
                    blockingQueue.put("2");

                    System.out.println(Thread.currentThread().getName() + " put 3");
                    blockingQueue.put("3");
                } catch (InterruptedException e) {

                }
            }
        }, "AA").start();


        try {
            TimeUnit.MILLISECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + blockingQueue.take());

                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + blockingQueue.take());


                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + blockingQueue.take());


                } catch (InterruptedException e) {

                }
            }
        }, "BB").start();

    }

    /**
     * AA put 1
     * BB1
     * AA put 2
     * BB2
     * AA put 3
     * BB3
     */
}
