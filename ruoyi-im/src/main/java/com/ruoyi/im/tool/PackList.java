package com.ruoyi.im.tool;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Pack类实现包整体结构
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class PackList implements Serializable {//过程组包
    private ArrayList arrayList=new ArrayList();//声明全局变量
    public void empty(){//创建byte[]=null
        this.arrayList=new ArrayList();
    }
    public ArrayList getAll(){//返回当前字节
        return this.arrayList;
    }

    public void add(ArrayList o){//直接拼接到全局变量bytes
        this.arrayList.add(o);
    }
    public void set(ArrayList o){//直接拼接到全局变量bytes
        this.arrayList=o;
    }



}
