package com.ruoyi.im.enums;

import java.io.Serializable;


/**
 * 指令行
 * 2021-10-15
 * *严格
 *
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class Cmd implements Serializable {


    /**
     * cmd 手表
     * 常用指令
     *
     */
    public static final String SB_0="wtlogin.trans_emp";//获取二维码
    public static final String SB_1 ="wtlogin.login";//登录服务
    public static final String SB_2 ="ConnAuthSvr.sdk_auth_api";//禁玩
    public static final String CMD_2 ="StatSvc.register";//状态服务
    public static final String CMD_8 ="StatSvc.GetOnlineStatus";
    public static final String CMD_9 ="WifiCloudCheckSvc.req";
    public static final String CMD_12 ="OidbSvc.0x6de";//提交设备信息
    public static final String CMD_21 ="friendlist.GetTroopAppointRemarkReq";//临时消息获取qunReq
    public static final String CMD_22 ="MessageSvc.PbSendMsg";//发送消息
    public static final String CMD_28 ="ConfigPushSvc.PushReq";//尝试主动获取回执
    public static final String CMD_29 ="MessageSvc.PushNotify";
    public static final String CMD_30 ="MessageSvc.PbGetMsg";//获取好友消息 1无条件 1需要验证消息 2禁止添加 3自定义问题(回答正确自动同意) 4自定义问题(回答后需要对方确认)
    public static final String CMD_31 ="OnlinePush.PbC2CMsgSync";//自己发送的消息
    public static final String CMD_32 ="OnlinePush.PbPushGroupMsg";//群消息
    public static final String CMD_33 ="OnlinePush.ReqPush";//在线消息推 撤回 修改名称等
    public static final String CMD_44 ="OidbSvc.0x570_8";//禁言某人 0就是解禁
    public static final String CMD_46 ="OidbSvc.0x8a0_0";//移出群聊 可重新加入
    public static final String CMD_52 ="PbMessageSvc.PbMsgWithDraw";//撤回消息

}

