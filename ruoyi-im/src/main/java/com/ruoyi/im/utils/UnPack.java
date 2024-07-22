package com.ruoyi.im.utils;

/**
 * Pack类实现包整体结构
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class UnPack {
    private byte[] bytes;//声明全局变量
    public void setData(byte[] bin){this.bytes=bin;}
    public int getInt(){
        if(this.bytes==null){
            return 0;
        }
        byte[] bin=ByteUtils.subBytes(this.bytes,0,4);
        this.bytes=ByteUtils.subBytes(this.bytes,4,this.bytes.length-4);
        return ByteToAll.byteToInt(bin);
    }
    public byte[] getBin(int len){
        len = len > this.bytes.length ? this.bytes.length : len;
        byte[] bin = ByteUtils.subBytes(this.bytes,0,len);
        this.bytes =ByteUtils.subBytes(this.bytes,len,this.bytes.length-len);
        return bin;
    }
    public byte getByte(){
        byte[] bin = ByteUtils.subBytes(this.bytes,0,1);
        this.bytes =ByteUtils.subBytes(this.bytes,1,this.bytes.length-1);
        if(bytes.length<1){
            return 0;
        }
        return bin[0];
    }
    public short getShort(){
        byte[] bin = ByteUtils.subBytes(this.bytes,0,2);
        this.bytes=ByteUtils.subBytes(this.bytes,2, this.bytes.length-2);
        return (short)ByteToAll.byteToShort(bin);
    }
    public byte[] getAll() {
        return this.bytes;
    }

}
