package com.example.jucdemo;

public class SortDemo {

    public static void main(String[] arg) {
        int[] arrays = new int[]{3, 6, 7, 2, 1, 9, 5, 0, 4, 8};
//        insertSort(arrays);
        quickSort(arrays, 0, arrays.length - 1);
        for (int i = 0; i < arrays.length; i++) {
            System.out.println(arrays[i]);
        }

    }

    // 冒泡排序
    public static void bubbleSort(int[] arrays) {
        int tmp;
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i; j < arrays.length; j++) {
                if (arrays[i] > arrays[j]) {
                    tmp = arrays[i];
                    arrays[i] = arrays[j];
                    arrays[j] = tmp;
                }
            }
        }
    }

    // 选择排序
    public static void selectSort(int[] arrays) {
        int minIndex;//保存最小数下标
        int tmp;

        for (int i = 0; i < arrays.length; i++) {
            minIndex = i;
            for (int j = i; j < arrays.length; j++) {
                // 遍历一次，寻找最小值
                if (arrays[j] < arrays[minIndex]) { // 寻找最小的数
                    minIndex = j; // 将最小数的索引保存
                }
            }
            // 原数组：3, 6, 7, 2, 1, 9, 5, 0, 4, 8
            // 第一次遍历结果：0,6,7,2,1,9,5,3,4,8
            // 遍历一次后，将最小值0移动到i=0位置，同时将3移动到之前0的位置，之后i++
            tmp = arrays[i];
            arrays[i] = arrays[minIndex];
            arrays[minIndex] = tmp;

        }
    }

    // 插入排序
    public static void insertSort(int[] arrays) {
        int tmp;
        for (int i = 0; i < arrays.length; i++) {
            // 在已经排序好的数组中，从后向前插入，故j=i（i表示排序后的数组长度）
            for (int j = i; j > 0; j--) {
                if (arrays[j] < arrays[j - 1]) {
                    // 原数组：3, 6, 7, 2, 1, 9, 5, 0, 4, 8
                    // 第4次时：2, 3, 6, 7, 1, 9, 5, 0, 4, 8
                    // i = 3; j = 4
                    tmp = arrays[j - 1];
                    arrays[j - 1] = arrays[j];
                    arrays[j] = tmp;
                }
            }
        }
    }

    /*
     * 快速排序
     *
     * 参数说明：
     *     arrays -- 待排序的数组
     *     left -- 数组的左边界(例如，从起始位置开始排序，则l=0)
     *     right -- 数组的右边界(例如，排序截至到数组末尾，则r=a.length-1)
     */
    public static void quickSort(int[] arrays, int left, int right) {
        if (left < right) {
            int i, j, x;
            i = left;
            j = right;
            // 基准：将所有比基准值小的摆放在基准前面，所有比基准值大的摆在基准的后面
            x = arrays[i];
            // 原数组：3, 6, 7, 2, 1, 9, 5, 0, 4, 8
            while (i < j) {
                while (i < j && arrays[j] > x) {
                    j--; // 从右向左找第一个小于x的数
                }
                if (i < j) {
                    arrays[i++] = arrays[j];// 将小于基准的数字，放在基准左边
                }
                while (i < j && arrays[i] < x) {
                    i++;// 从左向右找第一个大于x的数
                }
                if (i < j) {
                    arrays[j--] = arrays[i];// 将大于基准的数字，放在基准右边
                }
            }
            arrays[i] = x;
            quickSort(arrays, left, i - 1);// 在基准位置左边，比基准小的全部数据再次进行快速排序
            quickSort(arrays, i + 1, right);// 在基准位置右边。比基准大的全部数据再次进行快速排序
        }
    }


}
