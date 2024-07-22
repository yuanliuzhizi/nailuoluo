package com.ruoyi.im.tool;


import com.ruoyi.im.utils.AllToByte;

/**
 * 扫码算法
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class ScanEmpower {

    public static byte[] dec(String miYao,int size){
        byte[] key;
        byte[] bArr2;
        int i5;
        byte b2;
        int remainder;
        byte[] a_sm;
        key = AllToByte.textToByte(miYao);
        int len=key.length;
        bArr2 = new byte[size];
        a_sm = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 63, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};


        for (int i1 = 0; i1 < len; i1++) {

            i5 = key[i1];
            if (i5 == 32) {
                i5 = 42;
            }
            b2 = (byte) a_sm[i5];
            remainder = i1 % 4;
            if (remainder==0){
                int index=((i1/4)*3);
                bArr2[index] = (byte) (b2 << 2);
            }else if (remainder==1){
                int index=((i1/4)*3);
                int index1=index+1;
                bArr2[index] = (byte) (bArr2[index] | (b2 >> 4));
                bArr2[index1] = (byte) ((b2 & 15) << 4);
            }else if (remainder==2){
                int index=((i1/4)*3)+1;
                int index1=index+1;
                bArr2[index] = (byte) (bArr2[index] | (b2 >> 2));
                bArr2[index1] = (byte) ((b2 & 3) << 6);
            }else if (remainder==3){
                int index=((i1/4)*3)+2;
                bArr2[index] = (byte) (bArr2[index] | b2);
            }

        }

        return bArr2;

    }

}
