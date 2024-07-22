package com.ruoyi.im.cmd;

import com.ruoyi.im.entity.Android;
import com.ruoyi.im.entity.App;
import com.ruoyi.im.entity.UnPackLogin;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.enums.Cmd;
import com.ruoyi.im.service.TEAService;
import com.ruoyi.im.service.impl.TEAServiceImpl;
import com.ruoyi.im.tool.Jce;
import com.ruoyi.im.tool.Pack;
import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;
import com.ruoyi.im.utils.RandomUtils;

import java.util.Map;

/**
 * 上线
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class Register {

    private Pack pack = new Pack();
    private TEAService teaService = new TEAServiceImpl();

    public byte[] part2Outer(byte[] part2Core) {
        Jce jce = new Jce();
        jce.clear();

        jce.writeByte((byte) 3, 1);
        jce.writeByte((byte) 0, 2);
        jce.writeByte((byte) 0, 3);
        jce.writeByte((byte) 0, 4);
        jce.writeStringByte(AllToByte.textToByte("PushService"), 5);
        jce.writeStringByte(AllToByte.textToByte("SvcReqRegister"), 6);

        jce.writeSimpleList(part2Core, 7);
        jce.writeByte((byte) 0, 8);
        jce.writeMap(new byte[0], 9);
        jce.writeMap(new byte[0], 10);
        byte[] bin = jce.allToByte();
        pack.empty();
        pack.setInt(bin.length + 4);
        pack.setBin(bin);
        return pack.getAll();

    }


    public byte[] part2Core(byte[] packPart2Core) {
        Jce jce = new Jce();
        jce.clear();
        jce.wrap(AllToByte.hexToByte("08 00 01"));//map 2
        jce.writeStringByte(AllToByte.textToByte("SvcReqRegister"), 0);
        jce.writeSimpleList(packPart2Core, 1);
        return jce.allToByte();
    }


    public byte[] packPart2Core(User u, Android android) {
        Jce jce = new Jce();
        jce.clear();
        jce.wrap(AllToByte.hexToByte("0A"));
        jce.writeLong(Long.valueOf(u.getUser()), 0);
        jce.writeByte((byte) 7, 1);
        jce.writeByte((byte) 0, 2);
        jce.writeByteString("", 3);
        jce.writeByte((byte) 11, 4);//登录后在线状态 41 隐身 11 在线   21 离线
        jce.writeByte((byte) 0, 5);
        jce.writeByte((byte) 0, 6);
        jce.writeByte((byte) 0, 7);
        jce.writeByte((byte) 0, 8);
        jce.writeByte((byte) 0, 9);
        jce.writeByte((byte) 0, 10); //离线78
        jce.writeByte((byte) 22, 11);
        jce.writeByte((byte) 1, 12);
        jce.writeByteString("", 13);
        jce.writeByte((byte) 0, 14);
        jce.writeSimpleList(AllToByte.hexToByte(android.getGuid()), 16);//key allToByte.hexToByte(UserConfHD.GUID) UserConfHD.toHexText(UserConfHD.IMEI)
        jce.writeShort((short) 2052, 17);
        jce.writeByte((byte) 0, 18);
        jce.writeStringByte(AllToByte.textToByte(android.getPhoneModel()), 19);
        jce.writeStringByte(AllToByte.textToByte(android.getPhoneModel()), 20);
        jce.writeStringByte(AllToByte.textToByte(android.getSystemVer()), 21);
        jce.writeByte((byte) 1, 22);
        jce.writeShort((short) 649, 23);
        jce.writeByte((byte) 0, 24);
        jce.writeByte((byte) 0, 26);
        jce.writeLong(Long.valueOf(RandomUtils.randomToNumber(486713, 3658196)) * 1237, 27);//离线0
        jce.writeStringByte(AllToByte.textToByte(""), 28);
        jce.writeByte((byte) 0, 29);
        jce.writeStringByte(AllToByte.textToByte(android.getPhoneBrand()), 30);
        jce.writeStringByte(AllToByte.textToByte("?LMY48Z;android_x86-"), 31);
        jce.writeStringByte(AllToByte.textToByte(""), 32);
        jce.writeSimpleList(AllToByte.hexToByte("0A04082E10000A05089B021000"), 33);//某些token
        jce.writeByte((byte) 0, 34);
        jce.writeByte((byte) 0, 36);
        jce.writeByte((byte) 1, 37);
        jce.writeByte((byte) 255, 38);
        jce.writeByte((byte) 0, 39);
        jce.writeByte((byte) 1, 40);
        jce.writeByte((byte) 1, 41);
        jce.wrap(AllToByte.hexToByte("FA2A"));
        jce.writeByte((byte) 3, 0);
        jce.wrap(AllToByte.hexToByte("0B"));
        jce.writeByte((byte) 0, 43);
        jce.wrap(AllToByte.hexToByte("0B"));
        return jce.allToByte();
    }


    public byte[] part1Outer(int seq, int appid1, int appid2, byte[] tlv010A, byte[] part1Token, byte[] cmd, byte[] imei, byte[] imsi, byte[] appVersion) {
        pack.empty();
        pack.setInt(seq);
        pack.setInt(appid1);
        pack.setInt(appid2);
        pack.setHex("01 00 00 00");
        pack.setHex("00 00 00 00");
        pack.setHex("00 00 01 00");
        if (tlv010A == null) {//登录token tlv010A
            pack.setHex("00 00 00 04");
        } else {
            pack.setInt(tlv010A.length + 4);
            pack.setBin(tlv010A);
        }
        pack.setInt((cmd.length + 4));
        pack.setBin(cmd);
        if (part1Token == null) {//记录返回的getPart1Token
            pack.setHex("00 00 00 04");
        } else {
            pack.setInt(part1Token.length + 4);
            pack.setBin(part1Token);
        }
        pack.setInt((imei.length + 4));
        pack.setBin(imei);
        pack.setHex("00 00 00 04");
        pack.setShort((short) (imsi.length + appVersion.length + 2));//中国移动用户标识码 +apk版本带A
        String su = "7C" + ByteToAll.byteToHxe(imsi) + "7C" + ByteToAll.byteToHxe(appVersion) + "00 00";
        pack.setBin(AllToByte.hexToByte(su));
        byte[] bin = pack.getAll();
        pack.empty();
        pack.setInt(bin.length + 4);
        pack.setBin(bin);
        return pack.getAll();
    }


    public byte[] outer(Android android, UnPackLogin unPackLogin, byte[] part1Outer, byte[] part2Outer, byte[] hexTextUser) {
        pack.empty();
        pack.setBin(part1Outer);
        pack.setBin(part2Outer);
        byte[] bin = teaService.encrypt(pack.getAll(), (byte[]) unPackLogin.getToken().get("tlv_t0305"));
        pack.empty();
        pack.setHex("00 00 00 0A");
        pack.setHex("01");
        pack.setInt(((byte[]) unPackLogin.getToken().get("tlv_t0143")).length + 4);
        pack.setBin((byte[]) unPackLogin.getToken().get("tlv_t0143"));
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


    public byte[] register(Map initUser) {
        User u = (User) initUser.get("user");
        App app = (App) initUser.get("app");
        Android android = (Android) initUser.get("android");
        UnPackLogin unPackLogin = (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增
        Register register = new Register();
        try {
            byte[] packPart2Core = register.packPart2Core(u, android);
            byte[] part2Core = register.part2Core(packPart2Core);
            byte[] part2Outer = register.part2Outer(part2Core);

            byte[] cmd_0 = AllToByte.textToByte(Cmd.CMD_2);
            byte[] imsi = AllToByte.textToByte(android.getImsi());
            byte[] appVersion = AllToByte.textToByte(app.getAppVersion());
            byte[] tlv_t010A;
            if (unPackLogin.getToken() == null) {
                tlv_t010A = null;
            } else {
                tlv_t010A = (byte[]) unPackLogin.getToken().get("tlv_t010A");
            }
            byte[] part1Outer = register.part1Outer(u.getSeq(), app.getAppid1(), app.getAppid2(), tlv_t010A, unPackLogin.getPart1Token(), cmd_0, AllToByte.textToByte(android.getImei()), imsi, appVersion);

            byte[] outer = register.outer(android, unPackLogin, part1Outer, part2Outer, AllToByte.textToByte(u.getUser()));

            return outer;

        } catch (Exception e) {
            return null;
        }

    }


}
