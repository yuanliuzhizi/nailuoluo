package com.ruoyi.im.threads;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.QueueUserInfoConstant;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.RedisUtil;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.utils.MySocket;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 消息处理
 * 2022-7-9
 * by GuangHeLiZi
 * 更多请鉴
 */
public class MsgMy implements Callable<Object> {

    private SysUser sysUser;
    private Map<Long, Map> queueList;
    private RedisUtil redisUtil;
    public MsgMy(SysUser sysUser, Map<Long, Map> queueList, RedisUtil redisUtil) {
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
                        String jsonStr = redisUtil.dequeue(QueueUserInfoConstant.QQ_MY_MSG + user.getUser());
                        if (jsonStr != null) {
                            JSONObject jsonObject = JSON.parseObject(jsonStr);
                            System.out.println("jsonObject = " + jsonObject);
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



}