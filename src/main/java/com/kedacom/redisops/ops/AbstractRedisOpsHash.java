/**  
* Title: RedisHashOps.java
* Description: 对redis的hash结构操作抽象类 
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月9日 上午10:49:08  
* @version 1.0  
*/
package com.kedacom.redisops.ops;

import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
public abstract class AbstractRedisOpsHash<T> extends AbstractRedisOps<T> {

	private HashOperations<String, String, Object> hops;

	protected AbstractRedisOpsHash(StringRedisTemplate srt) {
		super(srt);
		hops = srt.opsForHash();
	}

	@Override
	protected T getFromRedis(String key) {
		T entity = null;
		Map<String, Object> map = hops.entries(getRedisKey(key));
		if ((null != map) && (!map.isEmpty())) {
			entity = convertMap2T(map);
		}
		return entity;
	}

	@Override
	protected void putToRedis(String key, T value) {
		Map<String, Object> map = convertT2Map(value);
		hops.putAll(getRedisKey(key), map);
	}

	/**
	 * 设置hash中某个域的值
	 * 
	 * @param key
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public boolean setField(String key, String fieldName, String fieldValue) {
		hops.put(getRedisKey(key), fieldName, fieldValue);
		if (enableMemoryCached) {
			T value = getFromCacheManager(key);
			if (null != value) {
				// 从cachemanager中取对象,修改对象属性
				return setEntityField(value, fieldName, fieldValue);
			} else {
				// 从redis中取，并回写到cache中
				value = getFromRedis(key);
				return putToCacheManager(key, value);
			}
		}
		pushKeyModified(key);
		return true;
	}

	/**
	 * 将Map对象转换为实体类型
	 * 
	 * @param map
	 * @return
	 */
	abstract protected T convertMap2T(Map<String, Object> map);

	/**
	 * 将实体类型转换为Map
	 * 
	 * @param value
	 * @return
	 */
	abstract protected Map<String, Object> convertT2Map(T value);

}
