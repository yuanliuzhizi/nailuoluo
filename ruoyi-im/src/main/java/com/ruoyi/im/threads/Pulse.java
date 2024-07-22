package com.ruoyi.im.threads;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.im.cmd.GetOnlineStatus;
import com.ruoyi.im.utils.MySocket;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 心跳包
 * 2022-7-9
 * by GuangHeLiZi
 * 更多请鉴
 */
public class Pulse implements Callable<Object> {

    private SysUser sysUser;
    private Map<Long, Map> queueList;

    public Pulse(SysUser sysUser, Map<Long, Map> queueList){
        this.sysUser = sysUser;
        this.queueList = queueList;
    }
    @Override
    public Object call() {
        try {
            Map map = queueList.get(sysUser.getUserId());
            while (map != null) {
                MySocket mySocket = (MySocket) map.get("mySocket");
                Thread.sleep(5000);
                try {
                    GetOnlineStatus getOnlineStatus=new GetOnlineStatus();
                    byte[] getOnlineStatusData = getOnlineStatus.getOnlineStatus(map);
                    mySocket.sendData(getOnlineStatusData);
                }catch (Exception e){//这里的过程不管什么异常都不会结束这个线程
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Pulse over";
    }
}