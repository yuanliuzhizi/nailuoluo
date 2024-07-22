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

import java.util.Map;

/**
 * wifi参数
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class WifiCloudCheckSvc {

    private Pack pack = new Pack();
    private TEAService teaService = new TEAServiceImpl();

    public byte[] part2Outer(byte[] part2Core) {
        Jce jce = new Jce();
        jce.clear();

        jce.writeByte((byte) 3, 1);
        jce.writeByte((byte) 0, 2);
        jce.writeByte((byte) 0, 3);
        jce.writeLong(1844692143, 4);
        jce.writeStringByte(AllToByte.textToByte("WifiSdkObj"), 5);
        jce.writeStringByte(AllToByte.textToByte("req"), 6);
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
        jce.wrap(AllToByte.hexToByte("08 00 02"));//map 2
        jce.writeStringByte(AllToByte.textToByte("CSGUIDRegist"), 0);
        jce.writeSimpleList(packPart2Core, 1);
        jce.writeStringByte(AllToByte.textToByte("Sharkfin"), 0);
        jce.writeSimpleList(AllToByte.hexToByte("0A0C1C2600360046005119C996000B"), 1);
        return jce.allToByte();
    }


    public byte[] packPart2Core(Android android, App app) {
        Jce jce = new Jce();
        jce.clear();
        jce.wrap(AllToByte.hexToByte("0A"));
        jce.writeStringByte(AllToByte.textToByte(android.getImei()), 0);
        jce.writeStringByte(AllToByte.textToByte(android.getImsi()), 1);
        jce.writeStringByte(AllToByte.textToByte(android.getMac()), 2);
        jce.writeStringByte(AllToByte.textToByte("FF4A5386F7B20DCB"), 3);
        jce.writeShort((short) 6601, 4);
        jce.writeStringByte(AllToByte.textToByte("102769"), 5);
        jce.writeStringByte(AllToByte.hexToByte(app.getPackageFullName()), 6);
        jce.writeStringByte(AllToByte.textToByte(android.getPhoneBrand()), 7);
        jce.writeStringByte(AllToByte.textToByte(android.getCompile()), 8);
        jce.writeStringByte(AllToByte.textToByte(android.getSystemVer()), 9);
        jce.writeByte((byte) 106, 10);
        jce.writeByte((byte) 2, 11);
        jce.writeShort((short) 201, 12);
        jce.writeByte((byte) 0, 13);
        jce.writeStringByte(AllToByte.textToByte(android.getPhoneModel()), 14);
        jce.wrap(AllToByte.hexToByte("0B"));
        return jce.allToByte();
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

    public byte[] wifiCloudCheckSvc(Map initUser) {
        User u = (User) initUser.get("user");
        App app = (App) initUser.get("app");
        Android android = (Android) initUser.get("android");
        UnPackLogin unPackLogin = (UnPackLogin) initUser.get("unPackLogin");
        u.addSeq();//自增
        WifiCloudCheckSvc wifiCloudCheckSvc = new WifiCloudCheckSvc();
        try {


            byte[] packPart2Core = wifiCloudCheckSvc.packPart2Core(android, app);
            byte[] part2Core = wifiCloudCheckSvc.part2Core(packPart2Core);
            byte[] part2Outer = wifiCloudCheckSvc.part2Outer(part2Core);

            byte[] cmd_0 = AllToByte.textToByte(Cmd.CMD_9);
            byte[] imsi = AllToByte.textToByte(android.getImsi());
            byte[] appVersion = AllToByte.textToByte(app.getAppVersion());
            byte[] hexTextUser = AllToByte.hexToByte(u.toUserText());
            byte[] tlv_t010A;
            if (unPackLogin.getToken() == null) {
                tlv_t010A = null;
            } else {
                tlv_t010A = (byte[]) unPackLogin.getToken().get("tlv_t010A");
            }
            byte[] part1Outer = wifiCloudCheckSvc.part1Outer(cmd_0, unPackLogin.getPart1Token());

            byte[] outer = wifiCloudCheckSvc.outer(unPackLogin, part1Outer, part2Outer, hexTextUser, u);

            return outer;

        } catch (Exception e) {
            return null;
        }

    }


}
