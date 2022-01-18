package com.allenyll.sw.core.cache.util;

import com.alibaba.fastjson.JSON;
import com.allenyll.sw.common.util.JsonUtil;
import com.allenyll.sw.core.cache.domain.RedisDo;
import com.allenyll.sw.core.cache.interfaces.ICache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description:  缓存工具类
 * @Author:       allenyll
 * @Date:         2019/2/27 10:52 PM
 * @Version:      1.0
 */
@SuppressWarnings("ALL")
@Service("cacheUtil")
public class CacheUtil implements ICache {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisDo redisDo;

    private Jedis jedis;

    /**
     * 获取操作字符串的ValueOperations
     */
    public ValueOperations<String, Object> getValueOperations(){
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        return vo;
    }

    @Override
    public boolean set(final String key, final String value) {
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            connection.set(serializer.serialize(key), serializer.serialize(value));
            return true;
        });
    }

    @Override
    public String get(final String key) {
        String result = redisTemplate.execute((RedisCallback<String>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(serializer.serialize(key));
            return serializer.deserialize(value);
        });
        return result;
    }

    @Override
    public boolean expire(String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public boolean remove(String key) {
        boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            connection.del(key.getBytes());
            return true;
        });
        return result;
    }

    @Override
    public <T> boolean setList(String key, List<T> list) {
        String value = JSON.toJSONString(list);
        return set(key, value);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        String json = get(key);
        if(json != null){
            List<T> list = JSON.parseArray(json, clazz);
            return list;
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getAllCache() {
        redisDo.open();
        jedis = redisDo.jedis;
        // 获取所有key
        Set<String> keySet = jedis.keys("*");
        Iterator it = keySet.iterator();
        List<Map<String, Object>> list = new ArrayList<>();

        while (it.hasNext()){
            Map<String, Object> map = new HashMap<>();
            String key = (String) it.next();
            if(key != null && key != ""){
                map.put("key", key);
                map.put("value", jedis.get(key));
                list.add(map);
            }
        }
        redisDo.close();
        return list;
    }

    @Override
    public void removeAll() {
        redisDo.open();
        jedis = redisDo.jedis;
        // 获取所有key
        Set<String> keySet = jedis.keys("*");
        redisTemplate.delete(keySet);
        redisDo.close();
    }

    /**
     * 将 key，value 存放到redis数据库中，默认设置过期时间为一周
     *
     * @param key
     * @param value
     */
    @Override
    public void set(String key, Object value) {
        set(key, value, 1000);
    }

    /**
     * 将 key，value 存放到redis数据库中，设置过期时间单位是秒
     *
     * @param key
     * @param value
     * @param expireTime
     */
    public void set(String key, Object value, long expireTime) {
        getValueOperations().set(key, JsonUtil.convertObj2String(value), expireTime, TimeUnit.SECONDS);
    }

    /**
     * 将 key，value 存放到redis数据库中，设置过期时间单位是秒
     *
     * @param key
     * @param value
     * @param expireTime
     */
//    public void set(String key, String value, long expireTime) {
//        getValueOperations().set(key, value, expireTime, TimeUnit.SECONDS);
//    }

    @Override
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取与 key 对应的对象
     * @param key
     * @param clazz 目标对象类型
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        String s = getKey(key);
        if (s == null) {
            return null;
        }
        return JsonUtil.convertString2Obj(s, clazz);
    }

    /**
     * 获取 key 对应的字符串
     * @param key
     * @return
     */
    public String getKey(String key) {
        return (String) getValueOperations().get(key);
    }

    /**
     * 将 key，value 存放到redis数据库中，默认设置过期时间为一周
     *
     * @param key
     * @param value
     */
    public void setBean(String key, Object value, int expireTime) {
        getValueOperations().set(key, JsonUtil.convertObj2String(value), expireTime, TimeUnit.SECONDS);
    }

    public <T> T getBean(String key, Class<T> clazz) {
        String value = (String) getValueByKey(key);
        if (value == null) {
            return null;
        }
        return JsonUtil.convertString2Obj(value, clazz);
    }

    /**
     * 获取 key 对应的字符串
     * @param key
     * @return
     */
    public Object getValueByKey(String key) {
        return getValueOperations().get(key);
    }

    /**
     * 向redis中存入数据
     *
     * @param key 键值
     * @param object 数据
     * @return
     */
    public boolean hset(String key, String fieldName, Object object) {
        redisTemplate.opsForHash().put(key, fieldName, object);
        return true;
    }

    /**
     * 向redis中存入数据
     *
     * @param key 键值
     * @param object 数据
     * @return
     */
    public <T> boolean hset(String key, String fieldName, T object, int seconds) {
        redisTemplate.opsForHash().put(key, fieldName, object);
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        return true;
    }


    /**
     * 向redis中存入数据
     *
     * @param key 键值
     * @param fieldName 数据
     * @return
     */
    public <T> boolean hdel(String key, String fieldName) {
        redisTemplate.opsForHash().delete(key, fieldName);
        return true;
    }

    /**
     * 获取数据
     *
     * @param key
     * @return
     */
    public <T> T hget(String key, String fieldName, Class<T> clazz) {
        Object value = redisTemplate.opsForHash().get(key, fieldName);
        if (value != null) {
            return JsonUtil.convertString2Obj(value.toString(), clazz);
        }
        return null;
    }

    /**
     * 获取hash的key集合
     * @param key
     * @return
     */
    public Set<Object> hkeys(String key, String hKey) {
        Set<Object> set = new HashSet<>();
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key,
                ScanOptions.scanOptions().match("*"+hKey+"*").count(1).build());

        while(cursor.hasNext()){
            Map.Entry<Object, Object> next = cursor.next();
            Object _key = next.getKey();
            set.add(_key);
        }
        try {
            cursor.close();
        }catch (Exception e){
        }
        return set;
    }


    /**
     * redis自减
     * @param key
     * @return
     */
    public Long decr(String key) {
        return getValueOperations().increment(key, -1);
    }

    /**
     * redis 自增
     * @param key
     */
    public void incr(String key) {
        getValueOperations().increment(key, 1);
    }

    /**
     * 根据keyPattern获取所有的key
     * @param keyPattern
     * @return
     */
    public Set<String> keys(String keyPattern) {
        return redisTemplate.keys(keyPattern);
    }

}
