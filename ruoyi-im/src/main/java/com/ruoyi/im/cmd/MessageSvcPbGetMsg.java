package com.ruoyi.im.cmd;

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
 * 接收好友消息
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class MessageSvcPbGetMsg {

    private Pack pack=new Pack();
    private TEAService teaService = new TEAServiceImpl();

    public byte[] part2Outer(User user){
        pack.empty();
        pack.setHex(Protobuf.int64(0l, 1));
        pack.setHex(Protobuf.bit64(user.updateSyncCookies(), 2));
        pack.setHex(Protobuf.int64(0l, 3));
        pack.setHex(Protobuf.int64(0l, 4));
        pack.setHex(Protobuf.int64(0l, 5));
        pack.setHex(Protobuf.int64(0l, 6));
        pack.setHex(Protobuf.int64(0l, 7));
        pack.setHex(Protobuf.int64(0l, 9));
        pack.setHex(Protobuf.bit64(new byte[0], 12));


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


    public byte[] messageSvcPbGetMsg(Map initUser){
        User u= (User) initUser.get("user");
        UnPackLogin unPackLogin= (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增
        MessageSvcPbGetMsg messageSvcPbGetMsg=new MessageSvcPbGetMsg();
        try {
            byte[] part2Outer = messageSvcPbGetMsg.part2Outer(u);
            byte[] cmd_0 = AllToByte.textToByte(Cmd.CMD_30);
            byte[] part1Outer = messageSvcPbGetMsg.part1Outer(cmd_0, unPackLogin.getPart1Token());
            byte[] hexTextUser = AllToByte.hexToByte(u.toUserText());
            byte[] outer = messageSvcPbGetMsg.outer(unPackLogin, part1Outer, part2Outer, hexTextUser, u);
            return outer;
        }catch (Exception e){
            return null;
        }


    }



}
