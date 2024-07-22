package com.ruoyi.im.dto;

import javax.validation.constraints.NotBlank;

public class GroupSendDTO {

    @NotBlank(message = "收信人qq不可空")
    private String group;

    @NotBlank(message = "收信人消息不可空")
    private String msg;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
