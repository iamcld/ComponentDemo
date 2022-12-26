package com.example.jucdemo;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.LockSupport;

public class Solution {
    private static class ListNode {
        private int data;
        private ListNode next;

        public ListNode(int data, ListNode next) {
            this.data = data;
            this.next = next;
        }


    }

    public static void main(String[] arg) {
        ListNode node5 = new ListNode(5, null);
        ListNode node4 = new ListNode(4, node5);
        ListNode node3 = new ListNode(3, node4);
        ListNode node2 = new ListNode(2, node3);
        ListNode node1 = new ListNode(1, node2);
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        hashMap.containsKey(9);
//        LockSupport lockSupport = new LockSupport();
//        LockSupport.unpark();

        Stack<Integer> stack = new Stack<>();

        // 1->2->3->4->5
        // 1->2-3->5
        ListNode relust = removeNthFromEnd(node1, 5);

        while (relust != null) {
            System.out.println(relust.data);
            relust = relust.next;
        }


    }

    private static ListNode removeNthFromEnd(ListNode head, int n) {
        if (head == null || n < 0) {
            return head;
        }
        ListNode p = head;
        // 被删除节点的前驱节点
        ListNode pre = null;
        int len = 1;

        while (p.next != null) {
            p = p.next;
            len++;
        }
        if (len < n) {
            return head;
        }

        p  = head;
        // 倒数第n个节点下标
        int index = len - n;
        // 遍历倒数第n个节点
        for (int i = 0; i < index; i++) {
            if (i == index - 1) {
                // 保存倒数第n个节点的前驱节点
                pre = p;
            }
            p = p.next;
        }

        // 倒数第n个节点刚好为头节点时，无前驱节点
        if (pre == null){
            p = head.next;
            return p;
        }

        pre.next = p.next;

        return head;
    }
}
