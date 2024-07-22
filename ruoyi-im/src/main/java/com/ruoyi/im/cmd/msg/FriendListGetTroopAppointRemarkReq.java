package com.ruoyi.im.cmd.msg;

import com.ruoyi.im.entity.UnPackLogin;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.enums.Cmd;
import com.ruoyi.im.service.TEAService;
import com.ruoyi.im.service.impl.TEAServiceImpl;
import com.ruoyi.im.tool.Jce;
import com.ruoyi.im.tool.Pack;
import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.NumericConvertUtil;

import java.util.Map;

/**
 * 目前用来获取群临时聊天
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class FriendListGetTroopAppointRemarkReq {

    private Pack pack=new Pack();
    private TEAService teaService = new TEAServiceImpl();

    public byte[] part2Outer(byte[] part2Core){
        Jce jce=new Jce();
        jce.clear();
        jce.writeByte((byte)3,1);
        jce.writeByte((byte)0,2);
        jce.writeByte((byte)0,3);
        jce.writeInt(243134112, 4);
        jce.writeStringByte(AllToByte.textToByte("mqq.IMService.FriendListServiceServantObj"),5);
        jce.writeStringByte(AllToByte.textToByte("GetTroopAppointRemarkReq"),6);
        jce.writeSimpleList(part2Core,7);
        jce.writeByte((byte)0,8);
        jce.writeByte((byte)0,8);
        jce.writeMap(new byte[0],9);
        jce.writeMap(new byte[0],10);
        byte[] bin=jce.allToByte();
        pack.empty();
        pack.setInt(bin.length+4);
        pack.setBin(bin);
        return pack.getAll();

    }


    public byte[] part2Core(byte[] packPart2Core) {
        Jce jce=new Jce();
        jce.clear();
        jce.wrap(AllToByte.hexToByte("08 00 01"));//map 2
        jce.writeStringByte(AllToByte.textToByte("GTA"),0);
        jce.writeSimpleList(packPart2Core,1);
        return jce.allToByte();
    }


    public byte[] packPart2Core(User u, Long userGo, Long group,Long qunLink){
        Jce jce=new Jce();
        jce.clear();
        jce.wrap(AllToByte.hexToByte("0A"));
        jce.writeLong(Long.parseLong(u.getUser()),0);
        jce.writeLong(group, 1);
        jce.writeLong(qunLink, 3);//随机 4153760627
        jce.wrap(AllToByte.hexToByte("4900010300000000" + NumericConvertUtil.toOtherBaseString(userGo.longValue(), 16)));
        jce.writeByte((byte)2,5);
        jce.writeByte((byte)1,6);
        jce.wrap(AllToByte.hexToByte("0B"));
        return jce.allToByte();
    }



    public byte[] part1Outer(byte[] cmd,byte[] part1Token){
        pack.empty();
        pack.setInt((cmd.length + 4));
        pack.setBin(cmd);
        if(part1Token==null){//记录返回的getPart1Token
            pack.setHex("00 00 00 04");
        }else {
            pack.setInt(part1Token.length+4);
            pack.setBin(part1Token);
        }
        pack.setInt(AllToByte.hexToByte("70 00").length+4);
        pack.setBin(AllToByte.hexToByte("70 00"));
        byte[] bin=pack.getAll();
        pack.empty();
        pack.setInt(bin.length+4);
        pack.setBin(bin);
        return pack.getAll();
    }



    public byte[] outer(UnPackLogin unPackLogin, byte[] part1Outer, byte[] part2Outer, byte[] hexTextUser, User u){
        pack.empty();
        pack.setBin(part1Outer);
        pack.setBin(part2Outer);
        byte[] bin=teaService.encrypt(pack.getAll(),(byte[]) unPackLogin.getToken().get("tlv_t0305"));
        pack.empty();
        pack.setHex("00 00 00 0B");
        pack.setHex("01");
        pack.setInt(u.getSeq());
        pack.setHex("00");
        pack.setInt( hexTextUser.length + 4);
        pack.setBin(hexTextUser);
        pack.setBin(bin);
        bin=pack.getAll();
        pack.empty();
        pack.setInt(bin.length+4);
        pack.setBin(bin);
        return pack.getAll();
    }




    public byte[] friendListGetTroopAppointRemarkReq(Map initUser,String userGo,String group,String qunLink){
        User u= (User) initUser.get("user");
        UnPackLogin unPackLogin= (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增
        FriendListGetTroopAppointRemarkReq friendListGetTroopAppointRemarkReq=new FriendListGetTroopAppointRemarkReq();
        try {
            byte[] packPart2Core = friendListGetTroopAppointRemarkReq.packPart2Core(u, Long.parseLong(userGo), Long.parseLong(group), Long.parseLong(qunLink));
            byte[] part2Core=friendListGetTroopAppointRemarkReq.part2Core(packPart2Core);
            byte[] part2Outer=friendListGetTroopAppointRemarkReq.part2Outer(part2Core);

            byte[] cmd_0= AllToByte.textToByte(Cmd.CMD_21);
            byte[] part1Outer = friendListGetTroopAppointRemarkReq.part1Outer(cmd_0, unPackLogin.getPart1Token());

            byte[] hexTextUser=AllToByte.hexToByte(u.toUserText());
            byte[] outer=friendListGetTroopAppointRemarkReq.outer(unPackLogin,part1Outer,part2Outer,hexTextUser,u);

            return outer;

        }catch (Exception e){
            return null;
        }

    }






}
