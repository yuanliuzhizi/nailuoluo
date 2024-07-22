package com.ruoyi.im.utils;


import java.net.ServerSocket;
import java.util.Random;

/**
 * 随机工具
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class RandomUtils {
    /**
     * 随机整数字节到byte
     * @param size byte长度
     * @return 字节码
     */
    public static byte[] randomToByte(int size){//随机整数字节到byte
        byte[] bytes =new byte[size >=1?size:1];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i]=(byte) (int) randomToNumber(0,255);
        }
        return bytes;
    }

    /**
     * 指定范围随机数
     * @param minVal 最小值
     * @param maxVal 最大值
     * @return 整数
     */
    public static Integer randomToNumber(int minVal, int maxVal){//指定范围随机数
        Integer v  = new Random().nextInt(maxVal);
        if(v <= minVal) {
            v = v +minVal;
        }
        return v;
    }

    /**
     * 指定范围闲置端口随机数
     * @param minPort 最小值
     * @param maxPort 最大值
     * @return 整数
     */
    public static int getRandomFreePort(int minPort, int maxPort) {
        Random random = new Random();
        int port;
        int size=(maxPort-minPort);
        while (size>0) {
            size=size-1;
            port = random.nextInt(maxPort - minPort) + minPort;
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.close();
                return port;
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }



}
