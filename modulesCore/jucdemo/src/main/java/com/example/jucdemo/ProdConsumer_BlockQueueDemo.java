package com.example.jucdemo;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产消费：生产1个，消费1个
 */
class MyResource {
    // 默认开启，进行生产+消费
    private volatile boolean FLAG = true;
    private AtomicInteger atomicInteger = new AtomicInteger();
    private BlockingQueue<String> blockingQueue;

    public MyResource(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println(blockingQueue.getClass().getName());
    }

    public void myProd() throws Exception {
        String data = null;
        boolean retResult;
        while (FLAG) {
            // ++i
            data = atomicInteger.incrementAndGet() + "";
            // 1秒插入队列失败，则返回false
            retResult = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if (retResult) {
                System.out.println(Thread.currentThread().getName() + " 插入队列 " + data + " 成功");
            } else {
                System.out.println(Thread.currentThread().getName() + " 插入队列 " + data + " 失败");
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println(Thread.currentThread().getName() + " 大老板叫停，表示FLAG = false 生产动作停止");


    }

    public void myConsumer() throws Exception {
        String result = null;
        while (FLAG) {
            // 2秒取出1个，纯粹测试用
            result = blockingQueue.poll(2L, TimeUnit.SECONDS);
            if (result == null || result.equalsIgnoreCase("")) {
                FLAG = false;
                System.out.println(Thread.currentThread().getName() + " 超过2秒没有取到蛋糕，消费退出 ");
                System.out.println();
                System.out.println();
                return;
            }

            System.out.println(Thread.currentThread().getName() + " 消费队列取到蛋糕 " + result + " 成功");

        }
    }

    public void stop() {
        this.FLAG = false;
    }
}

public class ProdConsumer_BlockQueueDemo {

    public static void main(String[] arg) {
        final MyResource myResource = new MyResource(new ArrayBlockingQueue<String>(10));

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " 生产线程启动");
                try {
                    myResource.myProd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "生产者").start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " 消费线程启动");
                System.out.println();
                System.out.println();
                try {
                    myResource.myConsumer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "消费者").start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("5秒钟时间到，大老板叫停，活动结束");
        myResource.stop();

    }
}
