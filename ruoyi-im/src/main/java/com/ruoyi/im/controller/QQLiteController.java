package com.ruoyi.im.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.RedisUtil;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.im.cmd.LoginState;
import com.ruoyi.im.dto.FriendSendDTO;
import com.ruoyi.im.dto.GroupSendDTO;
import com.ruoyi.im.dto.KeywordListDTO;
import com.ruoyi.im.dto.vo.KeywordConfVO;
import com.ruoyi.im.dto.RegisterDTO;
import com.ruoyi.im.entity.UnPackLogin;
import com.ruoyi.im.service.InitService;
import com.ruoyi.im.service.SendService;
import com.ruoyi.im.utils.ByteToAll;
import com.ruoyi.im.utils.ImageUtil;
import com.ruoyi.im.utils.MySocket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Api(tags = "qq手表")
@Controller
@RequestMapping("/qqlite")
public class QQLiteController {

    private Map<Long, Map> queueList = new HashMap();

    @Autowired
    private InitService initService;

    @Autowired
    private SendService sendService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("redis测试")
    @GetMapping("/redis/sel")
    @ResponseBody
    public void dd() {
        redisUtil.set("aaa", "{\"1\":\"sdf\"}");
        String data = redisUtil.get("aaa");
        System.out.println("queueList = " + data);
    }

    @ApiOperation("获取qq手表登录二维码(一个账号对应一个qq手表APP)")
    @GetMapping("/get/qr")
    @ResponseBody
    public synchronized AjaxResult getQr() {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        if (map != null) {
            ajax.put("msg", "如果二维码失效,请调用停止请求后重新获取");
            return ajax;
        }
        if (queueList.size() >= 1000) {
            ajax.put("msg", "当前服务器登录人数过多,请稍候再试。" + map.size());
            return ajax;
        }
        Map login = initService.init(sysUser, queueList);
        ajax.put("msg", "请在2分钟内扫码登录");
        ajax.put("qr", login.get("qr"));
        ajax.put("登录二维码链接", "qr");
        ajax.put("或者复制到浏览器回车", "qrView");
        ajax.put("qrView", "http://localhost/qqlite/qr/?url="+URLEncoder.encode((String) login.get("qr"), StandardCharsets.UTF_8));
        try {
            byte[] qrCodeImage = ImageUtil.generateQRCodeImage(URLDecoder.decode((String) login.get("qr"), StandardCharsets.UTF_8));
            String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
            ajax.put("base64Image", base64Image);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ajax;
    }

    @ApiOperation("获取qq手表登录二维码(一个账号对应一个qq手表APP)")
    @PostMapping("/register")
    @ResponseBody
    @RepeatSubmit(interval = 5000, message = "请求过于频繁")
    public synchronized AjaxResult register(@RequestBody @Validated RegisterDTO registerDTO) {
        SysUser sysUser = ShiroUtils.getSysUser();
        Map map = initService.register(registerDTO,sysUser,queueList);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("msg", "二次登录令牌已失效,请重新扫码登录");
        if (map != null) {
            stop();
            queueList.put(sysUser.getUserId(), map);
            ajax.put("msg", "已执行登录请求,请自行查询请求状态接口");
        }
        return ajax;
    }


    @ApiOperation("停止本账号的线程,以便于重新获取登录二维码登录.(此请求仅停止线程,不执行已登录qq下线操作)")
    @GetMapping("/stop")
    @ResponseBody
    public AjaxResult stop() {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("msg", "未查询到本账号登录数据");
        if (map != null) {
            MySocket mySocket = (MySocket) map.get("mySocket");
            mySocket.socketEnd();
            queueList.remove(id);
            ajax.put("msg", "操作成功");
        }
        return ajax;
    }

    @ApiOperation("查询本账号已扫码qq登录状态")
    @GetMapping("/status")
    @ResponseBody
    public AjaxResult status() {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        if (map != null) {
            UnPackLogin unPackLogin = (UnPackLogin) map.get("unPackLogin");
            byte[] loginState = unPackLogin.getLoginState();
            if (loginState != null) {
                if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                    ajax.put("msg", "正常登录");
                } else {
                    ajax.put("msg", "其他状态: " + ByteToAll.byteToHxe(unPackLogin.getLoginState()));
                }
            }
        } else {
            ajax.put("msg", "未查询到扫码信息请扫码登录后查询");
        }
        return ajax;
    }

    @ApiOperation("刷新登录二维码")
    @GetMapping("/refresh")
    @ResponseBody
    @RepeatSubmit(interval = 5000, message = "请求过于频繁")
    public synchronized AjaxResult refresh() {
        stop();
        return getQr();
    }

    @ApiOperation("获取q群消息-编程式处理(开启消息处理线程后无效)")
    @GetMapping("/msg/group/get")
    @ResponseBody
    @RepeatSubmit(interval = 5000, message = "请求过于频繁")
    public synchronized AjaxResult msgGroupGet() {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        if (map != null) {
            UnPackLogin unPackLogin = (UnPackLogin) map.get("unPackLogin");
            byte[] loginState = unPackLogin.getLoginState();
            if (loginState != null) {
                if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                    Boolean isOk = (Boolean) map.get("msgThreadStart");
                    if (isOk != null && isOk == true) {
                        ajax.put("msg", "获取q群消息失败(请关闭qq消息处理线程后使用)");
                    }else {
                        ajax.put("msg", "获取q群消息成功");
                        JSONObject jsonObject = initService.msgGroupGet(sysUser, queueList);
                        ajax.put("data", jsonObject);
                    }
                } else {
                    ajax.put("msg", "其他状态: " + ByteToAll.byteToHxe(unPackLogin.getLoginState()));
                }
            }
        } else {
            ajax.put("msg", "未查询到扫码信息请扫码登录后操作");
        }
        return ajax;
    }

    @ApiOperation("获取自己消息-编程式处理(开启消息处理线程后无效)")
    @GetMapping("/msg/my/get")
    @ResponseBody
    @RepeatSubmit(interval = 5000, message = "请求过于频繁")
    public synchronized AjaxResult msgMyGet() {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        if (map != null) {
            UnPackLogin unPackLogin = (UnPackLogin) map.get("unPackLogin");
            byte[] loginState = unPackLogin.getLoginState();
            if (loginState != null) {
                if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                    Boolean isOk = (Boolean) map.get("msgThreadStart");
                    if (isOk != null && isOk == true) {
                        ajax.put("msg", "获取自己消息失败(请关闭qq消息处理线程后使用)");
                    }else {
                        ajax.put("msg", "获取自己消息成功");
                        JSONObject jsonObject = initService.msgMyGet(sysUser, queueList);
                        ajax.put("data", jsonObject);
                    }
                } else {
                    ajax.put("msg", "其他状态: " + ByteToAll.byteToHxe(unPackLogin.getLoginState()));
                }
            }
        } else {
            ajax.put("msg", "未查询到扫码信息请扫码登录后操作");
        }
        return ajax;
    }

    @ApiOperation("获取好友消息-编程式处理(开启消息处理线程后无效)")
    @GetMapping("/msg/user/get")
    @ResponseBody
    @RepeatSubmit(interval = 5000, message = "请求过于频繁")
    public synchronized AjaxResult msgUserGet() {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        if (map != null) {
            UnPackLogin unPackLogin = (UnPackLogin) map.get("unPackLogin");
            byte[] loginState = unPackLogin.getLoginState();
            if (loginState != null) {
                if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                    Boolean isOk = (Boolean) map.get("msgThreadStart");
                    if (isOk != null && isOk == true) {
                        ajax.put("msg", "获取好友消息失败(请关闭qq消息处理线程后使用)");
                    }else {
                        ajax.put("msg", "获取好友消息成功");
                        JSONObject jsonObject = initService.msgUserGet(sysUser, queueList);
                        ajax.put("data", jsonObject);
                    }
                } else {
                    ajax.put("msg", "其他状态: " + ByteToAll.byteToHxe(unPackLogin.getLoginState()));
                }
            }
        } else {
            ajax.put("msg", "未查询到扫码信息请扫码登录后操作");
        }
        return ajax;
    }

    @ApiOperation("获取在线推消息-编程式处理(开启消息处理线程后无效)")
    @GetMapping("/msg/push/get")
    @ResponseBody
    @RepeatSubmit(interval = 5000, message = "请求过于频繁")
    public synchronized AjaxResult msgPushGet() {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        if (map != null) {
            UnPackLogin unPackLogin = (UnPackLogin) map.get("unPackLogin");
            byte[] loginState = unPackLogin.getLoginState();
            if (loginState != null) {
                if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                    Boolean isOk = (Boolean) map.get("msgThreadStart");
                    if (isOk != null && isOk == true) {
                        ajax.put("msg", "获取在线推消息失败(请关闭qq消息处理线程后使用)");
                    }else {
                        ajax.put("msg", "获取在线推消息成功");
                        JSONObject jsonObject = initService.msgPushGet(sysUser, queueList);
                        ajax.put("data", jsonObject);
                    }
                } else {
                    ajax.put("msg", "其他状态: " + ByteToAll.byteToHxe(unPackLogin.getLoginState()));
                }
            }
        } else {
            ajax.put("msg", "未查询到扫码信息请扫码登录后操作");
        }
        return ajax;
    }



    @ApiOperation("开启qq消息处理线程")
    @GetMapping("/msg/thread/start")
    @ResponseBody
    @RepeatSubmit(interval = 5000, message = "请求过于频繁")
    public synchronized AjaxResult msgThreadStart() {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        if (map != null) {
            UnPackLogin unPackLogin = (UnPackLogin) map.get("unPackLogin");
            byte[] loginState = unPackLogin.getLoginState();
            if (loginState != null) {
                if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                    map.put("msgThreadStart", true);
                    initService.msgThreadStart(sysUser,queueList);
                    ajax.put("msg", "开启qq消息处理线程开启成功");
                } else {
                    ajax.put("msg", "其他状态,未开启消息处理线程: " + ByteToAll.byteToHxe(unPackLogin.getLoginState()));
                }
            }
        } else {
            ajax.put("msg", "未查询到扫码信息请扫码登录后操作");
        }
        return ajax;
    }

    @ApiOperation("关闭qq消息处理线程")
    @GetMapping("/msg/thread/stop")
    @ResponseBody
    @RepeatSubmit(interval = 5000, message = "请求过于频繁")
    public synchronized AjaxResult msgThreadStop() {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        if (map != null) {
            map.put("msgThreadStart", false);
            ajax.put("msg", "关闭qq消息处理线程关闭成功");
        } else {
            ajax.put("msg", "未查询到扫码信息请扫码登录后操作");
        }
        return ajax;
    }

    @ApiOperation("设置机器人触发关键词回复配置")
    @PostMapping("/keyword/commit")
    @ResponseBody
    @RepeatSubmit(interval = 5000, message = "请求过于频繁")
    public synchronized AjaxResult keywordCommit(@RequestBody @Validated KeywordListDTO keywordListDTO) {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        if (map != null) {
            UnPackLogin unPackLogin = (UnPackLogin) map.get("unPackLogin");
            byte[] loginState = unPackLogin.getLoginState();
            if (loginState != null) {
                if (LoginState.LOGIN_STATE_PASS.equals(ByteToAll.byteToHxe(unPackLogin.getLoginState()))) {//正常登录
                    initService.keywordCommit(sysUser,queueList,keywordListDTO);
                    ajax.put("msg", "设置机器人触发关键词回复配置成功");
                } else {
                    ajax.put("msg", "其他状态: " + ByteToAll.byteToHxe(unPackLogin.getLoginState()));
                }
            }
        } else {
            ajax.put("msg", "未查询到扫码信息请扫码登录后操作");
        }
        return ajax;
    }

    @ApiOperation("发送好友消息")
    @PostMapping("/friend/send")
    @ResponseBody
    public AjaxResult friendSend(@RequestBody @Validated FriendSendDTO friendSendDTO) {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("msg", "未查询到本账号登录数据,发送失败");
        if (map != null) {
            sendService.userMsgText(map,friendSendDTO.getUserGo(),friendSendDTO.getMsg());
            ajax.put("msg", "操作成功");
        }
        return ajax;
    }

    @ApiOperation("发送群消息")
    @PostMapping("/group/send")
    @ResponseBody
    public AjaxResult groupSend(@RequestBody @Validated GroupSendDTO groupSendDTO) {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        Map map = queueList.get(id);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("msg", "未查询到本账号登录数据,发送失败");
        if (map != null) {
            sendService.groupMsgText(map,groupSendDTO.getGroup(),groupSendDTO.getMsg());
            ajax.put("msg", "操作成功");
        }
        return ajax;
    }


    @GetMapping("/qr")
    public String toQr(HttpServletRequest request, ModelMap mmap) {
        String url = request.getParameter("url");
        if (url != null) {
            try {
                byte[] qrCodeImage = ImageUtil.generateQRCodeImage(URLDecoder.decode(url, StandardCharsets.UTF_8));
                String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
                mmap.put("qr", base64Image);
            } catch (Exception e) {
                mmap.put("qr", "");
            }
        }else {
            mmap.put("qr", "");
        }
        return "qq/qr";
    }





}
