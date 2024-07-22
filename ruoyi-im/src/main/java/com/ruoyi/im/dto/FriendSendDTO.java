package com.ruoyi.im.dto;

import javax.validation.constraints.NotBlank;

public class FriendSendDTO {

    @NotBlank(message = "收信人qq不可空")
    private String userGo;

    @NotBlank(message = "收信人消息不可空")
    private String msg;

    public String getUserGo() {
        return userGo;
    }

    public void setUserGo(String userGo) {
        this.userGo = userGo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
