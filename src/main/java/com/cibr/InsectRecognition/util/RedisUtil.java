package com.cibr.InsectRecognition.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public void set(String key, Object value) {
        //更改在redis里面查看key编码问题
        ValueOperations<String,Object> vo = redisTemplate.opsForValue();
        vo.set(key, value,1, TimeUnit.HOURS);
        redisTemplate.expire(key,1,TimeUnit.HOURS);
    }

    /**
     * @param key
     * @param value
     * @param timeout 单位小时
     */
    public void set(String key, Object value,long timeout) {
        //更改在redis里面查看key编码问题
        ValueOperations<String,Object> vo = redisTemplate.opsForValue();
        vo.set(key, value,timeout, TimeUnit.HOURS);
    }

    public Object get(String key) {
        ValueOperations<String,Object> vo = redisTemplate.opsForValue();
        return vo.get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
}
