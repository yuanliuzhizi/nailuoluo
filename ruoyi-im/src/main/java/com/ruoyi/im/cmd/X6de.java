package com.ruoyi.im.cmd;


import com.ruoyi.im.entity.Android;
import com.ruoyi.im.entity.App;
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
 * 上报系统和应用版本android 8.8.38
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class X6de {

    private Pack pack = new Pack();
    private TEAService teaService = new TEAServiceImpl();

    public byte[] part2Outer(Android android, App app) {
        pack.empty();
        pack.setHex(Protobuf.int64(Long.valueOf(app.getAppid1()), 1));
        pack.setHex(Protobuf.bit64(AllToByte.textToByte(android.getImei()), 2));
        pack.setHex(Protobuf.bit64(AllToByte.hexToByte(android.getGuid()), 3));
        pack.setHex(Protobuf.bit64(AllToByte.textToByte(android.getAndroidId()), 5));

        byte[] bin = pack.getAll();
        pack.empty();
        pack.setBin(new byte[]{8, 0, 16, 31});
        pack.setBin(bin);
        pack.setBin(new byte[]{48, 1});
        bin = pack.getAll();
        pack.empty();
        pack.setHex(Protobuf.int64(1758l, 1));
        pack.setBin(new byte[]{16, 0});
        pack.setHex(Protobuf.bit64(bin, 5));
        bin = pack.getAll();
        pack.empty();
        pack.setInt(bin.length + 4);
        pack.setBin(bin);
        return pack.getAll();

    }

    public byte[] part1Outer(byte[] cmd, byte[] part1Token) {
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


    public byte[] x6de(Map initUser) {
        User u = (User) initUser.get("user");
        App app = (App) initUser.get("app");
        Android android = (Android) initUser.get("android");
        UnPackLogin unPackLogin = (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增
        X6de x6de = new X6de();
        try {


            byte[] part2Outer = x6de.part2Outer(android, app);

            byte[] cmd_0 = AllToByte.textToByte(Cmd.CMD_12);
            byte[] imsi = AllToByte.textToByte(android.getImsi());
            byte[] appVersion = AllToByte.textToByte(app.getAppVersion());
            byte[] hexTextUser = AllToByte.hexToByte(u.toUserText());
            byte[] tlv_t010A;
            if (unPackLogin.getToken() == null) {
                tlv_t010A = null;
            } else {
                tlv_t010A = (byte[]) unPackLogin.getToken().get("tlv_t010A");
            }
            byte[] part1Outer = x6de.part1Outer(cmd_0, unPackLogin.getPart1Token());

            byte[] outer = x6de.outer(unPackLogin, part1Outer, part2Outer, hexTextUser, u);

            return outer;

        } catch (Exception e) {
            return null;
        }

    }


}
