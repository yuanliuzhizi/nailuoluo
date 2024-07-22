package com.ruoyi.common.constant;


/**
 *
 *
 * @author UOHE
 */
public interface QueueUserInfoConstant {

    /**
     * 续期（单位是秒）,默认现在时间加1小时,1000*60*60
     */
    static final Integer expireIn = 3600;

    /**
     * 续期（单位是秒）,默认现在时间加1小时,1000*60*60
     */
    static final Integer expireIn24 = 1000 * 60 * 60 * 24 * 24;

    /**
     * 队列大小
     */
    static final Integer enqueueMax = 1000;

    //用户消息
    public static final String QQ_USER_MSG = "qq:user:msg:";

    //本人消息
    public static final String QQ_MY_MSG = "qq:my:msg:";

    //群消息
    public static final String QQ_GROUP_MSG = "qq:group:msg:";

    //推消息 撤回 修改名称等
    public static final String QQ_PUSH_MSG = "qq:push:msg:";

    //群配置json
    public static final String QQ_GROUP_KEYWORD = "qq:group:keyword:";

}
