package com.ruoyi.im.service;

import java.util.Map;

/**
 * 发送
 * 
 * @author UOHE
 */
public interface SendService {

    /**
     * 发送好友文本消息
     * @param userGo
     * @param msg
     */
    void userMsgText(Map initUser, String userGo, String msg);

    /**
     * 发送群文本消息
     * @param group
     * @param msg
     */
    void groupMsgText(Map initUser, String group, String msg);

}
