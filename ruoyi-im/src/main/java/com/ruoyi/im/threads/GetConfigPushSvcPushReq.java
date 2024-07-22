package com.ruoyi.im.threads;

import com.ruoyi.common.constant.UploadInfoConstant;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.RedisUtil;
import com.ruoyi.im.cmd.ConfigPushSvcPushReq;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.utils.MySocket;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 两种上传方式,tcp或者http这个是获取tcp返回的上传服务器参数
 * 2022-7-9
 * by GuangHeLiZi
 * 更多请鉴
 */
public class GetConfigPushSvcPushReq implements Callable<Object> {

    private SysUser sysUser;
    private Map<Long, Map> queueList;
    private RedisUtil redisUtil;

    public GetConfigPushSvcPushReq(SysUser sysUser, Map<Long, Map> queueList, RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
        this.sysUser = sysUser;
        this.queueList = queueList;
    }

    @Override
    public Object call() {
        try {
            Map map = queueList.get(sysUser.getUserId());
            while (map != null) {
                MySocket mySocket = (MySocket) map.get("mySocket");
                User user = (User) map.get("user");
                Thread.sleep(1000);
                try {
                    String jsonStr = redisUtil.get(UploadInfoConstant.QQ_UPLOAD + user.getUser());
                    if (jsonStr == null) {
                        //循环取这个回执,直到获取关键参数
                        ConfigPushSvcPushReq configPushSvcPushReq=new ConfigPushSvcPushReq();
                        byte[] configPushSvcPushReqData = configPushSvcPushReq.configPushSvcPushReq(map);
                        mySocket.sendData(configPushSvcPushReqData);
                    }else {
                        return "ConfigPushSvcPushReq over";
                    }
                } catch (Exception e) {//这里的过程不管什么异常都不会结束这个线程
                    e.printStackTrace();
                }
            }

            return "ConfigPushSvcPushReq over";

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ConfigPushSvcPushReq over";
    }



}