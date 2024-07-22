package com.ruoyi.common.utils;

import com.ruoyi.common.constant.QueueUserInfoConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具
 */
@Service
public class RedisUtil {

    public static Integer expireIn = 36000;
    public static Integer dayExpireIn = 604800;//默认7天
    public static int yearExpireIn = 36000 * 24 * 365;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @param key
     * @param strJson
     */
    public void set(String key, String strJson) {
        redisTemplate.opsForValue().set(key, strJson);
    }

    public void set(String key, String strJson, Integer expired) {
        redisTemplate.opsForValue().set(key, strJson, expired, TimeUnit.SECONDS);
    }

    public void updateCache(String key, String value) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        // 获取原来的过期时间
        Long expireTime = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        // 设置新的值
        ops.set(key, value);
        // 重新设置过期时间
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean isExists(String key) {
        String ret = redisTemplate.opsForValue().get(key);
        if (ret != null) {
            return true;
        }
        return false;
    }

    public void rightPushAll(String key,List lst){
        //数据缓存到redis中
        redisTemplate.opsForList().rightPushAll(key, lst);
        //设置当前key过期时间为1个小时
        redisTemplate.expire(key,1000*60*60, TimeUnit.MILLISECONDS);
    }


    public List getList(String key,Integer start,Integer end){
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 列表总记录数
     *
     * @param key
     *
     * @return
     */
    public Integer getListSize(String key){
        List lst = redisTemplate.opsForList().range(key, 0, -1);
        if (!CollectionUtils.isEmpty(lst)){
            return lst.size();
        }
        return 0;
    }

    /**
     * 增量
     *
     * @param key
     * @param num
     * @return
     */
    public Long increment(String key,Integer num){
        if (redisTemplate.opsForValue().get(key)!=null){
            return redisTemplate.opsForValue().increment(key, num);
        }else {
            return redisTemplate.boundValueOps(key).increment(num);
        }
    }

    /**
     * redis设置续期时间
     *
     * @param key
     * @param expireIn
     */
    public void setExpireIn(String key, Duration expireIn){
        redisTemplate.expire( key, expireIn);
    }

    public ZSetOperations<String, String> zSet(){
        return redisTemplate.opsForZSet();
    }

    /**
     *  模糊查询出所有合适的 keys
     *
     * @param keyPrefix
     * @return
     */
    public Set<String> keySet(String keyPrefix){
        Set<String> keySet = redisTemplate.keys(keyPrefix+"*");
        return keySet;
    }

    /**
     * 生成模糊匹配的对应的set集合,模糊匹配,例: cacheKey="指定前缀" + "*"
     * @param cacheKey
     * @return
     */
    public Set<String> keys(String cacheKey){
        return redisTemplate.keys(cacheKey);
    }

    public boolean delete(String cacheKey) {
        return redisTemplate.delete(cacheKey);
    }

    /**
     * 批量删除
     *
     * @param keys
     * @return
     */
    public Long delete(Set<String> keys) {
        return redisTemplate.delete(keys);
    }


    /**
     * 将元素添加到队列中
     * @param queueName 队列名称
     * @param jsonStr 要添加的元素
     * @param enqueueMax 队列允许的最大长度，超出部分会被删除
     * @param expireIn 元素的过期时间（以秒为单位），0表示不设置过期时间
     */
    public Long enqueue(String queueName, String jsonStr) {
        // 获取 Redis 列表操作对象
        ListOperations listOps = redisTemplate.opsForList();

        // 如果队列长度已经超过了最大长度，删除列表左侧的元素
        while (listOps.size(queueName) >= QueueUserInfoConstant.enqueueMax) {
            listOps.leftPop(queueName);
        }

        // 向列表右侧插入新元素
        long num = listOps.rightPush(queueName, jsonStr);

        // 如果需要设置过期时间，为新元素设置过期时间
        if (QueueUserInfoConstant.expireIn > 0) {
            redisTemplate.expire(queueName, QueueUserInfoConstant.expireIn, TimeUnit.SECONDS);
        }
        return num;
    }

    /**
     * 从队列中取出元素
     * @param queueName 队列名称
     * @return 队列中的元素
     */
    public String dequeue(String queueName) {
        // 获取 BoundListOperations 对象
        BoundListOperations listOps = redisTemplate.boundListOps(queueName);

        // 使用 blpop 命令阻塞式弹出队列左侧的元素
        Object object = listOps.leftPop(1, TimeUnit.SECONDS);

        if (object == null) {
            return null;
        }
        // 出队操作，从列表左侧弹出元素
        return object.toString();
    }



}
