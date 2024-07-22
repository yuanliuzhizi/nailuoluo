package com.ruoyi.im.service.impl;

import com.ruoyi.im.cmd.msg.friend.MessageSvcPbSendMsg;
import com.ruoyi.im.service.SendService;
import com.ruoyi.im.utils.MySocket;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 发送
 *
 * @author UOHE
 */
@Service
public class SendServiceImpl implements SendService {

    @Override
    public void userMsgText(Map initUser, String userGo, String msg) {
        MySocket mySocket= (MySocket) initUser.get("mySocket");
        MessageSvcPbSendMsg messageSvcPbSendMsg=new MessageSvcPbSendMsg();
        byte[] messageSvcPbSendMsgData=messageSvcPbSendMsg.messageSvcPbSendMsg(initUser,userGo,msg);
        mySocket.sendData(messageSvcPbSendMsgData);
    }

    @Override
    public void groupMsgText(Map initUser, String group, String msg) {
        MySocket mySocket= (MySocket) initUser.get("mySocket");
        com.ruoyi.im.cmd.msg.group.MessageSvcPbSendMsg messageSvcPbSendMsg=new com.ruoyi.im.cmd.msg.group.MessageSvcPbSendMsg();
        byte[] messageSvcPbSendMsgData=messageSvcPbSendMsg.messageSvcPbSendMsg(initUser,group,msg);
        mySocket.sendData(messageSvcPbSendMsgData);
    }
}
