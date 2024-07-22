package com.ruoyi.im.utils;

import com.ruoyi.im.entity.Ecdh;
import com.ruoyi.im.service.TEAService;
import com.ruoyi.im.service.impl.TEAServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理qq登录
 */
public class LoginUtils {


    /**
     * 解析二维码图片
     *
     * @param data 未知的结构
     * @return int值
     */
    public static Map unPack(byte[] data, Map initUser) {
        Map map = new HashMap();
        Ecdh ecdh = (Ecdh) initUser.get("ecdh");
        TEAService teaService = new TEAServiceImpl();//tea加解密
        UnPack unPack = new UnPack();
        unPack.setData(data);
        unPack.getInt();//包长
        int type = unPack.getInt();//包类型
        byte key = unPack.getByte();//包加密方式
        unPack.getByte();//0
        unPack.getInt();//00 00 00 05
        unPack.getByte();//30


        try {
            unPack.setData(teaService.decrypt(unPack.getAll(), new byte[16]));//outer
            byte[] part1Outer = unPack.getBin(unPack.getInt() - 4);//Len part1Outer
            byte[] part2Outer = unPack.getBin(unPack.getInt() - 4);//Len part2Outer
            //part1Outer
            unPack.setData(part1Outer);
            unPack.getBin(4);//用来配备发送的数据,seq相等就是此数据的返回包
            unPack.getInt();//0
            unPack.getInt();//04
            byte[] cmd = unPack.getBin(unPack.getInt() - 4);//用来配备发送的数据,cmd相等就是此数据的返回包
            byte[] part1Token = unPack.getBin(unPack.getInt() - 4);//token 记录一下，接下来的发送改成这里的


            unPack.setData(part2Outer);//part2Outer

            unPack.getByte();//02
            unPack.getShort();//02 F1
            unPack.getShort();//1F 41
            unPack.getShort();//08 12
            unPack.getShort();//00 01
            unPack.getInt();//00 00 00 00
            unPack.getBin(3);//00 00 00 登录状态
            unPack.setData(unPack.getBin(unPack.getAll().length - 1));

            unPack.setData(teaService.decrypt(unPack.getAll(), AllToByte.hexToByte(ecdh.getShare_key())));

            unPack.getShort();//00 00
            unPack.getShort();//02 CD 递增
            unPack.getShort();//00 02
            unPack.getShort();//02 CD 递增
            unPack.getBin(49);//
            byte[] t0018 = unPack.getBin(24);//获取扫码状态需要qq

            map.put("t0018", t0018);

            String qr = ByteToAll.byteToText(unPack.getAll());//
            int tag = qr.indexOf("https");
            String qrHex = ByteToAll.byteToHxe(AllToByte.textToByte(qr.substring(tag)));//
            int end = qrHex.indexOf("1A");

            qrHex = qrHex.substring(0, end);

            qr = ByteToAll.byteToText(AllToByte.hexToByte(qrHex));//
            map.put("qr", qr);
            return map;
        } catch (Exception e) {
            return map;
        }


    }


}
