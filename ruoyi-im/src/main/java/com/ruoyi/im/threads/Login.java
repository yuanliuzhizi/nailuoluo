package com.ruoyi.im.threads;


import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.RedisUtil;
import com.ruoyi.im.cmd.*;
import com.ruoyi.im.domain.QqRegister;
import com.ruoyi.im.entity.*;
import com.ruoyi.im.service.IQqRegisterService;
import com.ruoyi.im.service.TEAService;
import com.ruoyi.im.service.impl.TEAServiceImpl;
import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;
import com.ruoyi.im.utils.MySocket;
import com.ruoyi.im.utils.UnPack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 手表登录处理线程
 * 2022-7-9
 * by GuangHeLiZi
 * 更多请鉴
 */
public class Login implements Callable<Object> {
    private byte[] wtloginTrans_emp_stateData;
    private Map initUser;
    private SysUser sysUser;
    private IQqRegisterService iQqRegisterService;
    private Map<Long, Map> queueList;
    private RedisUtil redisUtil;

    public Login(Map initUser, byte[] wtloginTrans_emp_stateData, SysUser sysUser, IQqRegisterService iQqRegisterService, Map<Long, Map> queueList, RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
        this.wtloginTrans_emp_stateData = wtloginTrans_emp_stateData;
        this.sysUser = sysUser;
        this.iQqRegisterService = iQqRegisterService;
        this.queueList = queueList;
        this.initUser = initUser;
    }

    @Override
    public Object call() {
        try {
            for (int i = 0; i < 120; i++) {
                Thread.sleep(1000);
                try {
                    UnPackLogin unPackLogin = (UnPackLogin) initUser.get("unPackLogin");
                    byte[] loginState = unPackLogin.getLoginState();
                    if (loginState != null) {
                        if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                            break;
                        }
                    }
                    MySocket mySocket1 = (MySocket) initUser.get("mySocket");
                    mySocket1.sendData(wtloginTrans_emp_stateData);
                    unPackState(mySocket1.getData(), initUser, redisUtil);
                } catch (Exception e) {//这里的过程不管什么异常都不会结束这个线程
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Login over";
    }


    /**
     * 解析扫码状态
     *
     * @param data 未知的结构
     * @return int值
     */
    private void unPackState(byte[] data, Map initUser,RedisUtil redisUtil) {
        User u = (User) initUser.get("user");
        App app = (App) initUser.get("app");
        Android android = (Android) initUser.get("android");
        Ecdh ecdh = (Ecdh) initUser.get("ecdh");
        UnPackLogin unPackLogin = (UnPackLogin) initUser.get("unPackLogin");
        MySocket mySocket = (MySocket) initUser.get("mySocket");
        com.ruoyi.im.entity.Tlv t= (com.ruoyi.im.entity.Tlv) initUser.get("tlv");
        TEAService teaService = new TEAServiceImpl();
        UnPack unPack = new UnPack();
        unPack.setData(data);
        unPack.getInt();//包长 00 00 01 63//包长度355
        int type = unPack.getInt();//包类型 00 00 00 08//包类型8
        byte key = unPack.getByte();//包加密方式 02//包加密方式2
        unPack.getByte();//
        unPack.getInt();//00 00 00 05
        unPack.getByte();//30
        try {
            unPack.setData(teaService.decrypt(unPack.getAll(), new byte[16]));//outer
            byte[] part1Outer = unPack.getBin(unPack.getInt() - 4);//Len part1Outer
            byte[] part2Outer = unPack.getBin(unPack.getInt() - 4);//Len part2Outer
            //part1Outer
            unPack.setData(part1Outer);
            unPack.getBin(4);//用来配备发送的数据,seq相等就是此数据的返回包
            unPack.getInt();//00 00 00 00
            unPack.getInt();//00 00 00 04
            byte[] cmd = unPack.getBin(unPack.getInt() - 4);//用来配备发送的数据,cmd相等就是此数据的返回包
            byte[] part1Token = unPack.getBin(unPack.getInt() - 4);//token 记录一下，接下来的发送改成这里的

            unPack.setData(part2Outer);//part2Outer

            unPack.getByte();//02
            unPack.getShort();//包长度
            unPack.getShort();//1F 41
            unPack.getShort();//08 12
            unPack.getShort();//00 01
            unPack.getInt();//00 00 00 00
            unPack.getBin(3);//登录状态
            unPack.setData(unPack.getBin(unPack.getAll().length - 1));//去掉03
            unPack.setData(teaService.decrypt(unPack.getAll(), AllToByte.hexToByte(ecdh.getShare_key())));

            unPack.getShort();//00 00
            unPack.getShort();//长度
            unPack.getShort();//00 02
            unPack.getShort();//长度
            unPack.getBin(unPack.getShort());//00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
            unPack.getInt();//00 00 00 03
            unPack.getInt();//00 00 00 32
            unPack.getInt();//自增req
            unPack.getInt();//00 00 00 00
            unPack.getInt();//00 00 00 00
            unPack.getInt();//00 05 01 00
            unPack.getInt();//00 00 75 00
            int typeState = unPack.getInt();//扫码状态判断登录 00 00 10 30 未扫码 00 00 10 35 扫码待确认 00 00 10 36 扫码取消 00 00 10 00 返回登录所需参数
            if (typeState == 4096) {
                unPack.getInt();//00 00 00 00
                int userId = unPack.getInt();//qq
                unPack.getInt();//随机63 49 0E 5E
                short tlvSize = unPack.getShort();//tlv总数
                byte[] tlvByte = unPack.getAll();
                Map map = unTlv(tlvSize, tlvByte);
                map.put("userId", userId);
                u.setUser("" + userId);
                ecdh.setTgt_key((String) map.get("tlv_t001E"));
                Map<String, Object> tlv_t0144 = new HashMap<>();
                Map<String, Object> tlv_t0318 = new HashMap<>();//手表需要的
                Map<String, Object> tlv_t0106 = new HashMap<>();
                Map<String, Object> tlv_t016A = new HashMap<>();//手表需要的
                tlv_t0144.put("hexTgtKey", AllToByte.hexToByte(ecdh.getTgt_key()));
                tlv_t0318.put("tlv0318Token", AllToByte.hexToByte((String) map.get("tlv_t0065")));
                tlv_t0106.put("tlv0106Token", AllToByte.hexToByte((String) map.get("tlv_t0018")));
                tlv_t016A.put("tlv016AToken", AllToByte.hexToByte((String) map.get("tlv_t0019")));
                t.setTlv_t0144(tlv_t0144);
                t.setTlv_t0318(tlv_t0318);
                t.setTlv_t0106(tlv_t0106);
                t.setTlv_t016A(tlv_t016A);

                QqRegister qqRegister = iQqRegisterService.selectQqRegisterById(Integer.toUnsignedLong(userId));
                if (qqRegister != null) {
                    qqRegister.setUserId(sysUser.getUserId());
                    qqRegister.setTgtKey(ecdh.getTgt_key());
                    qqRegister.setTlvT0065((String)map.get("tlv_t0065"));
                    qqRegister.setTlvT0018((String)map.get("tlv_t0018"));
                    qqRegister.setTlvT0019((String)map.get("tlv_t0019"));
                    iQqRegisterService.updateQqRegister(qqRegister);
                }else {
                    QqRegister qqRegisterAdd = new QqRegister();
                    qqRegisterAdd.setUserId(sysUser.getUserId());
                    qqRegisterAdd.setQq(Integer.toUnsignedLong(userId));
                    qqRegisterAdd.setTgtKey(ecdh.getTgt_key());
                    qqRegisterAdd.setTlvT0065((String)map.get("tlv_t0065"));
                    qqRegisterAdd.setTlvT0018((String)map.get("tlv_t0018"));
                    qqRegisterAdd.setTlvT0019((String)map.get("tlv_t0019"));
                    iQqRegisterService.insertQqRegister(qqRegisterAdd);
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

//                            FutureTask futureTaskGetConfigPushSvcPushReq= new FutureTask(new GetConfigPushSvcPushReq(sysUser, queueList, redisUtil));
//                            new Thread(futureTaskGetConfigPushSvcPushReq).start();//两种上传方式,tcp或者http这个是获取tcp返回的上传服务器参数

                            FutureTask futureTaskPulse = new FutureTask(new Pulse(sysUser, queueList));
                            new Thread(futureTaskPulse).start();//开启心跳

                        }
                    }
                }

            }
//            if (typeState==4144){//未扫码
//                System.out.println("未扫码");
//            }
//            if (typeState==4149){//扫码待确认
//                System.out.println("扫码待确认");
//            }
//
//            if (typeState==4150){//扫码取消
//                System.out.println("扫码取消");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Map unTlv(short size, byte[] data) {
        UnPack unPack = new UnPack();
        unPack.setData(data);
        Map map = new HashMap();
        for (int i = 0; i < size; i++) {
            byte[] tlvCmd = unPack.getBin(2);//头
            short len = unPack.getShort();//取长度
            map.put("tlv_t" + ByteToAll.byteToHxe(tlvCmd), ByteToAll.byteToHxe(unPack.getBin(len)));
        }
        return map;
    }


}