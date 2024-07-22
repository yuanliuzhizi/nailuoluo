package com.ruoyi.im.cmd.msg.group;

import com.ruoyi.im.entity.UnPackLogin;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.enums.Cmd;
import com.ruoyi.im.service.TEAService;
import com.ruoyi.im.service.impl.TEAServiceImpl;
import com.ruoyi.im.tool.Pack;
import com.ruoyi.im.tool.Protobuf;
import com.ruoyi.im.utils.AllToByte;

import java.util.Map;

/**
 * 发送群文本消息
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class MessageSvcPbSendMsg {
    private Pack pack=new Pack();
    private TEAService teaService = new TEAServiceImpl();
    public byte[] part2Outer(User u, long group, String msg){

        pack.empty();

        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(AllToByte.hexToByte(Protobuf.int64(group, 1)), 2)),1));//群号
        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.int64(1l,1)+ Protobuf.int64(0l,2)+ Protobuf.int64(0l,3)),2));

        byte[] part1=pack.getAll();
        pack.empty();

        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(AllToByte.textToByte(msg),1)),1)),2));//消息


        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(AllToByte.hexToByte(Protobuf.int64(0l,17)+Protobuf.bit64(AllToByte.hexToByte(Protobuf.int64(0l,15)+Protobuf.int64(0l,31)+Protobuf.int64(0l,41)),19)),37)),2));

        byte[] part2=pack.getAll();
        pack.empty();



        pack.setBin(part1);
        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(part2,1)),3));

        pack.setHex(Protobuf.int64(Long.valueOf(u.getSeq())-6000, 4));//随机50000
        pack.setHex(Protobuf.int64(Long.valueOf(u.getSeq())*10000, 5));//随机499421387
        pack.setHex(Protobuf.int64(1l,8));

        byte[] bin=pack.getAll();
        pack.empty();
        pack.setInt(bin.length+4);
        pack.setBin(bin);
        return pack.getAll();

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



    public byte[] messageSvcPbSendMsg(Map initUser,String group,String msg){

        User u= (User) initUser.get("user");
        UnPackLogin unPackLogin= (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增

        MessageSvcPbSendMsg messageSvcPbSendMsg=new MessageSvcPbSendMsg();
        try {

            byte[] cmd_0 = AllToByte.textToByte(Cmd.CMD_22);
            byte[] part2Outer=messageSvcPbSendMsg.part2Outer(u,Long.parseLong(group),msg);
            byte[] part1Outer = messageSvcPbSendMsg.part1Outer(cmd_0, unPackLogin.getPart1Token());

            byte[] hexTextUser=AllToByte.hexToByte(u.toUserText());
            byte[] outer=messageSvcPbSendMsg.outer(unPackLogin,part1Outer,part2Outer,hexTextUser,u);

            return outer;
        }catch (Exception e){
            return null;
        }
    }
}
