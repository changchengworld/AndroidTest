package wenba.com.androidtest.algorithm;

/**
 * Created by silvercc on 18/3/8.
 */

public class BubbleSort {

    private int[] bubbleSort(int[] nums) {
        int len = nums.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len - 1 - i; j++) {//这一轮循环就是一个泡泡冒到头了
                if (nums[j] > nums[j + 1]) {
                    int temp = nums[j + 1];
                    nums[j + 1] = nums[j];
                    nums[j] = temp;
                }
            }
        }
        return nums;
    }
}
