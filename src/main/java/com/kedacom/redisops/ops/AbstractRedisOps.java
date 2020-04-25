/**  
* Title: AbstractRedisOps.java
* Description:  redis操作的抽象类，T为redis中key对应value的数据类型
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月9日 下午6:47:13  
* @version 1.0  
*/
package com.kedacom.redisops.ops;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.kedacom.redisops.ops.config.CacheConfiguration;
import com.kedacom.redisops.ops.config.CacheConsts;
import com.kedacom.redisops.ops.lock.RedisLock;
import com.kedacom.redisops.ops.lock.ThreadAwaitContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
@EnableCaching
public abstract class AbstractRedisOps<T> {

	@Resource
	protected CacheManager cm; // 由应用配置后，自动注入

	@Resource
	protected ThreadAwaitContainer threadAwait; // 线程等待、唤醒对象

	protected String keyPrefix; // redis中的key前缀，也作为内存缓存的名称。在子类中初始化

	protected boolean enableMemoryCached = true; // 是否在内存中缓存,默认为true，可在子类中初始化

	protected boolean enableDbLoad = false; // 是否从数据库中加载，,默认为false，可在子类中初始化

	protected int retryTimes = CacheConsts.LOAD_FROM_DB_RETRY_TIMES; // 从redis中获取不到时，读数据库的重试次数,可在子类中修改

	protected final StringRedisTemplate srt; // 构造函数中注入

	protected AbstractRedisOps(StringRedisTemplate rt) {
		srt = rt;
		// 允许开启事务
		srt.setEnableTransactionSupport(true);
	}

	/**
	 * 从内存，redis,DB中依次获取实体
	 * 
	 * @param key
	 * @return value
	 */
	public T get(String key) {
		T value = null;

		// 1.如果在内存缓存中，则获取内存缓存
		if (enableMemoryCached) {
			value = getFromCacheManager(key);
			if (null != value) {
				log.debug("***get from memory***{}->{}", getRedisKey(key), value);
				return value;
			}
		}
		// 2.从redis中获取,对于键不存在的hash,redis返回empty map
		value = getFromRedis(key);
		if (null != value) {
			// 从redis中获取并回写到内存
			log.debug("***get from redis***{}->{}", getRedisKey(key), value);
			putToCacheManager(key, value);
		} else {
			if (enableDbLoad) {
				// 3.从数据库中获取并回写到redis和内存,可能存在竞争，重试一定次数
				RedisLock redisLock = new RedisLock(srt, getRedisKey(key) + "_sync_lock");
				int retryCount = 0;
				do {
					try {
						// 如果获取到分布式锁，则从数据库中获取，取完后返回
						if (redisLock.lock()) {
							value = loadValueFromDb(key);
							log.debug("***get from db***{}->{}", getRedisKey(key), value);
							if (null != value) {
								set(key, value); // 回写入redis与内存
							}
							threadAwait.signalAll(getRedisKey(key)); // 唤醒所有等待该key的线程
							return value;
						}
						// 如果未获取到分布式锁，说明别的进程正在进行查询数据库，等待一段时间后查询redis
						threadAwait.await(getRedisKey(key), CacheConsts.LOAD_FROM_DB_WAIT_TIME);
						value = getFromRedis(key);
						if (null != value) {
							if (enableMemoryCached) {
								putToCacheManager(key, value);
							}
							return value;
						}
					} catch (Exception e) {
						log.error("get" + getRedisKey(key) + "fail!", e);
					} finally {
						redisLock.unlock();
					}
					retryCount++;
				} while (retryCount < this.retryTimes);
			}
		}
		return value;
	}

	/**
	 * 先写入redis,再写入内存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void set(String key, T value) {
		putToRedis(key, value);
		log.debug("***put to redis***{}->{}", getRedisKey(key), value);
		if (enableMemoryCached) {
			// 如果需要写入内存
			putToCacheManager(key, value);
		}
		pushKeyModified(key);
	}

	/**
	 * 从redis与缓存中删除key为参数的值
	 * 
	 * @param key
	 */
	public void del(String key) {
		deleteFromRedis(key);
		if (enableMemoryCached) {
			delFromCacheManager(key);
		}
		pushKeyModified(key);
	}

	/**
	 * 从redis中获取value,若取不到应返回null
	 * 
	 * @param key
	 * @return
	 */
	protected abstract T getFromRedis(String key);

	/**
	 * 将value的值设置入redis中，value不应为null
	 * 
	 * @param key
	 * @param value
	 */
	protected abstract void putToRedis(String key, T value);

	/**
	 * 判断redis中是否存在该key
	 * 
	 * @param key
	 * @return
	 */
	protected boolean containsRedisKey(String key) {
		return srt.hasKey(getRedisKey(key));
	}

	/**
	 * 从redis中批量删除key
	 * 
	 * @param keyCollection
	 */
	protected void deleteFromRedis(Collection<String> keyCollection) {
		Stream<String> keyStream = keyCollection.stream();
		Set<String> redisKeySet = keyStream.map(key -> getRedisKey(key)).collect(Collectors.toSet());
		srt.delete(redisKeySet);
	}

	/**
	 * 从redis中删除key
	 * 
	 * @param key
	 */
	protected void deleteFromRedis(String key) {
		srt.delete(getRedisKey(key));
	}

	/**
	 * 从一级缓存中获取key对应的value
	 * 
	 * @param cacheName
	 * @param key
	 * @return
	 */
	protected T getFromCacheManager(String key) {
		T value = null;
		Cache cache = cm.getCache(keyPrefix);
		if (null != cache.get(getRedisKey(key))) {
			value = (T) cache.get(getRedisKey(key)).get();
		}
		return value;
	}

	/**
	 * 将key对应的value写入一级缓存中
	 * 
	 * @param cacheName
	 * @param key
	 * @param value
	 * @return
	 */
	protected boolean putToCacheManager(String key, T value) {
		Cache cache = cm.getCache(keyPrefix);
		if (null != cache) {
			cache.put(getRedisKey(key), value);
			return true;
		}
		return false;
	}

	/**
	 * 在一级缓存中删除对应的key
	 * 
	 * @param cacheName
	 * @param key
	 */
	protected void delFromCacheManager(String key) {
		Cache cache = cm.getCache(keyPrefix);
		cache.evict(getRedisKey(key));
	}

	/**
	 * 在value对象中修改指定field的值
	 * 
	 * @param value
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	protected boolean setEntityField(T value, String fieldName, Object fieldValue) {
		try {
			Field field = value.getClass().getDeclaredField(fieldName);
			if (null != field) {
				field.setAccessible(true);
				field.set(value, fieldValue);
				return true;
			}
		} catch (Exception e) {
			log.info("modify entity field fail:{}", fieldName, e);
		}
		return false;
	}

	/**
	 * 从数据库中获取value,若取不到应返回null,可被子类重写
	 * 
	 * @param key
	 * @return
	 */
	protected T loadValueFromDb(String key) {
		return null;
	}

	/**
	 * 向redis的同步channel发送发生变化的key,发布消息的形式为：{redisPublishId}${keyPrefix}:{key}
	 * 
	 * @param key
	 */
	protected void pushKeyModified(String key) {
		StringBuilder sb = new StringBuilder(CacheConfiguration.redisPublisherId);
		sb.append(CacheConsts.REDIS_PUBLISHER_ID_SEPARATE).append(getRedisKey(key));
		srt.convertAndSend(CacheConsts.REDIS_CACHE_MODIFY_TOPIC, sb.toString());
	}

	/**
	 * 返回redis中定义的key
	 * 
	 * @param key
	 * @return
	 */
	protected String getRedisKey(String key) {
		return keyPrefix + CacheConsts.KEYPREFIX_SEPARATE + key;
	}

}
