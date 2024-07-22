package com.ruoyi.im.cmd.msg.group;

import com.ruoyi.im.entity.UnPackLogin;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.enums.Cmd;
import com.ruoyi.im.service.TEAService;
import com.ruoyi.im.service.impl.TEAServiceImpl;
import com.ruoyi.im.tool.Pack;
import com.ruoyi.im.tool.Protobuf;
import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.HexUtils;
import com.ruoyi.im.utils.NumericConvertUtil;
import java.util.Map;

/**
 * 禁言某人
 */
public class OidbSvc0x570_8 {

    private Pack pack=new Pack();
    private TEAService teaService = new TEAServiceImpl();
    public byte[] part2Outer(String groupId,String userGo,Long time){
        pack.empty();
        pack.setHex(Protobuf.int64(1392l, 1));
        pack.setHex(Protobuf.int64(8l, 2));
        pack.setHex(Protobuf.int64(0l, 3));
        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(NumericConvertUtil.toOtherBaseString(Long.parseLong(groupId),16)+"20 00 01"+NumericConvertUtil.toOtherBaseString(Long.parseLong(userGo),16)+ HexUtils.ten2Hex2(time*600)), 4));


        byte[] bin=pack.getAll();pack.empty();
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


    public byte[] oidbSvc0x570_8(Map initUser,String groupId,String userGo,Long time){
        User u= (User) initUser.get("user");
        UnPackLogin unPackLogin= (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增
        OidbSvc0x570_8 oidbSvc0x570_8=new OidbSvc0x570_8();
        try {
            byte[] part2Outer = oidbSvc0x570_8.part2Outer( groupId, userGo, time);
            byte[] cmd_0 = AllToByte.textToByte(Cmd.CMD_44);
            byte[] part1Outer = oidbSvc0x570_8.part1Outer(cmd_0, unPackLogin.getPart1Token());
            byte[] hexTextUser = AllToByte.hexToByte(u.toUserText());
            byte[] outer = oidbSvc0x570_8.outer(unPackLogin, part1Outer, part2Outer, hexTextUser, u);
            return outer;
        }catch (Exception e){
            return null;
        }


    }



}
