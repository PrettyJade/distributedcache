/**  
* Title: CacheConfig.java
* Description: 向spring中注入相关的对象
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月16日 下午3:42:13  
* @version 1.0  
*/
package com.kedacom.redisops.ops.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.StringUtils;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.kedacom.redisops.ops.listener.RedisMsgListener;
import com.kedacom.redisops.ops.lock.ThreadAwaitContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
@Configuration
public class CacheConfiguration {

	@Resource
	private CacheProperties cacheProperties;

	public static String redisPublisherId;

	/**
	 * 自定义cacheManager,实现动态生成的cache使用缺省的配置
	 * 
	 * @author zhang.kai
	 *
	 */
	public class VlineCacheManager extends SimpleCacheManager {
		@Override
		protected Cache getMissingCache(String cacheName) {
			return createCaffeineCache(cacheName, cacheProperties.getDefaultspec());
		}
	}

	/**
	 * CacheManager对象注入
	 * 
	 * @return
	 */
	@Bean
	public CacheManager getCacheManager() {
		if ((null == cacheProperties) || (null == cacheProperties.getDefaultspec())
				|| (null == cacheProperties.getCachespecs())) {
			log.error("cacheProperties is invalid:{}", cacheProperties);
			return null;
		}
		VlineCacheManager cm = new VlineCacheManager();
		List<Cache> caches = new ArrayList<>();
		cacheProperties.getCachespecs().keySet().forEach(cacheName -> {
			String cacheSpec = cacheProperties.getCachespecs().get(cacheName);
			if (StringUtils.isEmpty(cacheName)) {
				log.error("XXX no cacheSpec for cacheName{}", cacheName);
				return;
			}
			CaffeineCache cache = createCaffeineCache(cacheName, cacheSpec);
			caches.add(cache);
		});
		if (caches.size() > 0) {
			cm.setCaches(caches);
		} else {
			log.error("XXX no cache inited!");
			return null;
		}

		// 设置redis的发布ID
		redisPublisherId = getRedisPublishId();
		log.info("%%%%%get redisPublisherId:{}",redisPublisherId);
		return cm;
	}

	/**
	 * 线程同步唤醒对象注入
	 * 
	 * @return
	 */
	@Bean
	public ThreadAwaitContainer getThreadAwaitContainer() {
		return new ThreadAwaitContainer();
	}

	/**
	 * RedisMessageListenerContainer对象注入,这里加入了对key变化的channel监听的listener
	 * 
	 * @param redisTemplate
	 * @param cacheManager
	 * @return
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(StringRedisTemplate redisTemplate,
			CacheManager cacheManager) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisTemplate.getConnectionFactory());
		RedisMsgListener redisMessageListener = new RedisMsgListener(cacheManager);
		redisMessageListenerContainer.addMessageListener(redisMessageListener,
				new PatternTopic(CacheConsts.REDIS_CACHE_MODIFY_TOPIC));
		return redisMessageListenerContainer;
	}

	/**
	 * 获取redis的发布ID
	 * 
	 * @return
	 */
	private String getRedisPublishId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 创建一个自定义策略的CaffeineCache
	 * 
	 * @param cacheName
	 * @param cacheSpec
	 * @return
	 */
	private CaffeineCache createCaffeineCache(String cacheName, String cacheSpec) {
		CaffeineSpec spec = CaffeineSpec.parse(cacheSpec);
		Caffeine caffeine = Caffeine.from(spec);
		CaffeineCache cache = new CaffeineCache(cacheName, caffeine.build(), false); // 不允许空值
		return cache;

	}

}
