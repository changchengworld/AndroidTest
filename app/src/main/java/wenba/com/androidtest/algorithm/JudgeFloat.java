package wenba.com.androidtest.algorithm;

/**
 * Created by silvercc on 18/3/8.
 * 判断一个单精度浮点型是不是整数
 */

public class JudgeFloat {
    //单精度浮点数float是32位的，存储方式是第一位符号位，正数0，负数为1，接下来的八位是指数位，最后23位是底数位
    //
    private static boolean testE(float x) {
        int b = Float.floatToIntBits(x);
        int z = ((b >> 23) & 0xff) - 127;//0xff八位1
        int d = b & 0x7fffff;//0x7fffff23位1
        if (d == 0) {
            return z >= 0;
        } else {
            int k = 1;
            while ((d & 0x01) != 0x01) {
                d = d >> 1;
                k += 1;
            }
            k = 23 - k + 1;
            return (z >= k);
        }
    }
}
