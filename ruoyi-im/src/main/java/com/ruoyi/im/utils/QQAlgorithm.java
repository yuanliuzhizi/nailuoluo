package com.ruoyi.im.utils;

/**
 * qq的算法工具类
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class QQAlgorithm {

    /**
     * q群临时会话 uinKey
     * @param group 群号码
     * @return
     */
    public static String groupToGID(String group) {
        String per = group.substring(0, group.length() - 6);
        int i = Integer.parseInt(per);
        if (i <= 10) {
            i = 202 + i;
        } else if (i <= 19) {
            i = 480 + i - 11;
        } else if (i <= 66) {
            i = 2100 + i - 20;
        } else if (i <= 156) {
            i = 2010 + i - 67;
        } else if (i <= 209) {
            i = 2147 + i - 157;
        } else if (i <= 309) {
            i = 4100 + i - 210;
        } else if (i <= 499) {
            i = 3800 + i - 310;
        }
        return Integer.toString(i) + group.substring(group.length() - 6);
    }

}
