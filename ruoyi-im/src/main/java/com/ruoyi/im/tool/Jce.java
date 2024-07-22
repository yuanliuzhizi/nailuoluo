package com.ruoyi.im.tool;

import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;
import com.ruoyi.im.utils.ByteUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * jce
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class Jce implements Serializable {

    private Pack pack;

    public Jce(){//初始化
        this.pack=new Pack();
    }
    public void clear(){//清空
        this.pack.empty();
    }
    public byte[] allToByte(){
        return this.pack.getAll();
    }
    public void wrap(byte[] bin){
        this.pack.setBin(bin);
    }
    private void writeHead(byte p_val,int p_tag){//头
        if(p_tag>=15){
            this.pack.setByte((byte)(p_val|240));//填充
            this.pack.setByte((byte)p_tag);
        }else {
            this.pack.setByte((byte)(p_val|(p_tag<<4)));//左移
        }
    }
    public void writeObj(byte p_type,byte[] p_val,int p_tag){//多种结构
        switch (p_type){
            case 0:
                this.writeByte((byte) ByteToAll.byteToInt(p_val), p_tag);
                break;
            case 1:
                this.writeShort((short) ByteToAll.byteToInt(p_val), p_tag);
                break;
            case 2:
                this.writeInt(ByteToAll.byteToInt(p_val), p_tag);
                break;
            case 3:
                this.writeLong(ByteToAll.byteToInt(p_val), p_tag);
                break;
            case 13:
                this.writeSimpleList(p_val, p_tag);
                break;
            case 8:
                System.err.println("error can't write map");
                break;
            case 6:
            case 7:
                this.writeStringByte (p_val, p_tag);
                break;
            case 9:
                this.writeList (p_val, p_tag);
                break;
            default:
                System.err.println("error WriteObj  type=" + p_type);
                break;
        }
    }
    public void writeByte(byte p_val,int p_tag){//字节
        if(p_val == 0){
            this.writeHead((byte)12,p_tag);
        }else {
            this.writeHead((byte)0,p_tag);
            this.pack.setByte(p_val);
        }
    }
    public void writeShort(short p_val,int p_tag){//整数
        if (p_val <= 127 && p_val >= -128) {
            this.writeByte((byte)p_val,p_tag);
        }else {
            this.writeHead((byte)1,p_tag);
            this.pack.setShort(p_val);
        }
    }
    public void writeInt(int p_val, int p_tag){//整数
        if (p_val <= 32767 && p_val >= -32768) {
            this.writeShort((short) p_val, p_tag);
        } else {
            this.writeHead((byte) 2, p_tag);
            this.pack.setInt(p_val);
        }
    }
    public void writeLong(long p_val, int p_tag) {
        if (p_val <= 2147483647 && p_val >= -2147483648) {
            this.writeInt((int) p_val, p_tag);
        } else {
            this.writeHead((byte) 3, p_tag);
            this.pack.setBin(AllToByte.longToByte(p_val));
        }
    }
    public void writeByteString(String p_val, int p_tag) {
        byte[] t_val = AllToByte.hexToByte(p_val.trim());
        if (t_val.length > 255) {
            this.writeHead((byte) 7, p_tag);
            this.pack.setInt(t_val.length);
            this.pack.setBin(t_val);
        } else {
            this.writeHead((byte) 6, p_tag);
            this.pack.setByte((byte) t_val.length);
            this.pack.setBin(t_val);
        }
    }
    public void writeStringByte(byte[] p_val, int p_tag) {
        if (ByteUtils.subBytes(p_val,0,p_val.length - 1).length > 255) {
            this.writeHead((byte) 7, p_tag);
            this.pack.setInt(p_val.length);
            this.pack.setBin(p_val);
        } else {
            this.writeHead((byte) 6, p_tag);
            this.pack.setByte((byte) p_val.length);
            this.pack.setBin(p_val);
        }
    }
    public void writeJceStruct(byte[] p_val, int p_tag) {
        this.writeHead ((byte) 10, p_tag);
        pack.setBin (p_val);
        this.writeHead ((byte) 11, 0);
    }
    public void writeSimpleList(byte[] p_val, int p_tag) {
        this.writeHead ((byte) 13, p_tag);
        this.writeHead ((byte) 0, 0);
        this.writeInt(p_val.length,0);
        this.pack.setBin(p_val);
    }
    public void writeList(byte[] p_val, int p_tag) {
        this.writeHead ((byte) 9, p_tag);
        this.writeInt(p_val.length,0);
        for (byte b : p_val) {
            this.writeInt(b, 0);
        }
    }

    public void writeMap(byte[] p_val, int p_tag) {
        if(p_val.length == 0){
            this.writeHead((byte)8,p_tag);
            this.pack.setByte((byte)12);
        }


    }


    public void putHex(String hex){
        this.pack.setStr(hex);
    }




    /**
     *
     * @param data 十六进制jce数据
     * @return 本次解析数据和剩余数据
     */
    public static Map tagSimpleList2(String data){
        data=StringFilter(data);
        Map map=new HashMap();
        String judge=data.substring(0, 1).toUpperCase();
        BigInteger bigInteger=new BigInteger(judge,16);
        BigInteger flat=new BigInteger("15",10);
        if(bigInteger.compareTo(flat)==-1){//小于
            String tag=bigInteger.toString(10);
            String len=data.substring(4, 8).toUpperCase();
            BigInteger bigInteger1=new BigInteger(len,16);
            String len1=bigInteger1.toString(10);
            String v=data.substring(8,Integer.valueOf(len1)*2+8);
            map.put("tag",tag);
            map.put("v",v);
            int indexLen=data.indexOf(v)+v.length();
            if(data.length()>indexLen){
                map.put("remain",data.substring(indexLen));
            }else {
                map.put("remain","");
            }
            return map;

        }else {//大于不解析

        }

        return map;

    }



    /**
     * list型解析 到14
     * @param data 十六进制jce数据
     */
    public static Map tagList2(String data) {
        data = StringFilter(data);
        Map map = new HashMap();
        List list = new ArrayList();
        String judge = data.substring(0, 1).toUpperCase();
        BigInteger bigInteger = new BigInteger(judge, 16);
        BigInteger flat = new BigInteger("15", 10);
        if (bigInteger.compareTo(flat) == -1) {//小于
            String tag = bigInteger.toString(10);
            String v = data.substring(6);
            for (Integer i = 0; i <2; i++) {
                int s = v.indexOf("0A");
                int e = v.indexOf("290C3C0B") + 8;
                String vDate = v.substring(s, e);
                v = v.substring(e);
                list.add(vDate);
            }
            map.put("tag", tag);
            map.put("list", list);

            String index= (String) list.get(list.size()-1);
            int indexLen=data.indexOf(index)+index.length();

            if(data.length()>indexLen){
                map.put("remain",data.substring(indexLen));
            }else {
                map.put("remain","");
            }
            return map;

        } else {//大于不解析

        }

        return map;
    }


    public static String goLong(String data,int n){
        data = StringFilter(data);
        for (int i = 0; i < n; i++) {
            Map map=tagLong(data);
            data= (String) map.get("remain");
        }

        return data;
    }

    public static String goString(String data,int n){
        data = StringFilter(data);
        for (int i = 0; i < n; i++) {
            Map map=tagString(data);
            data= (String) map.get("remain");
        }

        return data;
    }

    public static String goList(String data,int n){
        data = StringFilter(data);
        for (int i = 0; i < n; i++) {
            Map map=tagList(data);
            data= (String) map.get("remain");
        }

        return data;
    }



    /**
     * Map型解析
     * @param data 十六进制jce数据
     */
    public static Map tagMap(String data) {
        data = StringFilter(data);
        Map map = new HashMap();
        String judge = data.substring(0, 1).toUpperCase();
        BigInteger bigInteger = new BigInteger(judge, 16);
        BigInteger flat = new BigInteger("15", 10);
        if (bigInteger.compareTo(flat) == -1) {//小于
            String tag=bigInteger.toString(10);
            String len=data.substring(2, 6).toUpperCase();
            BigInteger bigInteger1=new BigInteger(len,16);
            String len1=bigInteger1.toString(10);

            map.put("tag",tag);
            map.put("v",len1);

            if(data.length()>6){
                map.put("remain",data.substring(6));
            }else {
                map.put("remain","");
            }

            return map;
        } else {//大于不解析

        }

        return map;
    }

    /**
     * 去首尾
     * @param data 十六进制jce数据
     * @return
     */
    public static String tagSE(String data) {
        return data.substring(2,data.length()-2);
    }


    /**
     * 字符串型解析
     * @param data 十六进制jce数据
     */
    public static Map tagString(String data) {
        data = StringFilter(data);
        Map map = new HashMap();
        String judge = data.substring(0, 1).toUpperCase();
        BigInteger bigInteger = new BigInteger(judge, 16);
        BigInteger flat = new BigInteger("15", 10);
        if (bigInteger.compareTo(flat) == -1) {//小于
            String tag=bigInteger.toString(10);
            String len=data.substring(2, 4).toUpperCase();
            BigInteger bigInteger1=new BigInteger(len,16);
            String len1=bigInteger1.toString(10);
            String v=data.substring(4, Integer.valueOf(len1)*2+4);

            map.put("tag",tag);
            map.put("v",v);
            int indexLen=data.indexOf(v)+v.length();
            if(data.length()>indexLen){
                map.put("remain",data.substring(indexLen));
            }else {
                map.put("remain","");
            }
            return map;
        } else {//大于不解析

        }

        return map;
    }




    /**
     * SimpleList型解析 到14
     * @param data 十六进制jce数据
     * @return 本次解析数据和剩余数据
     */
    public static Map tagSimpleList(String data){
        data=StringFilter(data);
        Map map=new HashMap();
        String judge=data.substring(0, 1).toUpperCase();
        BigInteger bigInteger=new BigInteger(judge,16);
        BigInteger flat=new BigInteger("15",10);
        if(bigInteger.compareTo(flat)==-1){//小于
            String tag=bigInteger.toString(10);
            String len=data.substring(6, 10).toUpperCase();
            BigInteger bigInteger1=new BigInteger(len,16);
            String len1=bigInteger1.toString(10);
            String v=data.substring(10,Integer.valueOf(len1)*2+10);
            map.put("tag",tag);
            map.put("v",v);
            int indexLen=data.indexOf(v)+v.length();
            if(data.length()>indexLen){
                map.put("remain",data.substring(indexLen));
            }else {
                map.put("remain","");
            }
            return map;

        }else {//大于不解析

        }

        return map;

    }






    /**
     * list型解析 到14
     * @param data 十六进制jce数据
     */
    public static Map tagList(String data) {
        data = StringFilter(data);
        Map map = new HashMap();
        List list = new ArrayList();
        String judge = data.substring(0, 1).toUpperCase();
        BigInteger bigInteger = new BigInteger(judge, 16);
        BigInteger flat = new BigInteger("15", 10);
        if (bigInteger.compareTo(flat) == -1) {//小于
            String tag = bigInteger.toString(10);
            String len = data.substring(2, 6).toUpperCase();
            BigInteger len1 = new BigInteger(len, 16);
            String v = data.substring(6);
            for (Integer i = 0; i < Integer.valueOf(len1.toString(10)); i++) {
                int s = v.indexOf("0A");
                int e = v.indexOf("0B") + 2;
                String vDate = v.substring(s, e);
                v = v.substring(e);
                list.add(vDate);
            }
            map.put("tag", tag);
            map.put("list", list);

            String index= (String) list.get(list.size()-1);
            int indexLen=data.indexOf(index)+index.length();

            if(data.length()>indexLen){
                map.put("remain",data.substring(indexLen));
            }else {
                map.put("remain","");
            }
            return map;

        } else {//大于不解析

        }

        return map;
    }


    /**
     * 整数型解析
     * @param data 十六进制jce数据
     * @return 本次解析数据和剩余数据
     */
    public static Map tagLong(String data){
        data=StringFilter(data);
        Map map=new HashMap();
        String judge=data.substring(0, 1).toUpperCase();
        String zero=data.substring(1, 2).toUpperCase();
        if ("C".equals(zero)) {
            map.put("tag",judge);
            map.put("v",judge+zero);
            if(data.length()>2){
                map.put("remain",data.substring(2));
            }else {
                map.put("remain","");
            }
            return map;
        }
        BigInteger bigInteger=new BigInteger(judge,16);
        BigInteger flat=new BigInteger("15",10);
        if(bigInteger.compareTo(flat)==-1){
            String type=data.substring(1, 2).toUpperCase();
            BigInteger bigInteger1=new BigInteger(type,16);
            BigInteger bigInteger2=new BigInteger("2",10);
            int len=bigInteger2.pow(bigInteger1.intValue()).intValue()*2;//位长度

            String v=data.substring(2,2+len).toUpperCase();
            BigInteger bigInteger3=new BigInteger(v,16);

            map.put("tag",bigInteger.toString(10));
            map.put("v",bigInteger3.toString(10));

            int indexLen=data.indexOf(bigInteger3.toString(10))+bigInteger3.toString(10).length();

            if(data.length()>indexLen){
                map.put("remain",data.substring(indexLen));
            }else {
                map.put("remain","");
            }
            return map;

        }else {//大于不解析
//            String type=data.substring(1, 2).toUpperCase();
//            BigInteger bigInteger1=new BigInteger(type,16);
//            BigInteger bigInteger2=new BigInteger("2",10);
//            int len=bigInteger2.pow(bigInteger1.intValue()).intValue()*2;//位长度
//
//            String t=data.substring(2,4).toUpperCase();
//            BigInteger bigInteger3=new BigInteger(t,16);
//            System.out.println("头："+bigInteger3.toString(10));
//
//            String v=data.substring(4,4+len).toUpperCase();
//            BigInteger bigInteger4=new BigInteger(v,16);
//
//            System.out.println("内容："+bigInteger4.toString(10));
        }

        return map;
    }



//、、(i)=整数 (f)=小数 (d)=双精度 (b)=数据
    //0a b 1
    //12 b 2
    //1a b 3
    //20 i 4
    //28 i 5
    //32 b 6
    //40 i 8



    /**
     * 去掉指定特殊字符
     * @param str
     * @return
     */
    public static String StringFilter(String str)throws PatternSyntaxException {
        // String   regEx  =  "[^a-zA-Z0-9]"; // 只允许字母和数字
        // 清除掉所有特殊字符(除了~之外)
        String regEx="[`!@#$%^&*()+=|{}':;',//[//].<>/?！@#￥%……&*（）——+|{}【】‘；：”“’。，、？[g-z][G-Z] \n\t\r\\s*]";
        Pattern pattern   =   Pattern.compile(regEx);
        Matcher matcher   =   pattern.matcher(str);
        return   matcher.replaceAll("").trim();
    }


}
