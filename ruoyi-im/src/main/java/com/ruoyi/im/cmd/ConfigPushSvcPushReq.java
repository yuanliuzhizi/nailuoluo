package com.ruoyi.im.cmd;

import com.ruoyi.im.entity.UnPackLogin;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.enums.Cmd;
import com.ruoyi.im.service.TEAService;
import com.ruoyi.im.service.impl.TEAServiceImpl;
import com.ruoyi.im.tool.Pack;
import com.ruoyi.im.utils.AllToByte;
import java.util.Map;

/**
 * 两种上传方式,tcp或者http这个是获取tcp返回的上传服务器参数
 */
public class ConfigPushSvcPushReq {

    private Pack pack=new Pack();
    private TEAService teaService = new TEAServiceImpl();
    public byte[] part2Outer(){
        pack.empty();
        pack.setHex("00 00 00 00");
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


    public byte[] configPushSvcPushReq(Map initUser){
        User u= (User) initUser.get("user");
        UnPackLogin unPackLogin= (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增
        ConfigPushSvcPushReq configPushSvcPushReq=new ConfigPushSvcPushReq();
        try {
            byte[] part2Outer = configPushSvcPushReq.part2Outer();
            byte[] cmd_0 = AllToByte.textToByte(Cmd.CMD_28);
            byte[] part1Outer = configPushSvcPushReq.part1Outer(cmd_0, unPackLogin.getPart1Token());
            byte[] hexTextUser = AllToByte.hexToByte(u.toUserText());
            byte[] outer = configPushSvcPushReq.outer(unPackLogin, part1Outer, part2Outer, hexTextUser, u);
            return outer;
        }catch (Exception e){
            return null;
        }


    }



}
