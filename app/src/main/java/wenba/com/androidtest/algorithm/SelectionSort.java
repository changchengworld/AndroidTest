package wenba.com.androidtest.algorithm;

/**
 * Created by silvercc on 18/3/8.
 */

public class SelectionSort {
    private int[] selectionSort(int[] nums) {
        int len = nums.length;
        int minIndex, temp;
        for (int i = 0; i < len - 1; i++) {
            minIndex = i;
            for (int j = i + 1; j < len; j++) {
                if (nums[minIndex] > nums[j]) {
                    minIndex = j;
                }
                temp = nums[i];
                nums[i] = nums[minIndex];
                nums[minIndex] = temp;
            }
        }
        return nums;
    }
}
