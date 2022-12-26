package com.example.jucdemo;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionQueueTest {

    public static void main(String[] arg) {
        final BlockingQueue blockingQueue = new BlockingQueue<>(4);

        for (int i = 0; i < 5; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        blockingQueue.dequeue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        for (int i = 0; i < 5; i++){
            final int data = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        blockingQueue.equeue(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    // 阻塞队列
    public static class BlockingQueue<E> {
        // 阻塞队列最大容量
        private int size;

        private ReentrantLock lock = new ReentrantLock();
        //队列满时的等待条件
        private Condition notFull = lock.newCondition();
        //队列空时的等待条件
        private Condition notEmpty = lock.newCondition();

        //队列底层实现
        private LinkedList<E> linkedList = new LinkedList<>();

        public BlockingQueue(int size) {
            this.size = size;
        }

        public void equeue(E e) throws InterruptedException {
            lock.lock();
            try {


                /**
                 * 多线程判断时要用while,不要用if
                 * 即if(linkedList.size == size)不能这样用
                 * 因为当线程为多个时，if语句会出错，虚假
                 */
                //队列已满,在notFull条件上等待
                while (linkedList.size() == size) {
                    System.out.println("notFull.wait() = " + e);
                    notFull.await();
                }
                // 入队列，加入末尾
                linkedList.add(e);
                System.out.println("入队列 = " + e);

                //通知在notEmpty条件上等待的线程
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public E dequeue() throws InterruptedException {
            lock.lock();
            try {
                //队列为空,在notEmpty条件上等待
                while (linkedList.isEmpty()) {
                    notEmpty.await();
                }
                //出队:移除链表首元素
                E e = linkedList.removeFirst();
                System.out.println("出队列 = " + e);
                notFull.signal();
                return e;
            } finally {
                lock.unlock();
            }
        }

    }
}
