package com.ruoyi.im.utils;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * byte转全部类
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class ByteToAll {

    public static String byteToCode(byte[] bytes){//转字节码
        return Arrays.toString(bytes);
    }
    public static String byteToText(byte[] bytes){//转文本 为了统一全部采用UTF-8
        return new String(bytes, Charset.forName("UTF-8"));
    }
    public static String byteToHxe(byte[] bytes){//转十六进制 为了统一全部采用UTF-8
        final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5','6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for(byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }
        return new String(buf);
    }

    /**
     * byte[]转int
     * @param bytes 需要转换成int的数组
     * @return int值
     */
    public static int byteToInt(byte[] bytes) {//byte[]转int
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    public static long byteToLong(byte[] bytes) {//byte[]到long
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();

    }

    public static short byteToShort(byte[] b){//到short
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l<<=8; //<<=和我们的 +=是一样的，意思就是 l = l << 8
            l |= (b[i] & 0xff); //和上面也是一样的  l = l | (b[i]&0xff)
        }
        return l;
    }




}
