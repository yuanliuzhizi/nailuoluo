package com.ruoyi.im.dto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("关键词组")
public class KeywordVO {

    @ApiModelProperty(value = "正则表达式判断是否处理消息例子：\\\\b(你好|菜单)\\\\b")
    private String regexKey = "*";

    @ApiModelProperty(value = "这是正则匹配后的处理方式:不填直接发送|at(艾特ta)|reply(回复ta)|jinyan(禁言此人)|t(t出群)|ch(撤回消息)|si(群私聊消息)")
    private String msgType = "";

    @ApiModelProperty(value = "正则匹配成功回复文本内容,如果是其他比如 t人 则不生效")
    private String sendMsg = "这是一条默认消息";

    public String getRegexKey() {
        return regexKey;
    }

    public void setRegexKey(String regexKey) {
        this.regexKey = regexKey;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(String sendMsg) {
        this.sendMsg = sendMsg;
    }
}
