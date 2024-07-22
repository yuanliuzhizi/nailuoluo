package com.ruoyi.im.threads;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.RedisUtil;
import com.ruoyi.im.cmd.*;
import com.ruoyi.im.entity.*;
import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;
import com.ruoyi.im.utils.MySocket;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 手表二次登录处理线程
 * 2022-7-9
 * by GuangHeLiZi
 * 更多请鉴
 */
public class Register implements Callable<Object> {

    private Map initUser;
    private SysUser sysUser;
    private Map<Long, Map> queueList;
    private RedisUtil redisUtil;

    public Register(Map initUser, SysUser sysUser, Map<Long, Map> queueList, RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
        this.initUser = initUser;
        this.sysUser = sysUser;
        this.queueList = queueList;
    }

    @Override
    public Object call() {
        try {
            for (int i = 0; i < 120; i++) {
                Thread.sleep(1000);
                try {
                    Ecdh ecdh = (Ecdh) initUser.get("ecdh");
                    UnPackLogin unPackLogin = (UnPackLogin) initUser.get("unPackLogin");
                    MySocket mySocket = (MySocket) initUser.get("mySocket");

                    byte[] loginState = unPackLogin.getLoginState();
                    if (loginState != null) {
                        if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                            break;
                        }
                    }

                    PackLogin packLogin = new PackLogin();
                    byte[] packLoginData = packLogin.packLogin(initUser);
                    if (packLoginData != null) {
                        boolean okHttpCode = mySocket.sendData(packLoginData);
                        if (okHttpCode) {
                            byte[] loginData = mySocket.getData();
                            unPackLogin.setData(loginData);
                            unPackLogin.setShare_key(AllToByte.hexToByte(ecdh.getShare_key()));
                            unPackLogin.setTgt_key(AllToByte.hexToByte(ecdh.getTgt_key()));
                            packLogin.unPackLogin(unPackLogin);
                            if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                                com.ruoyi.im.cmd.Register register=new com.ruoyi.im.cmd.Register();
                                byte[] registerData=register.register(initUser);
                                mySocket.sendData(registerData);
                                X6de x6de=new X6de();
                                mySocket.sendData(x6de.x6de(initUser));
                                WifiCloudCheckSvc wifiCloudCheckSvc=new WifiCloudCheckSvc();
                                mySocket.sendData(wifiCloudCheckSvc.wifiCloudCheckSvc(initUser));

                                FutureTask futureTask = new FutureTask(new AllUnPack(sysUser, queueList, redisUtil));
                                new Thread(futureTask).start();//开启消息解析

//                                FutureTask futureTaskGetConfigPushSvcPushReq= new FutureTask(new GetConfigPushSvcPushReq(sysUser, queueList, redisUtil));
//                                new Thread(futureTaskGetConfigPushSvcPushReq).start();//两种上传方式,tcp或者http这个是获取tcp返回的上传服务器参数

                                FutureTask futureTaskPulse = new FutureTask(new Pulse(sysUser, queueList));
                                new Thread(futureTaskPulse).start();//开启心跳


                            }
                        }
                    }

                } catch (Exception e) {//这里的过程不管什么异常都不会结束这个线程
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Login over";
    }



}