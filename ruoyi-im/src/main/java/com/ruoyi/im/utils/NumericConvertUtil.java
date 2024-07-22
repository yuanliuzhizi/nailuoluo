package com.ruoyi.im.utils;

public class NumericConvertUtil {
    /**
     * 在进制表示中的字符集合
     */
    final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z' };

    /**
     * 将十进制的数字转换为指定进制的字符串
     *
     * @param n 十进制的数字
     * @param base 指定的进制
     * @return
     */
    public static String toOtherBaseString(long n, int base) {
        long num = 0;
        if (n < 0) {
            num = ((long) 2 * 0x7fffffff) + n + 2;
        } else {
            num = n;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((num / base) > 0) {
            buf[--charPos] = digits[(int) (num % base)];
            num /= base;
        }
        buf[--charPos] = digits[(int) (num % base)];
        return new String(buf, charPos, (32 - charPos));
    }

    /**
     * 将其它进制的数字（字符串形式）转换为十进制的数字
     *
     * @param str 其它进制的数字（字符串形式）
     * @param base 指定的进制
     * @return
     */
    public static long toDecimalism(String str, int base) {
        char[] buf = new char[str.length()];
        str.getChars(0, str.length(), buf, 0);
        long num = 0;
        for (int i = 0; i < buf.length; i++) {
            for (int j = 0; j < digits.length; j++) {
                if (digits[j] == buf[i]) {
                    num += j * Math.pow(base, buf.length - i - 1);
                    break;
                }
            }
        }
        return num;
    }




    /**
     * 将二进制的数字转换为十六进制的字符串
     *
     * @param n 二进制的数字
     * @return
     */
    public static String toTwoBaseString(String n) {
       return toOtherBaseString(toDecimalism(n,2),16);
    }


    /**
     * 将十六进制的数字转换为二进制的字符串
     *
     * @param n 二进制的数字
     * @return
     */
    public static String toHexBaseString(String n) {
        return toOtherBaseString(toDecimalism(n,16),2);
    }





/*    public static void main(String[] args) {
        System.out.println(toOtherBaseString(16857120L, 10));
        System.out.println(toDecimalism("A1B1AJASWDE", 36));
    }*/


}
