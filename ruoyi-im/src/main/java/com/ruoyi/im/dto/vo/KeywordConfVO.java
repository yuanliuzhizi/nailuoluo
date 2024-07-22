package com.ruoyi.im.dto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel("设置机器人触发关键词回复配置")
public class KeywordConfVO {

    @ApiModelProperty(value = "黑名单qq：支持多个")
    @Size(max = 100, message = "黑名单数量过多(不可大于100)")
    private List<Integer> blackList;

    @ApiModelProperty(value = "群号：支持多个的群,不填的就是默认全部群")
    @Size(max = 100, message = "群号数量过多(不可大于100)")
    private List<Integer> groupId;

    @ApiModelProperty(value = "回应的对象: 群主qz 管理员admin 全体成员all")
    private String responseObject = "all";

    @ApiModelProperty(value = "是否艾特机器人")
    private Boolean isAt = false;

    @ApiModelProperty(value = "是否回复机器人")
    private Boolean isReply = false;

    @ApiModelProperty(value = "群主的消息")
    private Boolean isQz = false;

    @ApiModelProperty(value = "管理的消息")
    private Boolean isAdmin = false;

    @ApiModelProperty(value = "艾特全员消息")
    private Boolean isAll = false;

    @Valid
    @ApiModelProperty(value = "触发关键词组")
    @Size(max = 50, message = "关键词数量过多(不可大于50)")
    private List<KeywordVO> keywordVO;

    public List<Integer> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<Integer> blackList) {
        this.blackList = blackList;
    }

    public List<Integer> getGroupId() {
        return groupId;
    }

    public void setGroupId(List<Integer> groupId) {
        this.groupId = groupId;
    }

    public String getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(String responseObject) {
        this.responseObject = responseObject;
    }

    public Boolean getAt() {
        return isAt;
    }

    public void setAt(Boolean at) {
        isAt = at;
    }

    public Boolean getReply() {
        return isReply;
    }

    public void setReply(Boolean reply) {
        isReply = reply;
    }

    public Boolean getQz() {
        return isQz;
    }

    public void setQz(Boolean qz) {
        isQz = qz;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getAll() {
        return isAll;
    }

    public void setAll(Boolean all) {
        isAll = all;
    }

    public List<KeywordVO> getKeywordVO() {
        return keywordVO;
    }

    public void setKeywordVO(List<KeywordVO> keywordVO) {
        this.keywordVO = keywordVO;
    }
}
