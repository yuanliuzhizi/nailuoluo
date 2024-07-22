package com.ruoyi.im.threads;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.RedisUtil;
import com.ruoyi.im.utils.AllUnpackImpl;
import com.ruoyi.im.utils.MySocket;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 解析包
 * 2022-7-9
 * by GuangHeLiZi
 * 更多请鉴
 */
public class AllUnPack implements Callable<Object> {

    private SysUser sysUser;
    private Map<Long, Map> queueList;
    private RedisUtil redisUtil;

    public AllUnPack(SysUser sysUser, Map<Long, Map> queueList, RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
        this.sysUser = sysUser;
        this.queueList = queueList;
    }

    @Override
    public Object call() {
        try {
            Map map = queueList.get(sysUser.getUserId());
            AllUnpackImpl allUnpack = new AllUnpackImpl();
            while (map != null) {
                MySocket mySocket = (MySocket) map.get("mySocket");
                Thread.sleep(100);
                try {
                    byte[] bytes = mySocket.getData();
                    if (bytes != null) {
                        allUnpack.unPack(bytes, map, redisUtil);
                    }
                } catch (Exception e) {//这里的过程不管什么异常都不会结束这个线程
                    e.printStackTrace();
                }
            }

            return "AllUnPack over";

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "AllUnPack over";
    }



}