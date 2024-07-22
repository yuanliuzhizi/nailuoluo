package com.ruoyi.im.cmd.msg.group;

import com.ruoyi.im.entity.UnPackLogin;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.enums.Cmd;
import com.ruoyi.im.service.TEAService;
import com.ruoyi.im.service.impl.TEAServiceImpl;
import com.ruoyi.im.tool.Pack;
import com.ruoyi.im.tool.Protobuf;
import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.NumericConvertUtil;
import java.util.Map;

/**
 * 发送群文本消息 加艾特
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class MessageSvcPbSendMsgAt {
    private Pack pack=new Pack();
    private TEAService teaService = new TEAServiceImpl();
    public byte[] part2Outer(Map map,String groupId,String nameAt,String userGo,String msg){
        User u= (User) map.get("user");

        pack.empty();
        Protobuf protobuf = new Protobuf();
        protobuf.pbPro(2, "1", null);
        protobuf.pbPro(2, "1.2", null);
        protobuf.pbPro(1, "1.2.1", groupId);
        pack.setHex(protobuf.recursion());
        protobuf.pbPro(2, "2", null);
        protobuf.pbPro(1, "2.1", 1);
        protobuf.pbPro(1, "2.2", 0);
        protobuf.pbPro(1, "2.3", 0);
        pack.setHex(protobuf.recursion());
        protobuf.pbPro(2, "3", null);
        protobuf.pbPro(2, "3.1", null);
        protobuf.pbPro(2, "3.1.2", null);
        protobuf.pbPro(2, "3.1.2.1", null);
        protobuf.pbPro(0, "3.1.2.1.1", AllToByte.textToByte("@"+nameAt));//    [3.1.2(3).1.1](0A 0D=13):40E58589E6A0B8E7B292E5AD90(b):@光核粒子
        protobuf.pbPro(0, "3.1.2.1.3", AllToByte.hexToByte("00010000000500"+ NumericConvertUtil.toOtherBaseString(Long.parseLong(userGo),16) +"0000"));//[3.1.2(3).1.3](1A 0D=13):00010000000500 2EB676B1 0000(b):
        protobuf.pbPro(2, "3.1.2", null);
        protobuf.pbPro(2, "3.1.2.1", null);
        protobuf.pbPro(0, "3.1.2.1.1", AllToByte.textToByte(msg));//发送内容
        pack.setHex(protobuf.recursion());


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



    public byte[] messageSvcPbSendMsgAt(Map initUser,String groupId,String nameAt,String userGo,String msg){
        User u= (User) initUser.get("user");
        UnPackLogin unPackLogin= (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增

        MessageSvcPbSendMsgAt messageSvcPbSendMsgAt=new MessageSvcPbSendMsgAt();
        try {

            byte[] cmd_0 = AllToByte.textToByte(Cmd.CMD_22);


            byte[] part2Outer = messageSvcPbSendMsgAt.part2Outer(initUser, groupId, nameAt, userGo, msg);
            byte[] part1Outer = messageSvcPbSendMsgAt.part1Outer(cmd_0, unPackLogin.getPart1Token());


            byte[] hexTextUser=AllToByte.hexToByte(u.toUserText());
            byte[] outer=messageSvcPbSendMsgAt.outer(unPackLogin,part1Outer,part2Outer,hexTextUser,u);

            return outer;
        }catch (Exception e){
            return null;
        }
    }
}
