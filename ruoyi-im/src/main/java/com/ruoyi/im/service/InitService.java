package com.ruoyi.im.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.im.dto.KeywordListDTO;
import com.ruoyi.im.dto.RegisterDTO;

import java.util.Map;

/**
 * 初始化
 * 
 * @author UOHE
 */
public interface InitService {

    /**
     * 初始化
     *
     */
    Map init(SysUser sysUser, Map<Long, Map> queueList);

    /**
     * 登录
     *
     */
    Map register(RegisterDTO registerDTO,SysUser sysUser, Map<Long, Map> queueList);

    /**
     * 获取q群消息-编程式处理(开启消息处理线程后无效)
     *
     */
    JSONObject msgGroupGet(SysUser sysUser, Map<Long, Map> queueList);

    /**
     * 获取自己消息-编程式处理(开启消息处理线程后无效)
     *
     */
    JSONObject msgMyGet(SysUser sysUser, Map<Long, Map> queueList);

    /**
     * 获取好友消息-编程式处理(开启消息处理线程后无效)
     *
     */
    JSONObject msgUserGet(SysUser sysUser, Map<Long, Map> queueList);

    /**
     * 获取推消息-编程式处理(开启消息处理线程后无效)
     *
     */
    JSONObject msgPushGet(SysUser sysUser, Map<Long, Map> queueList);

    /**
     * 开启qq消息处理线程
     *
     */
    void msgThreadStart(SysUser sysUser, Map<Long, Map> queueList);

    /**
     * 设置机器人触发关键词回复配置
     *
     */
    void keywordCommit(SysUser sysUser, Map<Long, Map> queueList, KeywordListDTO keywordListDTO);

}
