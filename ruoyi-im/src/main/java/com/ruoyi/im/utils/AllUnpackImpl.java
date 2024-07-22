package com.ruoyi.im.utils;

import com.ruoyi.common.constant.QueueUserInfoConstant;
import com.ruoyi.common.constant.QunFriendConstant;
import com.ruoyi.common.constant.UploadInfoConstant;
import com.ruoyi.common.json.JSONObject;
import com.ruoyi.common.utils.RedisUtil;
import com.ruoyi.im.cmd.MessageSvcPbGetMsg;
import com.ruoyi.im.entity.UnPackLogin;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.enums.Cmd;
import com.ruoyi.im.service.TEAService;
import com.ruoyi.im.service.impl.TEAServiceImpl;
import com.ruoyi.im.tool.JCEUnpack;
import com.ruoyi.im.tool.Jce;
import com.ruoyi.im.tool.Protobuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllUnpackImpl {

    private TEAService teaService = new TEAServiceImpl();

    public void unPack(byte[] data, Map initUser, RedisUtil redisUtil) {
        User user = (User) initUser.get("user");
        MySocket mySocket = (MySocket) initUser.get("mySocket");
        UnPackLogin unPackLogin = (UnPackLogin) initUser.get("unPackLogin");

        UnPack unPack = new UnPack();
        unPack.setData(data);
        unPack.getInt();
        int type = unPack.getInt();
        byte key = unPack.getByte();
        unPack.getByte();
        unPack.getBin(unPack.getInt() - 4);
        if (key == (byte) 1) {
            try {
                unPack.setData(teaService.decrypt(unPack.getAll(), (byte[]) unPackLogin.getToken().get("tlv_t0305")));
                byte[] part1Outer = unPack.getBin(unPack.getInt() - 4);
                byte[] part2Outer = unPack.getBin(unPack.getInt() - 4);
                unPack.setData(part1Outer);
                byte[] seq = unPack.getBin(4);
                unPack.getInt();
                unPack.getInt();
                byte[] cmd = unPack.getBin(unPack.getInt() - 4);
                unPackLogin.setPart1Token(unPack.getBin(unPack.getInt() - 4));

                if (Cmd.CMD_22.equals(ByteToAll.byteToText(cmd))) {
                    System.out.println(ByteToAll.byteToText(cmd)+"part2Outer = " + ByteToAll.byteToHxe(part2Outer));
                }
                if (Cmd.CMD_21.equals(ByteToAll.byteToText(cmd))) {
//                    unGroupLink(part2Outer, redisUtil, user);
                } else if (Cmd.CMD_28.equals(ByteToAll.byteToText(cmd))) {
                    unPack(part2Outer, redisUtil, user);
                } else if (Cmd.CMD_29.equals(ByteToAll.byteToText(cmd))) {//发送回执
                    mySocket.sendData(new MessageSvcPbGetMsg().messageSvcPbGetMsg(initUser));
                    return;
                } else if (Cmd.CMD_30.equals(ByteToAll.byteToText(cmd))) {
                    resUser(part2Outer,redisUtil,user);
                    return;
                } else if (Cmd.CMD_31.equals(ByteToAll.byteToText(cmd))) {
                    resUserThis(part2Outer,redisUtil,user);
                    return;
                } else if (Cmd.CMD_32.equals(ByteToAll.byteToText(cmd))) {
                    resGroup(part2Outer,redisUtil,user);
                    return;
                } else if (Cmd.CMD_33.equals(ByteToAll.byteToText(cmd))) {
                    revoke(part2Outer,redisUtil,user);
                    return;
                } else {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        System.out.println("其他包2");
        System.out.println("返回数据：" + ByteToAll.byteToHxe(data));
    }

    public void resUser(byte[] part2Outer,RedisUtil redisUtil,User user) {
        String zlibData;
        try {
            zlibData = ByteToAll.byteToHxe(ZLibUtils.decompress(AllToByte.hexToByte(ByteToAll.byteToHxe(part2Outer))));
        } catch (Exception e) {
            zlibData = ByteToAll.byteToHxe(part2Outer);
        }
        List l = new ArrayList();
        l.add(Integer.valueOf(0));
        List list = Protobuf.analysisAllPB(zlibData);
        List list2 = Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB((String) ((Map) list.get(4)).get("data")).get(3)).get("data"));
        Map map3 = (Map) list2.get(2);
        List list3 = Protobuf.analysisAllPB((String) ((Map) list2.get(0)).get("data"));
        l.add(((Map) list3.get(4)).get("lv"));
        l.add(((Map) list3.get(0)).get("lv"));
        l.add(((Map) list3.get(1)).get("lv"));
        l.add(((Map) list3.get(5)).get("lv"));
        l.add(((Map) list3.get(2)).get("lv"));//判断消息类型
        if ("87".equals(((Map) list3.get(2)).get("lv"))) {//87 是好友邀请入群
            l.add(((Map) list3.get(7)).get("lv"));//userGo
            l.add(zlibData);
        } else {
            if (list3.size() == 11) {
                l.add(((Map) list3.get(7)).get("lv"));
                l.add(((Map) Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB((String) ((Map) list.get(4)).get("data")).get(3)).get("data")).get(2)).get("data")).get(1)).get("data"));
            } else {
                l.add(((Map) Protobuf.analysisAllPB((String) map3.get("data")).get(0)).get("data"));
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mgsType", 0);//消息类型群消息
        if ("87".equals(((Map) list3.get(2)).get("lv"))) {//87 是好友邀请入群 166 好友消息
            jsonObject.put("msgReq", l.get(1));//消息标识
            jsonObject.put("groupId", l.get(2));//群号
            jsonObject.put("userId", l.get(3));//发送qq
            jsonObject.put("msgTime", l.get(4));//消息时间
            jsonObject.put("msgType", l.get(5));//判断消息类型
            jsonObject.put("userGo", l.get(6));//接收qq
            jsonObject.put("msgPB", l.get(7));//消息protobuf
//            System.out.println("好友邀请入群： " + jsonObject);
        } else if ("166".equals(((Map) list3.get(2)).get("lv"))) {//166 好友消息
            jsonObject.put("msgReq", l.get(1));//消息标识
            jsonObject.put("userId", l.get(2));//发送qq
            jsonObject.put("userGo", l.get(3));//接收qq
            jsonObject.put("msgTime", l.get(4));//消息时间
            jsonObject.put("msgType", l.get(5));//判断消息类型
            jsonObject.put("msgPB", l.get(6));//消息protobuf
//            System.out.println("好友消息： " + jsonObject);
        } else {//未知的直接发送完整pb分析
            jsonObject.put("msgType", -1);//判断消息类型
            jsonObject.put("data", zlibData);//分析
//            System.out.println("未解析消息： " + jsonObject);
        }

        redisUtil.enqueue(QueueUserInfoConstant.QQ_USER_MSG+user.getUser(),jsonObject.toCompactString());

    }

    public void resUserThis(byte[] part2Outer,RedisUtil redisUtil,User user) {
        String zlibData;
        try {
            zlibData = ByteToAll.byteToHxe(ZLibUtils.decompress(AllToByte.hexToByte(ByteToAll.byteToHxe(part2Outer))));
        } catch (Exception e) {
            zlibData = ByteToAll.byteToHxe(part2Outer);
        }
        List l = new ArrayList();
        l.add(Integer.valueOf(2));
        List list1 = Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB(zlibData).get(0)).get("data"));
        Map map2 = (Map) list1.get(2);
        List list2 = Protobuf.analysisAllPB((String) ((Map) list1.get(0)).get("data"));
        l.add(((Map) list2.get(4)).get("lv"));
        l.add(((Map) list2.get(0)).get("lv"));
        l.add(((Map) list2.get(1)).get("lv"));
        l.add(((Map) list2.get(5)).get("lv"));
        l.add(((Map) Protobuf.analysisAllPB((String) map2.get("data")).get(0)).get("data"));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mgsType", 2);//消息类型群消息本人发送
        jsonObject.put("msgReq", l.get(1));//消息标识
        jsonObject.put("userId", l.get(2));//发送人qq
        jsonObject.put("userGo", l.get(3));//接收人qq
        jsonObject.put("msgTime", l.get(4));//消息时间
        jsonObject.put("msgPB", l.get(5));//消息protobuf
//        System.out.println("my消息： " + jsonObject);
        redisUtil.enqueue(QueueUserInfoConstant.QQ_MY_MSG+user.getUser(),jsonObject.toCompactString());
    }


    public void resGroup(byte[] part2Outer,RedisUtil redisUtil,User user) {
        String zlibData;
        try {
            zlibData = ByteToAll.byteToHxe(ZLibUtils.decompress(AllToByte.hexToByte(ByteToAll.byteToHxe(part2Outer))));
        } catch (Exception e) {
            zlibData = ByteToAll.byteToHxe(part2Outer);
        }

        List l = new ArrayList();
        l.add(Integer.valueOf(1));
        List list = Protobuf.analysisAllPB(zlibData);
        List list3 = Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB((String) ((Map) list.get(0)).get("data")).get(0)).get("data"));
        l.add(((Map) list3.get(4)).get("lv"));
        List list4 = Protobuf.analysisAllPB((String) ((Map) list3.get(7)).get("data"));
        Map m6 = (Map) list4.get(3);
        Map m7 = (Map) list4.get(6);
        l.add(((Map) list4.get(0)).get("lv"));
        l.add(m6.get("data"));
        l.add(m7.get("data"));
        l.add(((Map) list3.get(0)).get("lv"));
        l.add(((Map) Protobuf.analysisAllPB((String) Protobuf.analysisPB((String) ((Map) Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB((String) ((Map) list.get(0)).get("data")).get(2)).get("data")).get(0)).get("data")).get("data")).get(1)).get("lv"));
        l.add(((Map) Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB((String) ((Map) Protobuf.analysisAllPB(zlibData).get(0)).get("data")).get(0)).get("remain")).get(0)).get("remain")).get(0)).get("data")).get(0)).get("data"));


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mgsType", 1);//消息类型群消息
        jsonObject.put("msgReq", l.get(1));//消息标识
        jsonObject.put("groupId", l.get(2));//群号
        jsonObject.put("userName", l.get(3));//发送用户名称
        jsonObject.put("groupName", l.get(4));//发送群名称
        jsonObject.put("userId", l.get(5));//发送人qq
        jsonObject.put("msgTime", l.get(6));//消息时间
        jsonObject.put("msgPB", l.get(7));//消息protobuf
//        System.out.println("群消息： " + jsonObject);
        redisUtil.enqueue(QueueUserInfoConstant.QQ_GROUP_MSG+user.getUser(),jsonObject.toCompactString());

    }

    public void revoke(byte[] part2Outer,RedisUtil redisUtil,User user) {
        String zlibData;
        try {
            zlibData = ByteToAll.byteToHxe(ZLibUtils.decompress(AllToByte.hexToByte(ByteToAll.byteToHxe(part2Outer))));
        } catch (Exception e) {
            zlibData = ByteToAll.byteToHxe(part2Outer);
        }
        List l = new ArrayList();
        l.add(Integer.valueOf(3));
        l.add(zlibData);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mgsType", 3);//消息类型群消息本人发送
        jsonObject.put("msgPB", l.get(1));//消息protobuf
//        System.out.println("在线消息推： " + jsonObject);
        redisUtil.enqueue(QueueUserInfoConstant.QQ_PUSH_MSG+user.getUser(),jsonObject.toCompactString());

    }

    public boolean unPack(byte[] part2Outer,RedisUtil redisUtil,User user) {
        JCEUnpack jceUnpack = new JCEUnpack(ZLibUtils.decompress(part2Outer));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readString(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readString(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readSimpleList(jceUnpack.bs).get("v")));
        jceUnpack.wrap(AllToByte.hexToByte((String) ((Map) jceUnpack.readMap(jceUnpack.bs).get("v")).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) ((Map) jceUnpack.readMap(jceUnpack.bs).get("v")).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte(Jce.tagSE((String) jceUnpack.readSimpleList(jceUnpack.bs).get("v"))));
        Map map11 = jceUnpack.readNumber(jceUnpack.bs);
        if ("2".equals(map11.get("v").toString())) {
            jceUnpack.wrap(AllToByte.hexToByte((String) map11.get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readSimpleList(jceUnpack.bs).get("v")));
            JCEUnpack jceUnpack1 = new JCEUnpack(jceUnpack.bs);
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack1.readListConfigPushSvc(jceUnpack1.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack1.readListConfigPushSvc(jceUnpack.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack1.readListConfigPushSvc(jceUnpack.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack1.readListConfigPushSvc(jceUnpack.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte(((String) jceUnpack1.readListConfigPushSvc(jceUnpack.bs).get("remain")).substring(2)));
            Map map = jceUnpack1.readListConfigPushSvcJ5(jceUnpack.bs);
            ArrayList<ArrayList> list = (ArrayList) map.get("v");
            jceUnpack.wrap(AllToByte.hexToByte((String) map.get("remain")));
            Map map13 = jceUnpack.readSimpleList(jceUnpack.bs);
            String videoToken = (String) map13.get("v");
            jceUnpack.wrap(AllToByte.hexToByte((String) map13.get("remain")));
            String ukey = (String) jceUnpack.readSimpleList(jceUnpack.bs).get("v");

            ArrayList addr = list.get(0);
            String host = (String) addr.get(0);
            String port = addr.get(1).toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("host", host);
            jsonObject.put("port", port);
            jsonObject.put("videoToken", videoToken);
            jsonObject.put("ukey", ukey);
            redisUtil.set(UploadInfoConstant.QQ_UPLOAD + user.getUser(), jsonObject.toCompactString(), UploadInfoConstant.expireIn);
            System.out.println("以解析上传key");
            return true;
        }
        return false;
    }

    public void unGroupLink(byte[] part2Outer,RedisUtil redisUtil,User user) {
        try {
            JCEUnpack jceUnpack = new JCEUnpack(ZLibUtils.decompress(part2Outer));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readString(jceUnpack.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readString(jceUnpack.bs).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readSimpleList(jceUnpack.bs).get("v")));
            jceUnpack.wrap(AllToByte.hexToByte((String) ((Map) jceUnpack.readMapSimpleList(jceUnpack.bs).get("v")).get("remain")));
            jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readSimpleList(jceUnpack.bs).get("v")));
            jceUnpack.wrap(AllToByte.hexToByte(Jce.tagSE(ByteToAll.byteToHxe(jceUnpack.bs.array()))));
            //本人qq
            Map qqMap = jceUnpack.readNumber(jceUnpack.bs);
            jceUnpack.wrap(AllToByte.hexToByte((String) qqMap.get("remain")));
            Map map = jceUnpack.readNumber(jceUnpack.bs);
            jceUnpack.wrap(AllToByte.hexToByte((String) map.get("remain")));
            Map qunLinkMap = jceUnpack.readNumber(jceUnpack.bs);
            jceUnpack.wrap(AllToByte.hexToByte((String) qunLinkMap.get("remain")));
            Map userMap = jceUnpack.readList(jceUnpack.bs);
            String userGo = userMap.get("v").toString();
            redisUtil.set(QunFriendConstant.QQ_QUN_FRIEND + user.getUser() + ":" + userGo, qunLinkMap.get("v").toString(), QunFriendConstant.expireIn);
        }catch (Exception e){
            System.out.println("解析part2Outer = " + ByteToAll.byteToHxe(part2Outer));
        }


    }

}
