package com.ruoyi.im.tool;

import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;
import com.ruoyi.im.utils.NumericConvertUtil;
import com.ruoyi.im.utils.UnPack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JCEUnpack {


    public ByteBuffer bs;
    protected String sServerEncoding = "GBK";


    public static class HeadData {
        public int tag;
        public byte type;

        public void clear() {
            this.type = (byte) 0;
            this.tag = 0;
        }
    }


    public JCEUnpack(ByteBuffer byteBuffer) {
        this.bs = byteBuffer;
    }


    public JCEUnpack(byte[] bArr) {
        this.bs = ByteBuffer.wrap(bArr);
    }


    public JCEUnpack(byte[] bArr, int i) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        this.bs = wrap;
        wrap.position(i);
    }

    public void warp(byte[] bArr) {
        wrap(bArr);
    }

    public void wrap(byte[] bArr) {
        this.bs = ByteBuffer.wrap(bArr);
    }



    /**
     *  返回tag长度
     * @param headData
     * @param byteBuffer
     * @return
     */
    public static int readHead(HeadData headData,ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        headData.type = (byte) (b & 15);
        headData.tag = (b & 240) >> 4;
        if (headData.tag != 15) {
            return 1;
        }
        byte b1 = byteBuffer.get();
        headData.tag = b1 & 255;
        return 2;
    }





    /**
     * 解析整数类型
     * @param byteBuffer
     * @return
     */
    public Map readNumber(ByteBuffer byteBuffer) {
        UnPack unPack=new UnPack();
        Map map=new HashMap();
        HeadData headData=new HeadData();
        int tagLen=readHead(headData,byteBuffer);
        byte b = headData.type;
        if (b == (byte) 0) {
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            unPack.getByte();
            map.put("tag",headData.tag);
            map.put("v",this.bs.get());
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;
        } else if (b == (byte) 1) {
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            unPack.getShort();
            map.put("tag",headData.tag);
            map.put("v",this.bs.getShort());
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;
        } else if (b == (byte) 2) {
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            unPack.getInt();
            map.put("tag",headData.tag);
            map.put("v",this.bs.getInt());
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;
        } else if (b == (byte) 3) {
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            unPack.getBin(8);
            map.put("tag",headData.tag);
            map.put("v",this.bs.getLong());
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;
        } else {
            if (b == (byte) 12) {//zero
                unPack.setData(byteBuffer.array());
                unPack.getBin(tagLen);
                map.put("tag",headData.tag);
                map.put("v",0);
                map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
                return map;
            }
//                System.out.println("type mismatch.");
            return null;
        }
    }


    /**
     * 小数类型
     * @param byteBuffer
     * @return
     */
    public Map readDecimals(ByteBuffer byteBuffer) {
        UnPack unPack=new UnPack();
        Map map=new HashMap();
        HeadData headData=new HeadData();
        int tagLen=readHead(headData,byteBuffer);
        byte b = headData.type;
        if (b == (byte) 4) {
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            unPack.getBin(4);
            map.put("tag",headData.tag);
            map.put("v",this.bs.getFloat());
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;
        }
        if (b == (byte) 5) {
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            unPack.getBin(8);
            map.put("tag",headData.tag);
            map.put("v",this.bs.getDouble());
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;
        }
        if (b == (byte) 12) {
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            map.put("tag",headData.tag);
            map.put("v",0.0d);
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;
        }
//            System.out.println("type mismatch.");
        return null;
    }


    /**
     * 文本类型
     * @param byteBuffer
     * @return
     */
    public Map readString(ByteBuffer byteBuffer) {
        UnPack unPack=new UnPack();
        unPack.setData(byteBuffer.array());
        Map map=new HashMap();
        HeadData headData=new HeadData();
        int tagLen=readHead(headData,byteBuffer);
        byte b = headData.type;
        int i2;
        byte[] bArr;
        if (b == (byte) 6) {
            i2 = this.bs.get();
            if (i2 < 0) {
                i2 += 256;
            }
            bArr = new byte[i2];
            this.bs.get(bArr);
            unPack.getBin(tagLen);
            unPack.getByte();
            unPack.getBin(bArr.length);
            map.put("tag",headData.tag);
            map.put("v", ByteToAll.byteToHxe(bArr));
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;

        } else if (b == (byte) 7) {
            i2 = this.bs.getInt();
            if (i2 > 104857600 || i2 < 0 || i2 > this.bs.capacity()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("String too long: ");
                stringBuilder.append(i2);
//                    System.out.println(stringBuilder.toString());
                return null;
            }
            bArr = new byte[i2];
            this.bs.get(bArr);
            unPack.getBin(tagLen);
            unPack.getInt();
            unPack.getBin(bArr.length);
            map.put("tag",headData.tag);
            map.put("v", ByteToAll.byteToHxe(bArr));
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;
        }
//            else {
//                System.out.println("type mismatch.");
//            }

        return null;

    }

    /**
     *  mpa类型 分为map头(tag) 加<K,V>两个数据 解析
     * @param byteBuffer
     * @return
     */
    public Map readMapSimpleList(ByteBuffer byteBuffer) {
        UnPack unPack=new UnPack();
        unPack.setData(byteBuffer.array());
        Map map=new HashMap();
        HeadData headData=new HeadData();
        int tagLen=readHead(headData,byteBuffer);
        if (headData.type == (byte) 8) {//如果是map类型则解析2次,具体的结构看返回而定这里是 String , SimpleList
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            unPack.getBin(2);

            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map m1=jceUnpack.readString(jceUnpack.bs);

            String simpleList = (String) m1.get("remain");

            map.put("tag",headData.tag);
            map.put("v",m1);
            map.put("remain", simpleList);
            return map;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("size invalid: ");
            System.out.println(stringBuilder.toString());
            return null;

        }
    }

    /**
     *  mpa类型 分为map头(tag) 加<K,V>两个数据 解析
     * @param byteBuffer
     * @return
     */
    public Map readMap(ByteBuffer byteBuffer) {
        UnPack unPack=new UnPack();
        unPack.setData(byteBuffer.array());
        Map map=new HashMap();
        HeadData headData=new HeadData();
        int tagLen=readHead(headData,byteBuffer);
        if (headData.type == (byte) 8) {//如果是map类型则解析2次,具体的结构看返回而定这里是 String , SimpleList
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            unPack.getBin(2);

            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map m1=jceUnpack.readString(jceUnpack.bs);

            String simpleList = (String) m1.get("remain");

            map.put("tag",headData.tag);
            map.put("v",m1);
            map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));
            return map;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("size invalid: ");
//            stringBuilder.append(read);
            System.out.println(stringBuilder.toString());
            return null;

        }
//        System.out.println("type mismatch.");
//        return hashMap;
    }


    /**
     * Simplelist 类型解析
     * @param byteBuffer
     * @return
     */
    public Map readSimpleList(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 13) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            unPack.getByte();//一个0头(head)体这是固定的
            HeadData headData2 = new HeadData();
            int tagLen2 = readHead(headData2, jceUnpack.bs);
            if (headData2.type == (byte) 0) {//有一个空"00" ,有一个jce int的长度
                jceUnpack=new JCEUnpack(unPack.getAll());
                Map map1=jceUnpack.readNumber(jceUnpack.bs);
                int l=Integer.parseInt(map1.get("v").toString());
                unPack.getByte();//一个0头(head)体这是固定的 ,所以不用tagLen2
                if (l < -32768 || l > 32767) {//int
                    unPack.getInt();//这里是int型的长度
                }else if (l < (short) -128 || l > (short) 127) {//short
                    unPack.getShort();//这里是int型的长度
                }else if (l == (byte) 0) {//zero

                }else {//byte
                    unPack.getByte();//这里是int型的长度
                }
                unPack.getBin(l);
                map.put("tag",tag);
                map.put("v",(map1.get("remain").toString()).substring(0,2*l));//remain
                map.put("remain", ByteToAll.byteToHxe(unPack.getAll()));

                return map;
            }

        } else {
            System.out.println("type mismatch.");
        }
        return null;
    }


    /**
     * list类型 需要单独解析这里是群列表类
     * @param byteBuffer
     * @return
     */
    public Map readGroupList(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");

            List list=new ArrayList<>();//list类型大小,内部的参数

            for (int j = 0; j < (byte)map1.get("v"); j++) {
                List list1=new ArrayList<>();//内部的参数,用户账号在线状态
                listUser=listUser.substring(2);//OA
                for (int i = 0; i < 21; i++) {
                    if (i<7) {
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==7){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<14){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==14){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<17){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else {
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }

                }
                list.add(list1);
                listUser=listUser.substring(2);//OB
            }
            listUser=listUser.substring(2);//OB

//            map.put("tag",tag);
            map.put("v",list);
//            map.put("remain",listUser);
            return map;

        } else {
            System.out.println("type mismatch.");
        }

        return null;


    }

    /**
     * list类型 需要单独解析这里是好友列表类
     * @param byteBuffer
     * @return
     */
    public Map readListAll(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");

            List list=new ArrayList<>();//list类型大小,内部的参数

            for (int j = 0; j < (byte)map1.get("v"); j++) {
                List list1=new ArrayList<>();//内部的参数,用户账号在线状态
                listUser=listUser.substring(2);//OA
                for (int i = 0; i < 41; i++) {
                    if (i<4) {//0-3
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<6){//
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<38){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==38){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else{
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }

                }
                list.add(list1);
                listUser=listUser.substring(2);//OB
            }
//            listUser=listUser.substring(2);//OB

//            map.put("tag",tag);
            map.put("v",list);
//            map.put("remain",listUser);
            return map;

        } else {
            System.out.println("type mismatch.");
        }

        return null;


    }







//    public Map readListAll(ByteBuffer byteBuffer) {
//        UnPack unPack = new UnPack();
//        unPack.setData(byteBuffer.array());
//        Map map = new HashMap();
//        HeadData headData = new HeadData();
//        int tagLen = readHead(headData, byteBuffer);
//        byte b = headData.type;
//        int tag = headData.tag;
//        if (b == (byte) 9) {
//            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
//            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
//            Map map1=jceUnpack.readNumber(jceUnpack.bs);
//
//            String listUser= (String) map1.get("remain");
//
//            List list=new ArrayList<>();//list类型大小,内部的参数
//
//            for (int j = 0; j < (byte)map1.get("v"); j++) {
//                System.out.println(j);
//                System.out.println(listUser);
//                List list1=new ArrayList<>();//内部的参数,用户账号在线状态
//                listUser=listUser.substring(2);//OA
//                for (int i = 0; i < 39; i++) {
//                    if (i<4) {
//                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
//                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
//                        list1.add(map2.get("v"));
//                        listUser = (String) map2.get("remain");
//                    }else if(i<6){
//                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
//                        Map map2 = jceUnpack.readString(jceUnpack.bs);
//                        list1.add(map2.get("v"));
//                        listUser = (String) map2.get("remain");
//                    }else if(i<38){
//                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
//                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
//                        list1.add(map2.get("v"));
//                        listUser = (String) map2.get("remain");
//                    }else {
//                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
//                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
//                        list1.add(map2.get("v"));
//                        listUser = (String) map2.get("remain");
//                    }
//
//                }
//                list.add(list1);
//                listUser=listUser.substring(2);//OB
//            }
//            listUser=listUser.substring(2);//OB
//
////            map.put("tag",tag);
//            map.put("v",list);
////            map.put("remain",listUser);
//            return map;
//
//        } else {
//            System.out.println("type mismatch.");
//        }
//
//        return null;
//
//
//    }


    /**
     * list类型 获取获取上传host
     * @param byteBuffer
     * @return
     */
    public Map readListConfigPushSvc(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");


            List list=new ArrayList<>();//list类型大小,内部的参数
            Object o=map1.get("v");


            for (int j = 0; j < Integer.parseInt(o.toString()); j++) {//41
                List list1=new ArrayList<>();//内部的参数,用户账号在线状态
                listUser=listUser.substring(2);//OA
                jceUnpack.wrap(AllToByte.hexToByte(listUser));
                Map map2=jceUnpack.readString(jceUnpack.bs);//host
                String host= (String) map2.get("v");
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                Map map3=jceUnpack.readNumber(jceUnpack.bs);//port
                Object port= map3.get("v");
                listUser=((String)map3.get("remain")).substring(2);//0B
                list1.add(host);
                list1.add(port);
                list.add(list1);
            }



            map.put("v",list);
            map.put("remain",listUser);
            return map;


        } else {
            System.out.println("type mismatch.");
        }

        return null;


    }


    /**
     * list类型 获取获取上传host
     * @param byteBuffer
     * @return
     */
    public Map readListConfigPushSvcJ5(ByteBuffer byteBuffer) {
        List list = new ArrayList();
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");



            Object o=map1.get("v");


            for (int j = 0; j < Integer.parseInt(o.toString()); j++) {//41



                if (j<2){
                    listUser=listUser.substring(2);//OA
                    jceUnpack.wrap(AllToByte.hexToByte(listUser));
                    Map map2=jceUnpack.readNumber(jceUnpack.bs);//一个整数类型
                    jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                    Map map3=jceUnpack.readListConfigPushSvcJ5_l(jceUnpack.bs);//第二层的解析


                    listUser=((String)map3.get("remain")).substring(2);//0B
//                list1.add(host);
//                list1.add(port);

                }else if(j==2){
                    listUser=listUser.substring(2);//OA
                    jceUnpack.wrap(AllToByte.hexToByte(listUser));
                    Map map2=jceUnpack.readNumber(jceUnpack.bs);//一个整数类型
                    jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                    Map map3=jceUnpack.readListConfigPushSvcJ5_2(jceUnpack.bs);//第二层的解析
                    list = (List) map3.get("v");
                    jceUnpack.wrap(AllToByte.hexToByte((String) map3.get("remain")));
                    Map map4=jceUnpack.readListConfigPushSvcJ5_3(jceUnpack.bs);//第二层的解析
                    listUser=((String)map4.get("remain")).substring(2);//0B

                }else{
                    listUser=listUser.substring(2);//OA
                    jceUnpack.wrap(AllToByte.hexToByte(listUser));
                    Map map2=jceUnpack.readNumber(jceUnpack.bs);//一个整数类型
                    jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                    Map map3=jceUnpack.readListConfigPushSvcJ5_l(jceUnpack.bs);//第二层的解析


                    listUser=((String)map3.get("remain")).substring(2);//0B
                }


            }


            map.put("v",list);
            map.put("remain",listUser);
            return map;


        } else {
            System.out.println("readListConfigPushSvcJ5type mismatch.");
        }

        return null;


    }



    /**
     * list类型 获取获取上传host
     * @param byteBuffer
     * @return
     */
    public Map readListConfigPushSvcJ5_l(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");


            List list=new ArrayList<>();//list类型大小,内部的参数
            Object o=map1.get("v");


            for (int j = 0; j < Integer.parseInt(o.toString()); j++) {//41
                List list1=new ArrayList<>();//内部的参数,用户账号在线状态
                listUser=listUser.substring(2);//OA
                jceUnpack.wrap(AllToByte.hexToByte(listUser));
                Map map2=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));

                Map map3=jceUnpack.readString(jceUnpack.bs);//host
                String host= (String) map3.get("v");
                jceUnpack.wrap(AllToByte.hexToByte((String) map3.get("remain")));
                Map map4=jceUnpack.readNumber(jceUnpack.bs);//port
                Object port= map4.get("v");

                listUser=((String)map4.get("remain")).substring(2);//0B
                list1.add(host);
                list1.add(port);
                list.add(list1);
            }

            jceUnpack.wrap(AllToByte.hexToByte(listUser.substring(4)));//290C

            Map map2=jceUnpack.readNumber(jceUnpack.bs);

            map.put("v",list);
            map.put("remain",map2.get("remain"));
            return map;


        } else {
            System.out.println("readListConfigPushSvcJ5_l type mismatch.");
        }

        return null;


    }

    /**
     * list类型 获取获取上传host
     * @param byteBuffer
     * @return
     */
    public Map readListConfigPushSvcJ5_2(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");


            List list=new ArrayList<>();//list类型大小,内部的参数
            Object o=map1.get("v");


            for (int j = 0; j < Integer.parseInt(o.toString()); j++) {//41
                List list1=new ArrayList<>();//内部的参数,用户账号在线状态
                listUser=listUser.substring(2);//OA

                jceUnpack.wrap(AllToByte.hexToByte(listUser));
                Map map2=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));

                Map map3=jceUnpack.readString(jceUnpack.bs);//host
                String host= (String) map3.get("v");
                jceUnpack.wrap(AllToByte.hexToByte((String) map3.get("remain")));
                Map map4=jceUnpack.readNumber(jceUnpack.bs);//port
                Object port= map4.get("v");

                listUser=((String)map4.get("remain")).substring(2);//0B
                list1.add(host);
                list1.add(port);
                list.add(list1);
            }

            map.put("v",list);
            map.put("remain",listUser);
            return map;


        } else {
            System.out.println("readListConfigPushSvcJ5_l type mismatch.");
        }

        return null;


    }

    /**
     * list类型 获取获取上传host
     * @param byteBuffer
     * @return
     */
    public Map readListConfigPushSvcJ5_3(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");


            Object o=map1.get("v");


            for (int j = 0; j < Integer.parseInt(o.toString()); j++) {//41
                listUser=listUser.substring(2);//OA

                jceUnpack.wrap(AllToByte.hexToByte(listUser));
                Map map2=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));

                Map map3=jceUnpack.readNumber(jceUnpack.bs);//

                jceUnpack.wrap(AllToByte.hexToByte((String) map3.get("remain")));
                Map map4=jceUnpack.readNumber(jceUnpack.bs);//

                jceUnpack.wrap(AllToByte.hexToByte((String) map4.get("remain")));
                Map map5=jceUnpack.readNumber(jceUnpack.bs);//

                listUser=((String)map5.get("remain")).substring(2);//0B
            }
            jceUnpack.wrap(AllToByte.hexToByte(listUser));

            Map map2=jceUnpack.readNumber(jceUnpack.bs);
            map.put("v","");
            map.put("remain", map2.get("remain"));
            return map;


        } else {
            System.out.println("readListConfigPushSvcJ5_l type mismatch.");
        }

        return null;


    }




    /**
     * list类型 需要单独解析这里是在线好友列表类
     * @param byteBuffer
     * @return
     */
    public Map readUserList(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");

            List list=new ArrayList<>();//list类型大小,内部的参数

            for (int j = 0; j < (byte)map1.get("v"); j++) {
                List list1=new ArrayList<>();//内部的参数,用户账号在线状态
                listUser=listUser.substring(2);//OA
                for (int i = 0; i < 63; i++) {
                    if (i<3) {
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==3){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<12){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==12){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<14){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==14){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<16){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<18){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==18){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==19){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser.substring(4)));//FA13去掉头
                        Map map2 = jceUnpack.readUserMap(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==20){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==21){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<27){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==27){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<33){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<35){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<41){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==41){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<45){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==45){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<49){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==49){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readString(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<52){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==52){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<55){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==55){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i==56){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else if(i<59){
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readNumber(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }else {
                        jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
                        Map map2 = jceUnpack.readSimpleList(jceUnpack.bs);
                        list1.add(map2.get("v"));
                        listUser = (String) map2.get("remain");
                    }

                }
                list.add(list1);
                listUser=listUser.substring(2);//OB
            }
            listUser=listUser.substring(2);//OB

//            map.put("tag",tag);
            map.put("v",list);
//            map.put("remain",listUser);
            return map;

        } else {
            System.out.println("type mismatch.");
        }

        return null;


    }


    /**
     * list类型 需要单独解析这里是群列表类
     * @param byteBuffer
     * @return
     */
    public Map readList(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            //去掉头
            unPack.getByte();
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            //占位符）OA
            unPack.getShort();
            JCEUnpack jceUnpack = new JCEUnpack(unPack.getAll());
            Map map = jceUnpack.readNumber(jceUnpack.bs);
            return map;
        }

        return null;
    }

    /**
     * list类型 需要单独解析这里是群列表类
     * @param byteBuffer
     * @return
     */
    public Map readRevokeList(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");

            listUser=listUser.substring(2);//OA

            jceUnpack = new JCEUnpack(AllToByte.hexToByte(listUser));
            Map map2 = jceUnpack.readNumber(jceUnpack.bs);//撤回人
            jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
            Map map3=jceUnpack.readNumber(jceUnpack.bs);
            jceUnpack.wrap(AllToByte.hexToByte((String) map3.get("remain")));
            Map map4=jceUnpack.readNumber(jceUnpack.bs);//好友撤回 528  群 732
            String num=map4.get("v").toString();
            if("528".equals(num)){//好友撤回
                jceUnpack.wrap(AllToByte.hexToByte((String) map4.get("remain")));
                Map map5=jceUnpack.readNumber(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map5.get("remain")));
                Map map6=jceUnpack.readString(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map6.get("remain")));
                Map map7=jceUnpack.readNumber(jceUnpack.bs);//时间戳
                jceUnpack.wrap(AllToByte.hexToByte((String) map7.get("remain")));
                Map map8=jceUnpack.readSimpleList(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map8.get("v")));


                Map map9=jceUnpack.readNumber(jceUnpack.bs);

                String s=(String) map9.get("remain");
                jceUnpack.wrap(AllToByte.hexToByte(s.substring(2)));//16
                Map map10=jceUnpack.readNumber(jceUnpack.bs);//1
                jceUnpack.wrap(AllToByte.hexToByte((String) map10.get("remain")));
                Map map11=jceUnpack.readNumber(jceUnpack.bs);//2
                jceUnpack.wrap(AllToByte.hexToByte((String) map11.get("remain")));
                Map map12=jceUnpack.readNumber(jceUnpack.bs);//3
                jceUnpack.wrap(AllToByte.hexToByte((String) map12.get("remain")));
                Map map13=jceUnpack.readNumber(jceUnpack.bs);//4
                jceUnpack.wrap(AllToByte.hexToByte((String) map13.get("remain")));
                Map map14=jceUnpack.readNumber(jceUnpack.bs);//5
                jceUnpack.wrap(AllToByte.hexToByte((String) map14.get("remain")));
                Map map15=jceUnpack.readSimpleList(jceUnpack.bs);//6
                jceUnpack.wrap(AllToByte.hexToByte((String) map15.get("remain")));
                Map map16=jceUnpack.readSimpleList(jceUnpack.bs);//7
                jceUnpack.wrap(AllToByte.hexToByte((String) map16.get("remain")));
                Map map17=jceUnpack.readSimpleList(jceUnpack.bs);//8
                jceUnpack.wrap(AllToByte.hexToByte((String) map17.get("remain")));
                Map map18=jceUnpack.readSimpleList(jceUnpack.bs);//9
                jceUnpack.wrap(AllToByte.hexToByte((String) map18.get("remain")));
                Map map19=jceUnpack.readNumber(jceUnpack.bs);//10
                jceUnpack.wrap(AllToByte.hexToByte((String) map19.get("remain")));
                Map map20=jceUnpack.readNumber(jceUnpack.bs);//11
                jceUnpack.wrap(AllToByte.hexToByte((String) map20.get("remain")));
                Map map21=jceUnpack.readNumber(jceUnpack.bs);//12
                jceUnpack.wrap(AllToByte.hexToByte((String) map21.get("remain")));
                Map map22=jceUnpack.readNumber(jceUnpack.bs);//13
                jceUnpack.wrap(AllToByte.hexToByte((String) map22.get("remain")));
                Map map23=jceUnpack.readSimpleList(jceUnpack.bs);//14
                jceUnpack.wrap(AllToByte.hexToByte((String) map23.get("remain")));
                Map map24=jceUnpack.readNumber(jceUnpack.bs);//15
                jceUnpack.wrap(AllToByte.hexToByte((String) map24.get("remain")));
                Map map25=jceUnpack.readNumber(jceUnpack.bs);//16

                String s1=(String) map25.get("remain");
                jceUnpack.wrap(AllToByte.hexToByte(s1.substring(4)));//7
                Map map26=jceUnpack.readNumber(jceUnpack.bs);//1
                jceUnpack.wrap(AllToByte.hexToByte((String) map26.get("remain")));
                Map map27=jceUnpack.readNumber(jceUnpack.bs);//2
                jceUnpack.wrap(AllToByte.hexToByte((String) map27.get("remain")));
                Map map28=jceUnpack.readNumber(jceUnpack.bs);//3
                jceUnpack.wrap(AllToByte.hexToByte((String) map28.get("remain")));
                Map map29=jceUnpack.readNumber(jceUnpack.bs);//4
                jceUnpack.wrap(AllToByte.hexToByte((String) map29.get("remain")));
                Map map30=jceUnpack.readNumber(jceUnpack.bs);//5
                jceUnpack.wrap(AllToByte.hexToByte((String) map30.get("remain")));
                Map map31=jceUnpack.readNumber(jceUnpack.bs);//6
                jceUnpack.wrap(AllToByte.hexToByte((String) map31.get("remain")));
                Map map32=jceUnpack.readNumber(jceUnpack.bs);//7
                jceUnpack.wrap(AllToByte.hexToByte((String) map32.get("remain")));

                String s2=(String) map32.get("remain");
                jceUnpack.wrap(AllToByte.hexToByte(s2.substring(4)));//17
                Map map33=jceUnpack.readNumber(jceUnpack.bs);//1
                jceUnpack.wrap(AllToByte.hexToByte((String) map33.get("remain")));
                Map map34=jceUnpack.readNumber(jceUnpack.bs);//2
                jceUnpack.wrap(AllToByte.hexToByte((String) map34.get("remain")));
                Map map35=jceUnpack.readNumber(jceUnpack.bs);//3
                jceUnpack.wrap(AllToByte.hexToByte((String) map35.get("remain")));
                Map map36=jceUnpack.readNumber(jceUnpack.bs);//4
                jceUnpack.wrap(AllToByte.hexToByte((String) map36.get("remain")));
                Map map37=jceUnpack.readNumber(jceUnpack.bs);//5
                jceUnpack.wrap(AllToByte.hexToByte((String) map37.get("remain")));
                Map map38=jceUnpack.readNumber(jceUnpack.bs);//6
                jceUnpack.wrap(AllToByte.hexToByte((String) map38.get("remain")));
                Map map39=jceUnpack.readNumber(jceUnpack.bs);//7
                jceUnpack.wrap(AllToByte.hexToByte((String) map39.get("remain")));
                Map map40=jceUnpack.readNumber(jceUnpack.bs);//8
                jceUnpack.wrap(AllToByte.hexToByte((String) map40.get("remain")));
                Map map41=jceUnpack.readSimpleList(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map41.get("remain")));
                Map map42=jceUnpack.readSimpleList(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map42.get("remain")));
                Map map43=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map43.get("remain")));
                Map map44=jceUnpack.readSimpleList(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map44.get("remain")));
                Map map45=jceUnpack.readSimpleList(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map45.get("remain")));
                Map map46=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map46.get("remain")));
                Map map47=jceUnpack.readSimpleList(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map47.get("remain")));
                Map map48=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map48.get("remain")));
                Map map49=jceUnpack.readNumber(jceUnpack.bs);//


                String s3=(String) map49.get("remain");
                jceUnpack.wrap(AllToByte.hexToByte(s3.substring(4)));//10
                Map map50=jceUnpack.readNumber(jceUnpack.bs);//1
                jceUnpack.wrap(AllToByte.hexToByte((String) map50.get("remain")));
                Map map51=jceUnpack.readNumber(jceUnpack.bs);//2
                jceUnpack.wrap(AllToByte.hexToByte((String) map51.get("remain")));
                Map map52=jceUnpack.readNumber(jceUnpack.bs);//3
                jceUnpack.wrap(AllToByte.hexToByte((String) map52.get("remain")));
                Map map53=jceUnpack.readNumber(jceUnpack.bs);//4
                jceUnpack.wrap(AllToByte.hexToByte((String) map53.get("remain")));
                Map map54=jceUnpack.readNumber(jceUnpack.bs);//5
                jceUnpack.wrap(AllToByte.hexToByte((String) map54.get("remain")));
                Map map55=jceUnpack.readNumber(jceUnpack.bs);//6
                jceUnpack.wrap(AllToByte.hexToByte((String) map55.get("remain")));
                Map map56=jceUnpack.readNumber(jceUnpack.bs);//7
                jceUnpack.wrap(AllToByte.hexToByte((String) map56.get("remain")));
                Map map57=jceUnpack.readNumber(jceUnpack.bs);//8
                jceUnpack.wrap(AllToByte.hexToByte((String) map57.get("remain")));
                Map map58=jceUnpack.readNumber(jceUnpack.bs);//9
                jceUnpack.wrap(AllToByte.hexToByte((String) map58.get("remain")));
                Map map59=jceUnpack.readString(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map59.get("remain")));


                String s4=(String) map59.get("remain");
                jceUnpack.wrap(AllToByte.hexToByte(s4.substring(4)));//10

                Map map60=jceUnpack.readNumber(jceUnpack.bs);//1
                jceUnpack.wrap(AllToByte.hexToByte((String) map60.get("remain")));
                Map map61=jceUnpack.readNumber(jceUnpack.bs);//2
                jceUnpack.wrap(AllToByte.hexToByte((String) map61.get("remain")));
                Map map62=jceUnpack.readNumber(jceUnpack.bs);//3
                jceUnpack.wrap(AllToByte.hexToByte((String) map62.get("remain")));
                Map map63=jceUnpack.readNumber(jceUnpack.bs);//4
                jceUnpack.wrap(AllToByte.hexToByte((String) map63.get("remain")));
                Map map64=jceUnpack.readNumber(jceUnpack.bs);//5
                jceUnpack.wrap(AllToByte.hexToByte((String) map64.get("remain")));
                Map map65=jceUnpack.readNumber(jceUnpack.bs);//6
                jceUnpack.wrap(AllToByte.hexToByte((String) map65.get("remain")));
                Map map66=jceUnpack.readNumber(jceUnpack.bs);//7
                jceUnpack.wrap(AllToByte.hexToByte((String) map66.get("remain")));
                Map map67=jceUnpack.readNumber(jceUnpack.bs);//8
                jceUnpack.wrap(AllToByte.hexToByte((String) map67.get("remain")));
                Map map68=jceUnpack.readNumber(jceUnpack.bs);//9
                jceUnpack.wrap(AllToByte.hexToByte((String) map68.get("remain")));
                Map map69=jceUnpack.readSimpleList(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map69.get("remain")));



                String s5=(String) map69.get("remain");
                jceUnpack.wrap(AllToByte.hexToByte(s5.substring(4)));//6
                Map map70=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte(((String)map70.get("remain")).substring(2)));

                Map map71=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map71.get("remain")));
                Map map72=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map72.get("remain")));
                Map map73=jceUnpack.readString(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte(((String)map73.get("remain")).substring(4)));
                Map map74=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte(((String)map74.get("remain")).substring(4)));
                Map map75=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map75.get("remain")));
                Map map76=jceUnpack.readString(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte(((String)map76.get("remain")).substring(22)));//0B4A 090C 0B 5A 090C 0B 0B 7A  两个空list


                Map map77=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map77.get("remain")));
                Map map78=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map78.get("remain")));
                Map map79=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map79.get("remain")));
                Map map80=jceUnpack.readString(jceUnpack.bs);//

                jceUnpack.wrap(AllToByte.hexToByte(((String)map80.get("remain")).substring(4)));
                Map map81=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map81.get("remain")));
                Map map82=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map82.get("remain")));
                Map map83=jceUnpack.readNumber(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte((String) map83.get("remain")));
                Map map84=jceUnpack.readString(jceUnpack.bs);//
                jceUnpack.wrap(AllToByte.hexToByte(((String)map84.get("remain")).substring(10)));//0B 9A 090C 0B 1个空list


                Map map85=jceUnpack.readSimpleList(jceUnpack.bs);//

                if ("138".equals(map9.get("v").toString())){//好友撤回 138   同意成员入群68

                    List l=Protobuf.analysisAllPB((String) map85.get("v"));
                    Map mUser= (Map) l.get(0);
                    List l1=Protobuf.analysisAllPB((String) mUser.get("data"));

                    Map groupMsg = new HashMap();


                    Map userId= (Map) l1.get(0);//撤回人
                    Map userGo= (Map) l1.get(1);//接收人
                    Map msgReq= (Map) l1.get(2);//msgReq
                    Map msgTime= (Map) l1.get(4);//msgTime
                    Map msgGuid= (Map) l1.get(5);//msgGuid
                    groupMsg.put("tag", map9.get("lv"));
                    groupMsg.put("userId", userId.get("lv"));
                    groupMsg.put("userGo", userGo.get("lv"));
                    groupMsg.put("msgReq", msgReq.get("lv"));
                    groupMsg.put("msgTime", msgTime.get("lv"));
                    groupMsg.put("msgGuid", msgGuid.get("lv"));



                    map.put("user",map2.get("v"));//撤回人
                    map.put("tag",map4.get("v"));//好友撤回 528  群 732
                    map.put("v",groupMsg);
                    return map;
                }else if ("68".equals(map9.get("v").toString())){//好友撤回 138   同意成员入群68

                    Map groupMsg = new HashMap();
                    groupMsg.put("tag", map9.get("lv"));
                    groupMsg.put("v", map85.get("v"));
                    map.put("tag",map4.get("v"));
                    map.put("v", groupMsg);
                    return map;
                }else {
                    map.put("tag", map4.get("lv"));
                    return map;
                }





            }

            if("732".equals(num)) {//群撤回
                jceUnpack.wrap(AllToByte.hexToByte((String) map4.get("remain")));
                Map map5=jceUnpack.readNumber(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map5.get("remain")));
                Map map6=jceUnpack.readString(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) map6.get("remain")));
                Map map7=jceUnpack.readNumber(jceUnpack.bs);//时间戳
                jceUnpack.wrap(AllToByte.hexToByte((String) map7.get("remain")));
                Map map8=jceUnpack.readSimpleList(jceUnpack.bs);
                String s= (String) map8.get("v");
                //群号的十六进制占位
                String v=map2.get("v").toString();
                String g16= NumericConvertUtil.toOtherBaseString(Long.parseLong(v), 16);
                s=s.substring(g16.length());//
                Map type = new HashMap();
                type.put("lv",NumericConvertUtil.toDecimalism(s.substring(0,2),16)+"");//转成十进制

//                List l=Protobuf.analysisAllPB((s.substring(2)));//判断是什么类型的推
//                Map type = (Map) l.get(1);//12 群禁言的提示  17 群撤回消息的 20 群邀请时可以统计

                if("17".equals(type.get("lv"))){
//                    Map groupId= (Map) l.get(5);
//                    Map group= (Map) l.get(5);
//
//                    List l1=Protobuf.analysisAllPB((String) group.get("data"));
//
//                    Map msg= (Map) l1.get(1);
//
//                    List l2=Protobuf.analysisAllPB((String) msg.get("data"));
//
//
//                    Map groupMsg = new HashMap();
//
//
//                    Map msgReq= (Map) l2.get(0);//msgReq
//                    Map msgTime= (Map) l2.get(1);//msgTime
//                    Map msgGuid= (Map) l2.get(2);//msgGuid
//                    Map userId= (Map) l2.get(5);//撤回人
//                    groupMsg.put("msgReq", msgReq.get("lv"));
//                    groupMsg.put("msgTime", msgTime.get("lv"));
//                    groupMsg.put("msgGuid", msgGuid.get("lv"));
//                    groupMsg.put("userId", userId.get("lv"));
//
//
//                    map.put("user",map2.get("v"));//撤回人
//                    map.put("tag",map4.get("v"));//好友撤回 528  群 732
//                    map.put("type",type.get("lv"));
//                    map.put("groupId",groupId.get("lv"));//群号
//                    map.put("v",groupMsg);
                    map.put("type",type.get("lv"));
                    map.put("msg",s);
                    map.put("v",type.get("remain"));
                }


                if("12".equals(type.get("lv"))){//0C 01 2EB676B1执行账号 639DF987时间戳 0001 6AF88DAB被禁言账号 00000258禁言时间
                    map.put("type",type.get("lv"));
                    map.put("msg",s);
                    map.put("v",type.get("remain"));
                }
                if("20".equals(type.get("lv"))){
                    map.put("type",type.get("lv"));
                    map.put("msg",s);
                    map.put("v",type.get("remain"));
                }



                return map;
            }





        } else {
            System.out.println("type mismatch.");
        }

        return null;


    }






    /**
     * list类型 群成员信息
     * @param byteBuffer
     * @return
     */
    public Map readGroupUserList(ByteBuffer byteBuffer) {
        UnPack unPack = new UnPack();
        unPack.setData(byteBuffer.array());
        Map map = new HashMap();
        HeadData headData = new HeadData();
        int tagLen = readHead(headData, byteBuffer);
        byte b = headData.type;
        int tag = headData.tag;
        if (b == (byte) 9) {
            unPack.getBin(tagLen);//这是头(tag)的长0-255之间变动
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());
            Map map1=jceUnpack.readNumber(jceUnpack.bs);

            String listUser= (String) map1.get("remain");


            List list=new ArrayList<>();//list类型大小,内部的参数
            Object o=map1.get("v");


            for (int j = 0; j < Integer.parseInt(o.toString()); j++) {//41
                List list1=new ArrayList<>();//内部的参数,用户账号在线状态
                listUser=listUser.substring(2);//OA
                jceUnpack.wrap(AllToByte.hexToByte(listUser));
                Map map2=jceUnpack.readNumber(jceUnpack.bs);//0 账号
                list1.add(map2.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
//                Map map3=jceUnpack.readNumber(jceUnpack.bs);//1
//                jceUnpack.wrap(AllToByte.hexToByte((String) map3.get("remain")));
//                Map map4=jceUnpack.readNumber(jceUnpack.bs);//2 年龄
//                list1.add(map4.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map4.get("remain")));
//                Map map5=jceUnpack.readNumber(jceUnpack.bs);//3 性别
//                list1.add(map5.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map5.get("remain")));
//                Map map6=jceUnpack.readString(jceUnpack.bs);//4 光核粒子
//                list1.add(map6.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map6.get("remain")));
//                Map map7=jceUnpack.readNumber(jceUnpack.bs);//5
//                jceUnpack.wrap(AllToByte.hexToByte((String) map7.get("remain")));
//                Map map8=jceUnpack.readString(jceUnpack.bs);//6
//                jceUnpack.wrap(AllToByte.hexToByte((String) map8.get("remain")));
//                Map map9=jceUnpack.readString(jceUnpack.bs);//8
//                jceUnpack.wrap(AllToByte.hexToByte((String) map9.get("remain")));
//                Map map10=jceUnpack.readNumber(jceUnpack.bs);//9
//                jceUnpack.wrap(AllToByte.hexToByte((String) map10.get("remain")));
//                Map map11=jceUnpack.readString(jceUnpack.bs);//10 手机号
//                list1.add(map11.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map11.get("remain")));
//                Map map12=jceUnpack.readString(jceUnpack.bs);//11 邮箱
//                list1.add(map12.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map12.get("remain")));
//                Map map13=jceUnpack.readString(jceUnpack.bs);//12
//                jceUnpack.wrap(AllToByte.hexToByte((String) map13.get("remain")));
//                Map map14=jceUnpack.readString(jceUnpack.bs);//13 我去什么鬼！好友备注
//                list1.add(map14.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map14.get("remain")));
//                Map map15=jceUnpack.readNumber(jceUnpack.bs);//14
//                jceUnpack.wrap(AllToByte.hexToByte((String) map15.get("remain")));
//                Map map16=jceUnpack.readNumber(jceUnpack.bs);//15  加群时间
//                list1.add(map16.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map16.get("remain")));
//                Map map17=jceUnpack.readNumber(jceUnpack.bs);//16  发言时间
//                list1.add(map17.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map17.get("remain")));
//                Map map18=jceUnpack.readNumber(jceUnpack.bs);//17
//                jceUnpack.wrap(AllToByte.hexToByte((String) map18.get("remain")));
//                Map map19=jceUnpack.readNumber(jceUnpack.bs);//18
//                jceUnpack.wrap(AllToByte.hexToByte((String) map19.get("remain")));
//                Map map20=jceUnpack.readNumber(jceUnpack.bs);//19
//                jceUnpack.wrap(AllToByte.hexToByte((String) map20.get("remain")));
//                Map map21=jceUnpack.readNumber(jceUnpack.bs);//20
//                jceUnpack.wrap(AllToByte.hexToByte((String) map21.get("remain")));
//                Map map22=jceUnpack.readNumber(jceUnpack.bs);//21
//                jceUnpack.wrap(AllToByte.hexToByte((String) map22.get("remain")));
//                Map map23=jceUnpack.readNumber(jceUnpack.bs);//22
//                jceUnpack.wrap(AllToByte.hexToByte((String) map23.get("remain")));
//                Map map24=jceUnpack.readString(jceUnpack.bs);//23 头衔
//                list1.add(map24.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map24.get("remain")));
//                Map map25=jceUnpack.readNumber(jceUnpack.bs);//24 头衔到期时间
//                list1.add(map25.get("v"));
//                jceUnpack.wrap(AllToByte.hexToByte((String) map25.get("remain")));
//                Map map26=jceUnpack.readString(jceUnpack.bs);//25
//                jceUnpack.wrap(AllToByte.hexToByte((String) map26.get("remain")));
//                Map map27=jceUnpack.readNumber(jceUnpack.bs);//26
//                jceUnpack.wrap(AllToByte.hexToByte((String) map27.get("remain")));
//                Map map28=jceUnpack.readNumber(jceUnpack.bs);//27
//                jceUnpack.wrap(AllToByte.hexToByte((String) map28.get("remain")));
//                Map map29=jceUnpack.readNumber(jceUnpack.bs);//28
//                jceUnpack.wrap(AllToByte.hexToByte((String) map29.get("remain")));
//                Map map30=jceUnpack.readNumber(jceUnpack.bs);//29
//                jceUnpack.wrap(AllToByte.hexToByte((String) map30.get("remain")));
//                Map map31=jceUnpack.readNumber(jceUnpack.bs);//30
//                jceUnpack.wrap(AllToByte.hexToByte((String) map31.get("remain")));
//                Map map32=jceUnpack.readNumber(jceUnpack.bs);//31
//                jceUnpack.wrap(AllToByte.hexToByte(((String)map32.get("remain")).substring(2)));// FA
//                Map map33=jceUnpack.readNumber(jceUnpack.bs);//32
//                jceUnpack.wrap(AllToByte.hexToByte(((String)map33.get("remain")).substring(6)));// 180C 0B
//                Map map35=jceUnpack.readNumber(jceUnpack.bs);//33
//                jceUnpack.wrap(AllToByte.hexToByte((String) map35.get("remain")));
//                Map map36=jceUnpack.readNumber(jceUnpack.bs);//34
//                jceUnpack.wrap(AllToByte.hexToByte((String) map36.get("remain")));
//                Map map37=jceUnpack.readNumber(jceUnpack.bs);//35
//                jceUnpack.wrap(AllToByte.hexToByte((String) map37.get("remain")));
//                Map map38=jceUnpack.readNumber(jceUnpack.bs);//36
//                jceUnpack.wrap(AllToByte.hexToByte((String) map38.get("remain")));
//                Map map39=jceUnpack.readNumber(jceUnpack.bs);//37
//                jceUnpack.wrap(AllToByte.hexToByte((String) map39.get("remain")));
//                Map map40=jceUnpack.readNumber(jceUnpack.bs);//38
//                jceUnpack.wrap(AllToByte.hexToByte((String) map40.get("remain")));
//                Map map41=jceUnpack.readSimpleList(jceUnpack.bs);//39
//                jceUnpack.wrap(AllToByte.hexToByte((String) map41.get("remain")));
//                Map map42=jceUnpack.readSimpleList(jceUnpack.bs);//40
//                jceUnpack.wrap(AllToByte.hexToByte((String) map42.get("remain")));
//                Map map43=jceUnpack.readNumber(jceUnpack.bs);//41

                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                Map map3=jceUnpack.readNumber(jceUnpack.bs);//1
                jceUnpack.wrap(AllToByte.hexToByte((String) map3.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//2 年龄
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//3 性别
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readString(jceUnpack.bs);//4 光核粒子
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//5
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readString(jceUnpack.bs);//6
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readString(jceUnpack.bs);//8
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//9
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readString(jceUnpack.bs);//10 手机号
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readString(jceUnpack.bs);//11 邮箱
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readString(jceUnpack.bs);//12
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readString(jceUnpack.bs);//13 我去什么鬼！好友备注
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//14
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//15  加群时间
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//16  发言时间
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//17
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//18
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//19
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//20
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//21
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//22
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readString(jceUnpack.bs);//23 头衔
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//24 头衔到期时间
                list1.add(map2.get("v"));
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readString(jceUnpack.bs);//25
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//26
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//27
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//28
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//29
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//30
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//31

                jceUnpack.wrap(AllToByte.hexToByte(((String)map2.get("remain")).substring(4)));// FA 20
                map2=jceUnpack.readNumber(jceUnpack.bs);//32

                jceUnpack.wrap(AllToByte.hexToByte(((String)map2.get("remain")).substring(6)));// 180C 0B

                map2=jceUnpack.readNumber(jceUnpack.bs);//33
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//34
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//35
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//36
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//37
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//38
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readSimpleList(jceUnpack.bs);//39
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readSimpleList(jceUnpack.bs);//40
                jceUnpack.wrap(AllToByte.hexToByte((String) map2.get("remain")));
                map2=jceUnpack.readNumber(jceUnpack.bs);//41



                listUser=((String)map2.get("remain")).substring(2);//0B
                list.add(list1);
            }

            jceUnpack.wrap(AllToByte.hexToByte(listUser));
            Map map2=jceUnpack.readNumber(jceUnpack.bs);//4 userGo


            map.put("v",list);
            map.put("userGo",map2.get("v"));

            return map;


        } else {
            System.out.println("type mismatch.");
        }

        return null;


    }




    /**
     *  mpa类型 好友列表
     * @param byteBuffer
     * @return
     */
    public Map<String, String> readUserMap(ByteBuffer byteBuffer) {
        UnPack unPack=new UnPack();
        unPack.setData(byteBuffer.array());
        Map map=new HashMap();
        HeadData headData=new HeadData();
        int tagLen=readHead(headData,byteBuffer);
        if (headData.type == (byte) 8) {//如果是map类型则解析2次,具体的结构看返回而定这里是
            unPack.setData(byteBuffer.array());
            unPack.getBin(tagLen);
            unPack.getBin(2);
            String s="";
            JCEUnpack jceUnpack=new JCEUnpack(unPack.getAll());//map==4
            for (int i = 0; i < 4; i++) {
                Map m1=jceUnpack.readNumber(jceUnpack.bs);
                s= (String) m1.get("remain");
                s=s.substring(2);//1A
                jceUnpack.wrap(AllToByte.hexToByte(s));
                Map m2=jceUnpack.readNumber(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) m2.get("remain")));
                Map m3=jceUnpack.readNumber(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) m3.get("remain")));
                Map m4=jceUnpack.readNumber(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) m4.get("remain")));
                Map m5=jceUnpack.readNumber(jceUnpack.bs);
                jceUnpack.wrap(AllToByte.hexToByte((String) m5.get("remain")));
                Map m6=jceUnpack.readNumber(jceUnpack.bs);
                s= (String) m6.get("remain");
                s=s.substring(2);//0B
                jceUnpack.wrap(AllToByte.hexToByte(s));

                if(i==3){
                    Map m7=jceUnpack.readNumber(jceUnpack.bs);
                    jceUnpack.wrap(AllToByte.hexToByte((String) m7.get("remain")));
                    Map m8=jceUnpack.readNumber(jceUnpack.bs);
                    jceUnpack.wrap(AllToByte.hexToByte((String) m8.get("remain")));
                    Map m9=jceUnpack.readString(jceUnpack.bs);
                    s= (String) m9.get("remain");
                    s=s.substring(2);//0B
                }
            }


            map.put("tag",headData.tag);
            map.put("v","");
            map.put("remain", s);
            return map;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("size invalid: ");
//            stringBuilder.append(read);
            System.out.println(stringBuilder.toString());
            return null;

        }
//        System.out.println("type mismatch.");
//        return hashMap;
    }








}








