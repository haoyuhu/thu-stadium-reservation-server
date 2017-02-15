package com.huhaoyu.thu.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by huhaoyu
 * Created On 2017/1/25 下午5:21.
 */

@Component
public class RedisUtil {

    private final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Autowired
    private RedisTemplate<String, Object> template;

    public boolean exists(final String key) {
        return template.hasKey(key);
    }

    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    public void removePattern(final String pattern) {
        Set<String> keys = template.keys(pattern);
        if (!keys.isEmpty()) {
            template.delete(keys);
        }
    }

    public void remove(final String key) {
        if (exists(key)) {
            template.delete(key);
        }
    }

    public Object get(final String key) {
        ValueOperations<String, Object> operations = template.opsForValue();
        return operations.get(key);
    }

    public boolean set(final String key, Object value) {
        boolean ret = false;
        try {
            ValueOperations<String, Object> operations = template.opsForValue();
            operations.set(key, value);
            ret = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ret;
    }

    public boolean set(final String key, Object value, Long expireTime) {
        boolean ret = false;
        try {
            ValueOperations<String, Object> operations = template.opsForValue();
            operations.set(key,value);
            template.expire(key, expireTime, TimeUnit.SECONDS);
            ret = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ret;
    }

    public Object getHash(final String key, final String secondaryKey) {
        HashOperations<String, String, Object> operations = template.opsForHash();
        return operations.get(key, secondaryKey);
    }

    public List<Object> getHashes(final String key, final Collection<String> secondaryKeys) {
        HashOperations<String, String, Object> operations = template.opsForHash();
        return operations.multiGet(key, secondaryKeys);
    }

    public boolean setHash(final String key, final String secondaryKey, Object value) {
        boolean ret = false;
        try {
            HashOperations<String, String, Object> operations = template.opsForHash();
            operations.put(key, secondaryKey, value);
            ret = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ret;
    }

    public boolean setHashes(final String key, Map<String, Object> map) {
        boolean ret = false;
        try {
            HashOperations<String, String, Object> operations = template.opsForHash();
            operations.putAll(key, map);
            ret = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ret;
    }

    public void expire(final String key, final long expireTime) {
        template.expire(key, expireTime, TimeUnit.SECONDS);
    }

}
