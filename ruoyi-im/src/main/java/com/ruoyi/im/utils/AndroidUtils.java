package com.ruoyi.im.utils;

import java.util.Random;

/**
 * 安卓设备参数生成工具
 *
 */
public class AndroidUtils {

    public static String getImei() {
        StringBuilder stringBuffer = new StringBuilder();
        String imeiTAC="865166";
        String imeiFAC="02";
        int v = (int) (Math.random() * 999999);
        if(v <= 100001) {
            v = v +100001;
        }
        String random4=Integer.toString(v);
        stringBuffer.append(imeiTAC);
        stringBuffer.append(imeiFAC);
        stringBuffer.append(random4);
        String imeiString = stringBuffer.toString();
        char[] imeiChar=imeiString.toCharArray();

        int resultInt=0;

        for (int i = 0; i < imeiChar.length; i++) {

            int a=Integer.parseInt(String.valueOf(imeiChar[i]));

            i++;

            int temp=Integer.parseInt(String.valueOf(imeiChar[i]))*2;

            int b=temp<10?temp:temp-9;

            resultInt+=a+b;

        }

        resultInt%=10;

        resultInt=resultInt==0?0:10-resultInt;

        return imeiString+resultInt;
    }

    public static String getAndroidId() {
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            int num = random.nextInt(16);
            result.append(Integer.toHexString(num));
        }
        return result.toString();
    }

    public static String getImsi() {
        String imsiMCC = "460";
        String imsiMNC = "00";
        String imsiMSIN = "061323";

        int random4 = new Random().nextInt(8999) + 1000;
        String imsi = imsiMCC + imsiMNC + imsiMSIN + random4;
        return imsi;
    }


    public static String getMac() {
        String[] char1 = {"A", "B", "C", "D", "E", "F"};
        String[] char2 = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder mBuffer = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int t = random.nextInt(char1.length);
            int y = random.nextInt(char2.length);
            int key = random.nextInt(2);
            if (key == 0) {
                mBuffer.append(char2[y]).append(char1[t]);
            } else {
                mBuffer.append(char1[t]).append(char2[y]);
            }
            if (i != 5) {
                mBuffer.append(":");
            }
        }
        return mBuffer.toString();
    }


    public static String getBssId() {
        String[] char1 = {"a", "b", "c", "d", "e", "f"};
        String[] char2 = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder mBuffer = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int t = random.nextInt(char1.length);
            int y = random.nextInt(char2.length);
            int key = random.nextInt(2);
            if (key == 0) {
                mBuffer.append(char2[y]).append(char1[t]);
            } else {
                mBuffer.append(char1[t]).append(char2[y]);
            }
            if (i != 5) {
                mBuffer.append(":");
            }
        }
        return mBuffer.toString();
    }

}
