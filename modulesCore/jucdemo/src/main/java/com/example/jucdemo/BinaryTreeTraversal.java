package com.example.jucdemo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 1
 * 2       3
 * 4               5
 */

public class BinaryTreeTraversal {
    Stack<Integer> stack = new Stack<>();
    Deque<Integer> dequeList = new ArrayDeque<>();
    Deque<Integer> dequeLink = new LinkedList<>();
    LinkedList<Integer> linkedList = new LinkedList<>();
    List<List<Integer>> lists = new LinkedList<>();

    void test() {
        stack.peek();
        dequeLink.push(1);
        linkedList.push(1);
        linkedList.pop();
        linkedList.isEmpty();

    }
}
