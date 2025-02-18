package com.blog.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {


    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // =============================common============================

    /**
     * 设置缓存的过期时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     * @return 设置成功返回 true，失败返回 false
     */
    public boolean setExpirationTime(String key, long time, TimeUnit timeUnit) {
        if (time <= 0 || key == null) {
            return false;
        }
        try {
            return redisTemplate.expire(key, time, timeUnit);
        } catch (Exception e) {
            logError("设置缓存失效时间失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 设置缓存的过期时间，默认时间单位为秒
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 设置成功返回 true，失败返回 false
     */
    public boolean setExpirationTime(String key, long time) {
        return setExpirationTime(key, time, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存的过期时间
     *
     * @param key      键，不能为 null
     * @param timeUnit 时间单位
     * @return 时间，返回 0 代表为永久有效
     */
    public long getCacheExpirationTime(String key, TimeUnit timeUnit) {
        if (key == null) {
            return 0;
        }
        try {
            return redisTemplate.getExpire(key, timeUnit);
        } catch (Exception e) {
            logError("获取缓存过期时间失败，key: " + key, e);
            return 0;
        }
    }

    /**
     * 获取缓存的过期时间，默认时间单位为秒
     *
     * @param key 键，不能为 null
     * @return 时间(秒)，返回 0 代表为永久有效
     */
    public long getCacheExpirationTime(String key) {
        return getCacheExpirationTime(key, TimeUnit.SECONDS);
    }

    /**
     * 判断缓存键是否存在
     *
     * @param key 键
     * @return 存在返回 true，不存在返回 false
     */
    public boolean isCacheKeyExists(String key) {
        if (key == null) {
            return false;
        }
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logError("判断缓存 key 是否存在失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个或多个键
     */
    public void deleteCacheKeys(String... keys) {
        if (keys == null || keys.length == 0) {
            return;
        }
        try {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(Arrays.asList(keys));
            }
        } catch (Exception e) {
            logError("删除缓存失败，keys: " + Arrays.toString(keys), e);
        }
    }

    // ============================String=============================

    /**
     * 获取普通缓存的值
     *
     * @param key 键
     * @return 值
     */
    public Object getStringCacheValue(String key) {
        if (key == null) {
            return null;
        }
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logError("获取普通缓存失败，key: " + key, e);
            return null;
        }
    }

    /**
     * 设置普通缓存的值
     *
     * @param key   键
     * @param value 值
     * @return 放入成功返回 true，失败返回 false
     */
    public boolean setStringCacheValue(String key, Object value) {
        if (key == null || value == null) {
            return false;
        }
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logError("放入普通缓存失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 设置普通缓存的值并设置过期时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return 放入成功返回 true，失败返回 false
     */
    public boolean setStringCacheValueWithExpiration(String key, Object value, long time, TimeUnit timeUnit) {
        if (key == null || value == null || time <= 0) {
            return false;
        }
        try {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
            return true;
        } catch (Exception e) {
            logError("放入普通缓存并设置时间失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 设置普通缓存的值并设置过期时间，默认时间单位为秒
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)，time 要大于 0，如果 time 小于等于 0 将设置无限期
     * @return 放入成功返回 true，失败返回 false
     */
    public boolean setStringCacheValueWithExpiration(String key, Object value, long time) {
        if (time > 0) {
            return setStringCacheValueWithExpiration(key, value, time, TimeUnit.SECONDS);
        } else {
            return setStringCacheValue(key, value);
        }
    }

    /**
     * 对普通缓存的值进行递增操作
     *
     * @param key   键
     * @param delta 要增加的值(大于 0)
     * @return 递增后的值
     */
    public long incrementStringCacheValue(String key, long delta) {
        if (key == null || delta <= 0) {
            throw new IllegalArgumentException("键不能为空，递增因子必须大于 0");
        }
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            logError("递增缓存值失败，key: " + key, e);
            return 0;
        }
    }

    /**
     * 对普通缓存的值进行递减操作
     *
     * @param key   键
     * @param delta 要减少的值(大于 0)
     * @return 递减后的值
     */
    public long decrementStringCacheValue(String key, long delta) {
        if (key == null || delta <= 0) {
            throw new IllegalArgumentException("键不能为空，递减因子必须大于 0");
        }
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            logError("递减缓存值失败，key: " + key, e);
            return 0;
        }
    }

    // ================================Map=================================

    /**
     * 获取 Hash 缓存中指定项的值
     *
     * @param key  键，不能为 null
     * @param item 项，不能为 null
     * @return 值
     */
    public Object getHashCacheItemValue(String key, String item) {
        if (key == null || item == null) {
            return null;
        }
        try {
            return redisTemplate.opsForHash().get(key, item);
        } catch (Exception e) {
            logError("获取 Hash 缓存值失败，key: " + key + ", item: " + item, e);
            return null;
        }
    }

    /**
     * 获取 Hash 缓存的所有键值对
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> getHashCacheAllEntries(String key) {
        if (key == null) {
            return new HashMap<>();
        }
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            logError("获取 Hash 缓存所有键值失败，key: " + key, e);
            return new HashMap<>();
        }
    }

    /**
     * 设置 Hash 缓存的多个键值对
     *
     * @param key 键
     * @param map 对应多个键值
     * @return 放入成功返回 true，失败返回 false
     */
    public boolean setHashCacheEntries(String key, Map<String, Object> map) {
        if (key == null || map == null || map.isEmpty()) {
            return false;
        }
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            logError("放入 Hash 缓存失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 设置 Hash 缓存的多个键值对并设置过期时间
     *
     * @param key      键
     * @param map      对应多个键值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return 放入成功返回 true，失败返回 false
     */
    public boolean setHashCacheEntriesWithExpiration(String key, Map<String, Object> map, long time, TimeUnit timeUnit) {
        if (key == null || map == null || map.isEmpty() || time <= 0) {
            return false;
        }
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return setExpirationTime(key, time, timeUnit);
        } catch (Exception e) {
            logError("放入 Hash 缓存并设置时间失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 设置 Hash 缓存的多个键值对并设置过期时间，默认时间单位为秒
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return 放入成功返回 true，失败返回 false
     */
    public boolean setHashCacheEntriesWithExpiration(String key, Map<String, Object> map, long time) {
        return setHashCacheEntriesWithExpiration(key, map, time, TimeUnit.SECONDS);
    }

    /**
     * 向 Hash 缓存中放入一个键值对
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return 放入成功返回 true，失败返回 false
     */
    public boolean setHashCacheItem(String key, String item, Object value) {
        if (key == null || item == null || value == null) {
            return false;
        }
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            logError("向 Hash 表中放入数据失败，key: " + key + ", item: " + item, e);
            return false;
        }
    }

    /**
     * 向 Hash 缓存中放入一个键值对并设置过期时间
     *
     * @param key      键
     * @param item     项
     * @param value    值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return 放入成功返回 true，失败返回 false
     */
    public boolean setHashCacheItemWithExpiration(String key, String item, Object value, long time, TimeUnit timeUnit) {
        if (key == null || item == null || value == null || time <= 0) {
            return false;
        }
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return setExpirationTime(key, time, timeUnit);
        } catch (Exception e) {
            logError("向 Hash 表中放入数据并设置时间失败，key: " + key + ", item: " + item, e);
            return false;
        }
    }

    /**
     * 向 Hash 缓存中放入一个键值对并设置过期时间，默认时间单位为秒
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)，注意:如果已存在的 hash 表有时间，这里将会替换原有的时间
     * @return 放入成功返回 true，失败返回 false
     */
    public boolean setHashCacheItemWithExpiration(String key, String item, Object value, long time) {
        return setHashCacheItemWithExpiration(key, item, value, time, TimeUnit.SECONDS);
    }

    /**
     * 删除 Hash 缓存中的指定项
     *
     * @param key  键，不能为 null
     * @param item 项，可以是多个，不能为 null
     */
    public void deleteHashCacheItems(String key, Object... item) {
        if (key == null || item == null || item.length == 0) {
            return;
        }
        try {
            redisTemplate.opsForHash().delete(key, item);
        } catch (Exception e) {
            logError("删除 Hash 表中的值失败，key: " + key, e);
        }
    }

    /**
     * 判断 Hash 缓存中是否存在指定项
     *
     * @param key  键，不能为 null
     * @param item 项，不能为 null
     * @return 存在返回 true，不存在返回 false
     */
    public boolean isHashCacheItemExists(String key, String item) {
        if (key == null || item == null) {
            return false;
        }
        try {
            return redisTemplate.opsForHash().hasKey(key, item);
        } catch (Exception e) {
            logError("判断 Hash 表中是否存在项失败，key: " + key + ", item: " + item, e);
            return false;
        }
    }

    /**
     * 对 Hash 缓存中指定项的值进行递增操作
     *
     * @param key  键
     * @param item 项
     * @param by   要增加的值(大于 0)
     * @return 递增后的值
     */
    public double incrementHashCacheItemValue(String key, String item, double by) {
        if (key == null || item == null || by <= 0) {
            throw new IllegalArgumentException("键、项不能为空，递增因子必须大于 0");
        }
        try {
            return redisTemplate.opsForHash().increment(key, item, by);
        } catch (Exception e) {
            logError("Hash 递增失败，key: " + key + ", item: " + item, e);
            return 0;
        }
    }

    /**
     * 对 Hash 缓存中指定项的值进行递减操作
     *
     * @param key  键
     * @param item 项
     * @param by   要减少的值(大于 0)
     * @return 递减后的值
     */
    public double decrementHashCacheItemValue(String key, String item, double by) {
        if (key == null || item == null || by <= 0) {
            throw new IllegalArgumentException("键、项不能为空，递减因子必须大于 0");
        }
        try {
            return redisTemplate.opsForHash().increment(key, item, -by);
        } catch (Exception e) {
            logError("Hash 递减失败，key: " + key + ", item: " + item, e);
            return 0;
        }
    }

    // ============================set=============================

    /**
     * 获取 Set 缓存中的所有元素
     *
     * @param key 键
     * @return Set 中的所有值
     */
    public Set<Object> getSetCacheMembers(String key) {
        if (key == null) {
            return new HashSet<>();
        }
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logError("获取 Set 缓存所有值失败，key: " + key, e);
            return new HashSet<>();
        }
    }

    /**
     * 判断 Set 缓存中是否存在指定值
     *
     * @param key   键
     * @param value 值
     * @return 存在返回 true，不存在返回 false
     */
    // 存在返回 true，不存在返回 false
    public boolean isSetCacheValueExists(String key, Object value) {
        if (key == null || value == null) {
            return false;
        }
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            logError("判断 Set 缓存中是否存在值失败，key: " + key + ", value: " + value, e);
            return false;
        }
    }

    /**
     * 向 Set 缓存中添加元素
     *
     * @param key    键
     * @param values 值，可以是多个
     * @return 成功添加的元素个数
     */
    public long addToSetCache(String key, Object... values) {
        if (key == null || values == null || values.length == 0) {
            return 0;
        }
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            logError("向 Set 缓存中添加元素失败，key: " + key, e);
            return 0;
        }
    }

    /**
     * 向 Set 缓存中添加元素并设置过期时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     * @param values   值，可以是多个
     * @return 成功添加的元素个数
     */
    public long addToSetCacheWithExpiration(String key, long time, TimeUnit timeUnit, Object... values) {
        if (key == null || values == null || values.length == 0 || time <= 0) {
            return 0;
        }
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            setExpirationTime(key, time, timeUnit);
            return count;
        } catch (Exception e) {
            logError("向 Set 缓存中添加元素并设置过期时间失败，key: " + key, e);
            return 0;
        }
    }

    /**
     * 向 Set 缓存中添加元素并设置过期时间，默认时间单位为秒
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值，可以是多个
     * @return 成功添加的元素个数
     */
    public long addToSetCacheWithExpiration(String key, long time, Object... values) {
        return addToSetCacheWithExpiration(key, time, TimeUnit.SECONDS, values);
    }

    /**
     * 获取 Set 缓存的元素个数
     *
     * @param key 键
     * @return Set 缓存的元素个数
     */
    public long getSetCacheSize(String key) {
        if (key == null) {
            return 0;
        }
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            logError("获取 Set 缓存大小失败，key: " + key, e);
            return 0;
        }
    }

    /**
     * 从 Set 缓存中移除指定元素
     *
     * @param key    键
     * @param values 值，可以是多个
     * @return 成功移除的元素个数
     */
    public long removeFromSetCache(String key, Object... values) {
        if (key == null || values == null || values.length == 0) {
            return 0;
        }
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            logError("从 Set 缓存中移除元素失败，key: " + key, e);
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取 List 缓存中指定范围的元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引，0 到 -1 代表所有值
     * @return List 缓存中指定范围的元素
     */
    public List<Object> getListCacheRange(String key, long start, long end) {
        if (key == null) {
            return new ArrayList<>();
        }
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logError("获取 List 缓存指定范围元素失败，key: " + key, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取 List 缓存的长度
     *
     * @param key 键
     * @return List 缓存的长度
     */
    public long getListCacheLength(String key) {
        if (key == null) {
            return 0;
        }
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            logError("获取 List 缓存长度失败，key: " + key, e);
            return 0;
        }
    }

    /**
     * 获取 List 缓存中指定索引的元素
     *
     * @param key   键
     * @param index 索引，index >= 0 时，0 表头，1 第二个元素，依次类推；index < 0 时，-1 表尾，-2 倒数第二个元素，依次类推
     * @return List 缓存中指定索引的元素
     */
    public Object getListCacheElementByIndex(String key, long index) {
        if (key == null) {
            return null;
        }
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logError("获取 List 缓存指定索引元素失败，key: " + key + ", index: " + index, e);
            return null;
        }
    }

    /**
     * 向 List 缓存的右侧添加一个元素
     *
     * @param key   键
     * @param value 值
     * @return 添加成功返回 true，失败返回 false
     */
    public boolean addToRightOfListCache(String key, Object value) {
        if (key == null || value == null) {
            return false;
        }
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            logError("向 List 缓存右侧添加元素失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 向 List 缓存的右侧添加一个元素并设置过期时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return 添加成功返回 true，失败返回 false
     */
    public boolean addToRightOfListCacheWithExpiration(String key, Object value, long time, TimeUnit timeUnit) {
        if (key == null || value == null || time <= 0) {
            return false;
        }
        try {
            redisTemplate.opsForList().rightPush(key, value);
            setExpirationTime(key, time, timeUnit);
            return true;
        } catch (Exception e) {
            logError("向 List 缓存右侧添加元素并设置过期时间失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 向 List 缓存的右侧添加一个元素并设置过期时间，默认时间单位为秒
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 添加成功返回 true，失败返回 false
     */
    public boolean addToRightOfListCacheWithExpiration(String key, Object value, long time) {
        return addToRightOfListCacheWithExpiration(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 向 List 缓存的右侧添加多个元素
     *
     * @param key   键
     * @param value 值列表
     * @return 添加成功返回 true，失败返回 false
     */
    public boolean addAllToRightOfListCache(String key, List<Object> value) {
        if (key == null || value == null || value.isEmpty()) {
            return false;
        }
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            logError("向 List 缓存右侧添加多个元素失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 向 List 缓存的右侧添加多个元素并设置过期时间
     *
     * @param key      键
     * @param value    值列表
     * @param time     时间
     * @param timeUnit 时间单位
     * @return 添加成功返回 true，失败返回 false
     */
    public boolean addAllToRightOfListCacheWithExpiration(String key, List<Object> value, long time, TimeUnit timeUnit) {
        if (key == null || value == null || value.isEmpty() || time <= 0) {
            return false;
        }
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            setExpirationTime(key, time, timeUnit);
            return true;
        } catch (Exception e) {
            logError("向 List 缓存右侧添加多个元素并设置过期时间失败，key: " + key, e);
            return false;
        }
    }

    /**
     * 向 List 缓存的右侧添加多个元素并设置过期时间，默认时间单位为秒
     *
     * @param key   键
     * @param value 值列表
     * @param time  时间(秒)
     * @return 添加成功返回 true，失败返回 false
     */
    public boolean addAllToRightOfListCacheWithExpiration(String key, List<Object> value, long time) {
        return addAllToRightOfListCacheWithExpiration(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 修改 List 缓存中指定索引的元素
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return 修改成功返回 true，失败返回 false
     */
    public boolean updateListCacheElementByIndex(String key, long index, Object value) {
        if (key == null || value == null) {
            return false;
        }
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            logError("修改 List 缓存指定索引元素失败，key: " + key + ", index: " + index, e);
            return false;
        }
    }

    /**
     * 从 List 缓存中移除指定数量的指定元素
     *
     * @param key   键
     * @param count 移除数量
     * @param value 值
     * @return 成功移除的元素个数
     */
    public long removeFromListCache(String key, long count, Object value) {
        if (key == null || value == null) {
            return 0;
        }
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            logError("从 List 缓存中移除指定元素失败，key: " + key + ", count: " + count + ", value: " + value, e);
            return 0;
        }
    }

    // 简单的错误日志记录方法，可根据实际情况替换为日志框架
    private void logError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }
}