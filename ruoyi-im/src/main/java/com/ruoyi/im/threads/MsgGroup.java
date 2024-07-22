package com.ruoyi.im.threads;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.ruoyi.common.constant.QueueUserInfoConstant;
import com.ruoyi.common.constant.QunFriendConstant;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.RedisUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.im.cmd.PbMessageSvcPbMsgWithDraw;
import com.ruoyi.im.cmd.msg.FriendListGetTroopAppointRemarkReq;
import com.ruoyi.im.cmd.msg.group.OidbSvc0x570_8;
import com.ruoyi.im.cmd.msg.group.OidbSvc0x8a0_0;
import com.ruoyi.im.cmd.msg.siliao.MessageSvcPbSendMsg;
import com.ruoyi.im.dto.KeywordListDTO;
import com.ruoyi.im.dto.vo.KeywordConfVO;
import com.ruoyi.im.dto.vo.KeywordVO;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;
import com.ruoyi.im.utils.MySocket;
import com.ruoyi.im.utils.QQAlgorithm;
import org.apache.poi.ss.formula.functions.T;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ruoyi.im.tool.Protobuf.analysisAllPB;
import static com.ruoyi.im.tool.Protobuf.analysisPB;

/**
 * 消息处理
 * 2022-7-9
 * by GuangHeLiZi
 * 更多请鉴
 */
public class MsgGroup implements Callable<Object> {

    private SysUser sysUser;
    private Map<Long, Map> queueList;
    private RedisUtil redisUtil;

    public MsgGroup(SysUser sysUser, Map<Long, Map> queueList, RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
        this.sysUser = sysUser;
        this.queueList = queueList;
    }

    @Override
    public Object call() {
        try {
            Map map = queueList.get(sysUser.getUserId());
            while (map != null) {
                Boolean isOk = (Boolean) map.get("msgThreadStart");
                if (isOk != null && isOk == true) {
                    MySocket mySocket = (MySocket) map.get("mySocket");
                    User user = (User) map.get("user");
                    Thread.sleep(200);
                    try {
                        String jsonStr = redisUtil.dequeue(QueueUserInfoConstant.QQ_GROUP_MSG + user.getUser());
                        if (jsonStr != null) {
                            JSONObject jsonObject = JSON.parseObject(jsonStr);
                            Integer mgsType = (Integer) jsonObject.get("mgsType");//消息类型 1群消息
                            String msgReq = (String) jsonObject.get("msgReq");//消息标识
                            String groupId = (String) jsonObject.get("groupId");//群号
                            String userName = (String) jsonObject.get("userName");//用户名称
                            String groupName = (String) jsonObject.get("groupName");//群名称
                            String userId = (String) jsonObject.get("userId");//qq
                            String msgTime = (String) jsonObject.get("msgTime");//消息时间戳
                            String msgPB = (String) jsonObject.get("msgPB");//pb结构
                            ArrayList<HashMap> list = (ArrayList<HashMap>) analysisAllPB(msgPB);
                            try {
                                jsonObject.put("msgGuid", ((Map) analysisAllPB((String) ((HashMap) list.get(0)).get("data")).get(2)).get("lv"));//撤回的标识
                            } catch (Exception e) {
                                jsonObject.put("msgGuid", "1");
                            }
                            String msgGuid = (String) jsonObject.get("msgGuid");//撤回的标识

                            //1文字符号消息 2表情消息 5文件 8图片 12转发或者卡片 19手机视频 24群收账 45回复 51json卡片 53低版本提示

                            //是否是机器人本人消息
                            Boolean isMyMsg = false;
                            if (userId.equals(user.getUser())) {
                                isMyMsg = true;
                            }
                            //判断是否是回复本人消息
                            Boolean isReply = false;
                            //判断是否是at本人消息
                            Boolean isAt = false;
                            //判断是否是群主消息
                            Boolean isQz = false;
                            //判断是否是管理消息
                            Boolean isAdmin = false;
                            //判断是否是at全体消息
                            Boolean isAll = false;
                            JSONArray qqMsgJson = new JSONArray();
                            for (HashMap hashMap : list) {
                                String tag = (String) hashMap.get("tag");
                                //这里的2是消息相关的
                                if ("2".equals(tag)) {
                                    String msg = (String) hashMap.get("data");
                                    Map msgMap = analysisPB(msg);
                                    String msgTag = (String) msgMap.get("tag");
                                    //判断是否是回复消息
                                    if ("45".equals(msgTag) && isReply == false) {
                                        String reply = (String) msgMap.get("data");
                                        ArrayList<HashMap> replyList = (ArrayList<HashMap>) analysisAllPB(reply);
                                        for (HashMap replyMap : replyList) {
                                            String qTag = (String) replyMap.get("tag");
                                            if ("2".equals(qTag)) {
                                                String qq = (String) replyMap.get("lv");
                                                if (qq.equals(user.getUser())) {
                                                    isReply = true;
                                                }
                                            }

                                        }
                                    }

                                    //判断是否是at消息
                                    if ("1".equals(msgTag)) {
                                        String at = (String) msgMap.get("data");
                                        ArrayList<HashMap> atList = (ArrayList<HashMap>) analysisAllPB(at);
                                        for (HashMap atMap : atList) {
                                            String atTag = (String) atMap.get("tag");
                                            if ("12".equals(atTag)) {
                                                String qqData = (String) atMap.get("data");
                                                ArrayList<HashMap> qqList = (ArrayList<HashMap>) analysisAllPB(qqData);
                                                for (HashMap qqMap : qqList) {
                                                    String qqTag = (String) qqMap.get("tag");
                                                    //1是全体成员 2是成员
                                                    if ("3".equals(qqTag) && isAll == false) {
                                                        String all = (String) qqMap.get("lv");
                                                        if ("1".equals(all)) {
                                                            isAll = true;
                                                        }
                                                    }
                                                    //at机器人
                                                    if ("4".equals(qqTag) && isAt == false) {
                                                        String qq = (String) qqMap.get("lv");
                                                        if (qq.equals(user.getUser())) {
                                                            isAt = true;
                                                        }
                                                    }
                                                }
                                            }

                                            //是文本消息
                                            if (atList.size() == 1) {
                                                if ("1".equals(atTag)) {
                                                    String qqMsg = (String) atMap.get("data");
                                                    put(qqMsgJson, 1, "text", qqMsg, "纯文本消息");
                                                }
                                            }

                                        }
                                    }

                                    //记录qq表情消息
                                    if ("2".equals(msgTag)) {
                                        String emo = (String) msgMap.get("data");
                                        ArrayList<HashMap> emoList = (ArrayList<HashMap>) analysisAllPB(emo);
                                        for (HashMap emoMap : emoList) {
                                            String emoTag = (String) emoMap.get("tag");
                                            if ("1".equals(emoTag)) {
                                                String emoCode = (String) emoMap.get("lv");
                                                put(qqMsgJson, 2, "emo", emoCode, "qq的emo消息");
                                            }
                                        }
                                    }

                                    //记录qq图片消息
                                    if ("8".equals(msgTag)) {
                                        String img = (String) msgMap.get("data");
                                        ArrayList<HashMap> imgList = (ArrayList<HashMap>) analysisAllPB(img);
                                        for (HashMap imgMap : imgList) {
                                            String imgTag = (String) imgMap.get("tag");
                                            //图片的md5
                                            if ("13".equals(imgTag)) {
                                                String imgMd5 = (String) imgMap.get("data");
                                                put(qqMsgJson, 8, "img", imgMd5, "qq的图片消息");
                                            }

                                        }
                                    }

                                    //判断 群主消息4=8 管理员消息4=16 群员4不存在
                                    if ("16".equals(msgTag) && isQz == false && isAdmin == false) {
                                        String identity = (String) msgMap.get("data");
                                        ArrayList<HashMap> identityList = (ArrayList<HashMap>) analysisAllPB(identity);
                                        for (HashMap identityMap : identityList) {
                                            String identityMapTag = (String) identityMap.get("tag");
                                            if ("4".equals(identityMapTag)) {
                                                String identityType = (String) identityMap.get("lv");
                                                //群主
                                                if ("8".equals(identityType)) {
                                                    isQz = true;
                                                }
                                                //管理
                                                if ("16".equals(identityType)) {
                                                    isAdmin = true;
                                                }

                                            }

                                        }
                                    }


                                }

                            }

                            System.out.println("判断是否是机器本人消息isMyMsg = " + isMyMsg);
                            System.out.println("判断是否是at本人消息isAt = " + isAt);
                            System.out.println("判断是否是回复本人消息isReply = " + isReply);
                            System.out.println("判断是否是群主消息isQz = " + isQz);
                            System.out.println("判断是否是管理消息isAdmin = " + isAdmin);
                            System.out.println("判断是否是at全体消息isAll = " + isAll);
                            jsonObject.put("isMyMsg", isMyMsg);
                            jsonObject.put("isAt", isAt);
                            jsonObject.put("isReply", isReply);
                            jsonObject.put("isQz", isQz);
                            jsonObject.put("isAll", isAll);
                            jsonObject.put("qqMsgJson", qqMsgJson);
                            StringBuffer qqMsgText = new StringBuffer();
                            for (Object o : qqMsgJson) {
                                JSONObject json = (JSONObject) o;
                                String text = (String) json.get("text");
                                qqMsgText.append(StringUtils.trim(text));
                            }
                            jsonObject.put("qqMsgText", qqMsgText.toString());
                            System.out.println("jsonObject = " + jsonObject);
                            //这个是群关键词配置
//                            String keywordJson = redisUtil.get(QueueUserInfoConstant.QQ_GROUP_KEYWORD + user.getUser());
//                            if (keywordJson != null) {
//                                Gson gson = new Gson();
//                                KeywordListDTO keywordListDTO = gson.fromJson(keywordJson, KeywordListDTO.class);
//                                List<KeywordConfVO> keywordConfVO = keywordListDTO.getKeywordConfVO();
//                                for (KeywordConfVO confVO : keywordConfVO) {
//
//                                }
//                            }

                            //是机器人本人跳出本次判断
                            if (isMyMsg){
                                throw new Exception();
                            }

                            //这个是群关键词配置
                            String keywordJson = redisUtil.get(QueueUserInfoConstant.QQ_GROUP_KEYWORD + user.getUser());

                            //查看和编辑配置只能群主
                            if (keywordJson != null) {
                                if (isQz || isAdmin) {
                                    String msgText = ByteToAll.byteToText(AllToByte.hexToByte(qqMsgText.toString()));
                                    if (msgText.equals("查看配置")) {
                                        groupMsgText(map, groupId, JSONObject.toJSONString(keywordJson));
                                    }
                                }
                            }else {
                                groupMsgText(map, groupId, "配置不存在");
                            }

                            try {
                                String msgText = ByteToAll.byteToText(AllToByte.hexToByte(qqMsgText.toString()));
                                if (msgText.contains("编辑配置")) {
                                    msgText = msgText.replace("编辑配置", "");
                                    // 创建 Gson 对象
                                    String json = JSONArray.parse(msgText).toString();
                                    redisUtil.set(QueueUserInfoConstant.QQ_GROUP_KEYWORD + user.getUser(), json, QueueUserInfoConstant.expireIn24);
                                    groupMsgText(map, groupId, "配置成功");
                                }
                                if (msgText.contains("重置配置")) {
                                    String json = "{\"keywordConfVO\":[{\"blackList\":[0],\"groupId\":[0],\"responseObject\":\"\",\"isAt\":false,\"isReply\":false,\"isQz\":false,\"isAdmin\":false,\"isAll\":false,\"keywordVO\":[{\"regexKey\":\"\\\\b(你好|菜单)\\\\b\",\"msgType\":\"at\",\"sendMsg\":\"这是一条默认响应消息\"}]}]}";
                                    redisUtil.set(QueueUserInfoConstant.QQ_GROUP_KEYWORD + user.getUser(), json, QueueUserInfoConstant.expireIn24);
                                    groupMsgText(map, groupId, "重置配置成功");
                                }
                            }catch (Exception e){
                                groupMsgText(map, groupId, "配置失败");
                            }

                            if (keywordJson != null) {
                                Gson gson = new Gson();
                                KeywordListDTO keywordListDTO = gson.fromJson(keywordJson, KeywordListDTO.class);
                                List<KeywordConfVO> keywordConfVO = keywordListDTO.getKeywordConfVO();
                                for (KeywordConfVO confVO : keywordConfVO) {
                                    List<Integer> blackList = confVO.getBlackList();
                                    //黑名单是否匹配
                                    if (blackList != null && blackList.size() != 0 && blackList.contains(Long.parseLong(userId))) {
                                        //跳过本次循环
                                        continue;
                                    }
                                    List<Integer> groupIds = confVO.getGroupId();
                                    //作用群号是否匹配
                                    if (groupIds != null && groupIds.size() != 0 && !groupIds.contains(0) && !groupIds.contains(Long.parseLong(groupId))) {
                                        //跳过本次循环
                                        continue;
                                    }

                                    //是否需要at
                                    Boolean isAtCompare = confVO.getAt();
                                    if (isAtCompare != null && isAtCompare) {
                                        if (!isAt) {
                                            //跳过本次循环
                                            continue;
                                        }
                                    }

                                    //是否需要回复
                                    Boolean isReplyCompare = confVO.getReply();
                                    if (isReplyCompare != null && isReplyCompare) {
                                        if (!isReply) {
                                            //跳过本次循环
                                            continue;
                                        }
                                    }

                                    List<KeywordVO> keywordVOs = confVO.getKeywordVO();

                                    String responseObject = confVO.getResponseObject();
                                    //不管是谁都回复
                                    if (responseObject == null) {
                                        boolean ok = stop(map, keywordVOs, msgReq, groupId, userName, groupName, userId, msgTime, msgGuid, qqMsgText);
                                        if (ok) {
                                            break;
                                        }
                                    } else if ("qz".equals(responseObject) && isQz) {
                                        //是群主回复
                                        boolean ok = stop(map, keywordVOs, msgReq, groupId, userName, groupName, userId, msgTime, msgGuid, qqMsgText);
                                        if (ok) {
                                            break;
                                        }
                                    } else if ("admin".equals(responseObject) && isAdmin) {
                                        //是群管理回复
                                        boolean ok = stop(map, keywordVOs, msgReq, groupId, userName, groupName, userId, msgTime, msgGuid, qqMsgText);
                                        if (ok) {
                                            break;
                                        }
                                    } else if ("all".equals(responseObject) && isAll) {
                                        //是at全体成员回复
                                        boolean ok = stop(map, keywordVOs, msgReq, groupId, userName, groupName, userId, msgTime, msgGuid, qqMsgText);
                                        if (ok) {
                                            break;
                                        }
                                    } else {
                                        //不管是谁都回复
                                        boolean ok = stop(map, keywordVOs, msgReq, groupId, userName, groupName, userId, msgTime, msgGuid, qqMsgText);
                                        if (ok) {
                                            break;
                                        }
                                    }


                                }
                            }

                        }
                    } catch (Exception e) {//这里的过程不管什么异常都不会结束这个线程
                        e.printStackTrace();
                    }
                } else {
                    return "Msg over";
                }
            }

            return "Msg over";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Msg over";
    }


    private void put(JSONArray qqMsgJson, Integer type, String key, Object t, String explain) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put(key, t);
        json.put("explain", explain);
        qqMsgJson.add(json);
    }

    private Boolean stop(Map map, List<KeywordVO> keywordVOs, String msgReq, String groupId, String userName, String groupName, String userId, String msgTime, String msgGuid, StringBuffer qqMsgText) {
        String msgText = ByteToAll.byteToText(AllToByte.hexToByte(qqMsgText.toString()));
        userName = ByteToAll.byteToText(AllToByte.hexToByte(userName));
        groupName = ByteToAll.byteToText(AllToByte.hexToByte(groupName));

        for (KeywordVO keywordVO : keywordVOs) {
            String regexKey = keywordVO.getRegexKey();
            Pattern pattern = Pattern.compile(regexKey);
            Matcher matcher = pattern.matcher(msgText);
            //是否符合正则
            Boolean regexKeyIsOk = matcher.find();
            if (regexKeyIsOk) {
                String sendMsg = keywordVO.getSendMsg();
                //不填直接发送|at(艾特ta)|reply(回复ta)|jinyan(禁言此人)|t(t出群)|ch(撤回消息)|si(群私聊消息)
                String msgType = keywordVO.getMsgType();
                if (msgType == null) {
                    //直接发送
                    groupMsgText(map, groupId, sendMsg);
                    return true;
                } else if ("at".equals(msgType)) {
                    //at发送
                    groupMsgTextAt(map, groupId, userName, userId, sendMsg);
                    return true;
                } else if ("reply".equals(msgType)) {
                    //回复发送
                    groupMsgTextReply(map, groupId, msgReq, msgTime, regexKey, msgGuid, userId, sendMsg);
                    return true;
                } else if ("si".equals(msgType)) {
                    //群私聊发送
                    groupMsgTextSi(map, groupId, userId, sendMsg);
                    return true;
                } else if ("jinyan".equals(msgType)) {
                    //禁言后发送
                    groupJinYan(map, groupId, userId, 1l);
                    //直接发送
                    groupMsgText(map, groupId, sendMsg);
                    return true;
                } else if ("ch".equals(msgType)) {
                    //撤回后发送
                    groupWithDraw(map, groupId, msgGuid, msgReq);
                    //直接发送
                    groupMsgText(map, groupId, sendMsg);
                    return true;
                } else if ("t".equals(msgType)) {
                    //移出群后发送
                    groupTi(map, groupId, sendMsg);
                    //直接发送
                    groupMsgText(map, groupId, sendMsg);
                    return true;
                } else {
                    //直接发送
                    groupMsgText(map, groupId, sendMsg);
                    return true;
                }

            }
        }
        return false;
    }

    private void groupMsgText(Map initUser, String group, String msg) {
        MySocket mySocket = (MySocket) initUser.get("mySocket");
        com.ruoyi.im.cmd.msg.group.MessageSvcPbSendMsg messageSvcPbSendMsg = new com.ruoyi.im.cmd.msg.group.MessageSvcPbSendMsg();
        byte[] messageSvcPbSendMsgData = messageSvcPbSendMsg.messageSvcPbSendMsg(initUser, group, msg);
        mySocket.sendData(messageSvcPbSendMsgData);
    }

    private void groupMsgTextAt(Map initUser,String groupId,String nameAt,String userGo,String msg) {
        MySocket mySocket = (MySocket) initUser.get("mySocket");
        com.ruoyi.im.cmd.msg.group.MessageSvcPbSendMsgAt messageSvcPbSendMsgAt = new com.ruoyi.im.cmd.msg.group.MessageSvcPbSendMsgAt();
        byte[] messageSvcPbSendMsgAtData = messageSvcPbSendMsgAt.messageSvcPbSendMsgAt(initUser, groupId, nameAt, userGo, msg);
        mySocket.sendData(messageSvcPbSendMsgAtData);
    }

    private void groupMsgTextReply(Map initUser,String groupId, String msgReq, String msgTime, String msgReply, String msgGuid, String userGo, String msg) {
        MySocket mySocket = (MySocket) initUser.get("mySocket");
        com.ruoyi.im.cmd.msg.group.MessageSvcPbSendMsgReply messageSvcPbSendMsgReply = new com.ruoyi.im.cmd.msg.group.MessageSvcPbSendMsgReply();
        byte[] messageSvcPbSendMsgReplyData = messageSvcPbSendMsgReply.messageSvcPbSendMsgReply(initUser, groupId, msgReq, msgTime, msgReply, msgGuid, userGo, msg);
        mySocket.sendData(messageSvcPbSendMsgReplyData);
    }

    private void groupMsgTextSi(Map initUser, String groupId, String userGo, String msg) {
        MySocket mySocket = (MySocket) initUser.get("mySocket");
        MessageSvcPbSendMsg messageSvcPbSendMsg = new MessageSvcPbSendMsg();
        byte[] messageSvcPbSendMsgData = messageSvcPbSendMsg.messageSvcPbSendMsg(initUser, QQAlgorithm.groupToGID(groupId), userGo, msg);
        mySocket.sendData(messageSvcPbSendMsgData);
    }

    private void groupJinYan(Map initUser, String groupId, String userGo, Long time) {
        MySocket mySocket = (MySocket) initUser.get("mySocket");
        OidbSvc0x570_8 oidbSvc0x570_8 = new OidbSvc0x570_8();
        byte[] oidbSvc0x570_8Data = oidbSvc0x570_8.oidbSvc0x570_8(initUser, groupId, userGo, time);
        mySocket.sendData(oidbSvc0x570_8Data);
    }

    private void groupWithDraw(Map initUser,String groupId,String msgGuid,String msgReq) {
        MySocket mySocket = (MySocket) initUser.get("mySocket");
        PbMessageSvcPbMsgWithDraw pbMessageSvcPbMsgWithDraw = new PbMessageSvcPbMsgWithDraw();
        byte[] pbMessageSvcPbMsgWithDrawData = pbMessageSvcPbMsgWithDraw.pbMessageSvcPbMsgWithDraw(initUser, groupId, msgGuid, msgReq);
        mySocket.sendData(pbMessageSvcPbMsgWithDrawData);
    }

    private void groupTi(Map initUser,String groupId,String userGo) {
        MySocket mySocket = (MySocket) initUser.get("mySocket");
        OidbSvc0x8a0_0 oidbSvc0x8a0_0 = new OidbSvc0x8a0_0();
        byte[] oidbSvc0x8a0_0Data = oidbSvc0x8a0_0.oidbSvc0x8a0_0(initUser, groupId, userGo);
        mySocket.sendData(oidbSvc0x8a0_0Data);
    }


}