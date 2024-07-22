package com.ruoyi.im.cmd.msg.siliao;

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
 * 发送群私聊消息
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class MessageSvcPbSendMsg {

    private TEAService teaService = new TEAServiceImpl();
    private Pack pack = new Pack();

    public byte[] part2Outer(User u, String msg, Long qunLink, Long userGo) {
        pack.empty();
        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(AllToByte.hexToByte(Protobuf.int64(qunLink, 1) + Protobuf.int64(userGo, 2)), 3)), 1));//userGo
        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.int64(1l, 1) + Protobuf.int64(0l, 2) + Protobuf.int64(0l, 3)), 2));
        byte[] part1 = pack.getAll();
        pack.empty();

        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(AllToByte.textToByte(msg), 1)), 1)), 2));
        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(AllToByte.hexToByte(Protobuf.int64(0l, 17) + Protobuf.bit64(AllToByte.hexToByte(Protobuf.int64(0l, 15) + Protobuf.int64(0l, 31) + Protobuf.int64(0l, 41)), 19)), 37)), 2));

        byte[] part2 = pack.getAll();
        pack.empty();


        pack.setBin(part1);
        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(Protobuf.bit64(part2, 1)), 3));

        pack.setHex(Protobuf.int64(Long.valueOf(u.getSeq()) - 6000, 4));//随机50000
        pack.setHex(Protobuf.int64(Long.valueOf(u.getSeq()) * 100000, 5));//随机50000000
        pack.setHex(Protobuf.bit64(u.updateSyncCookies(), 6));
        pack.setHex(Protobuf.int64(1l, 8));


        byte[] bin = pack.getAll();
        pack.empty();
        pack.setInt(bin.length + 4);
        pack.setBin(bin);
        return pack.getAll();

    }


    public byte[] part1Outer(UnPackLogin unPackLogin) {
        byte[] cmd = AllToByte.textToByte(Cmd.CMD_22);
        byte[] part1Token = unPackLogin.getPart1Token();
        pack.empty();
        pack.setInt((cmd.length + 4));
        pack.setBin(cmd);
        if (part1Token == null) {//记录返回的getPart1Token
            pack.setHex("00 00 00 04");
        } else {
            pack.setInt(part1Token.length + 4);
            pack.setBin(part1Token);
        }
        pack.setInt(AllToByte.hexToByte("70 00").length + 4);
        pack.setBin(AllToByte.hexToByte("70 00"));
        byte[] bin = pack.getAll();
        pack.empty();
        pack.setInt(bin.length + 4);
        pack.setBin(bin);
        return pack.getAll();
    }


    public byte[] outer(UnPackLogin unPackLogin, byte[] part1Outer, byte[] part2Outer, byte[] hexTextUser, User u) {
        pack.empty();
        pack.setBin(part1Outer);
        pack.setBin(part2Outer);
        byte[] bin = teaService.encrypt(pack.getAll(), (byte[]) unPackLogin.getToken().get("tlv_t0305"));
        pack.empty();
        pack.setHex("00 00 00 0B");
        pack.setHex("01");
        pack.setInt(u.getSeq());
        pack.setHex("00");
        pack.setInt(hexTextUser.length + 4);
        pack.setBin(hexTextUser);
        pack.setBin(bin);
        bin = pack.getAll();
        pack.empty();
        pack.setInt(bin.length + 4);
        pack.setBin(bin);
        return pack.getAll();
    }


    public byte[] messageSvcPbSendMsg(Map initUser, String qunLink, String userGo, String msg) {
        User u = (User) initUser.get("user");
        UnPackLogin unPackLogin = (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增
        MessageSvcPbSendMsg messageSvcPbSendMsg = new MessageSvcPbSendMsg();
        try {

            byte[] part2Outer = messageSvcPbSendMsg.part2Outer(u, msg, Long.parseLong(qunLink), Long.parseLong(userGo));
            byte[] part1Outer = messageSvcPbSendMsg.part1Outer(unPackLogin);

            byte[] outer = messageSvcPbSendMsg.outer(unPackLogin, part1Outer, part2Outer, AllToByte.textToByte(u.getUser()), u);

            return outer;

        } catch (Exception e) {
            return null;
        }

    }


}
