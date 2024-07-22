package com.ruoyi.im.utils;


import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * 全部转byte类
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class AllToByte {

    public static byte[] codeToByte(String str){//字节码转byte
        if(str == null || str.trim().equals("")) {
            return null;
        }else {
            ArrayList li= new ArrayList();
            str= str.replace(" ", "");
            str=str .replaceAll("\\s*", "");
            str=str.replaceAll("[\\{\\}\\[\\]]", "");
            str=str.replaceAll("\n", "");
            String s[]=str.split("[,]");
            for (int i = 0; i < s.length; i++) {
                li.add(s[i]);
            }
            byte[] toby = new byte[li.size()];
            for (int j = 0; j < li.size(); j++) {
                int aaa=Integer.valueOf((String)li.get(j));
                toby[j]=(byte)aaa;
            }
            return toby;
        }

    }



    public static byte[] hexToByte(String str) {//十六进制转换字节数组
        str=str.replace(" ", "");
        str=str .replaceAll("\\s*", "");
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }


    public static byte[] textToByte(String str) {//文本转换字节数组
        try {
             return  str.getBytes("UTF-8");
        }catch (Exception e){
            System.err.println("文本转换字节数组");
        }
        return null;

    }


    /**
     * int到byte[] 由高位到低位 32位
     * @param i 需要转换为byte数组的整行值。
     * @return byte数组
     */
    public static byte[] intToByte(long i) {//int到byte[]
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }


    public static byte[] longToByte(long x) {//long到byte[]
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();

    }

    public static byte[] shortToByte(short s){//short到byte
        byte[] b = new byte[2];
        for(int i = 0; i < 2; i++){
            int offset = 16 - (i+1)*8; //因为byte占4个字节，所以要计算偏移量
            b[i] = (byte)((s >> offset)&0xff); //把16位分为2个8位进行分别存储
        }
        return b;
    }






}
