package com.ruoyi.im.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * byte工具类
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class ByteUtils {
    /**
     * 数组拼接
     * @param start 前
     * @param end 后
     * @return
     */
    public static byte[] byteMerger(byte[] start,byte[] end){
        byte[] startEnd = new byte[start.length + end.length];
        if(start==null){
            System.arraycopy(end,0,startEnd,0,end.length);
        }else if (end==null){
            System.arraycopy(start,0,startEnd,0,start.length);
        }else {
            System.arraycopy(start,0,startEnd,0,start.length);
            System.arraycopy(end,0,startEnd,start.length,end.length);
        }
        return startEnd;
    }
    public static String timeToText(){//生成当前时间转字节
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }
    /**
     * 字节集截取
     * @param bytes 原始字节集
     * @param begin 原始字节集开始位置
     * @param count 截取长度
     * @return subBytes
     */
    public static byte[] subBytes(byte[] bytes, int begin, int count) {
        if (bytes == null || bytes.length <= 1) {
            return bytes;
        }
        byte[] subBytes = new byte[count];
        System.arraycopy(bytes, begin, subBytes, 0, count);
        return subBytes;
    }

    /**
     * 字节集截取
     * @param bytes 原始字节集
     * @param begin 原始字节集开始位置
     * @param count 截取长度
     * @return subBytes
     */
    public static byte[] subToBytes(byte[] bytes, int begin, int count) {
        if (bytes == null || bytes.length <= 1) {
            return bytes;
        }
        byte[] subBytes = new byte[count];
        System.arraycopy(bytes, begin, subBytes, 0, count);
        return subBytes;
    }





}
