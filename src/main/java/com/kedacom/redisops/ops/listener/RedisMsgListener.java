/**  
* Title: RedisMsgListener.java
* Description: 监听来自key改变channel的消息 
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月16日 下午7:59:55  
* @version 1.0  
*/
package com.kedacom.redisops.ops.listener;

import java.nio.charset.Charset;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.util.StringUtils;

import com.kedacom.redisops.ops.config.CacheConfiguration;
import com.kedacom.redisops.ops.config.CacheConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
public class RedisMsgListener implements MessageListener {

	CacheManager cm;

	/**
	 * @param caffeineCacheManager
	 */
	public RedisMsgListener(CacheManager cacheManager) {
		this.cm = cacheManager;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		if (null != message.getBody()) {
			String key = new String(message.getBody(), Charset.forName("UTF8"));
			log.debug("+++reveive msg:{} which topic is:{}", key,
					new String(message.getChannel(), Charset.forName("UTF8")));
			String publisherId = findRedisPublisherId(key);
			// 如果是本进程发布的，不处理
			if (StringUtils.isEmpty(publisherId) || publisherId.equals(CacheConfiguration.redisPublisherId)) {
				return;
			}
			// 不是本进程发布的，在一级缓存中失效.发布的key形式为：{redisPublishId}${keyPrefix}:{key}
			String redisKey = key.substring(key.indexOf(CacheConsts.REDIS_PUBLISHER_ID_SEPARATE) + 1);
			String keyPrefix = findKeyprefixFromRedisKey(redisKey);
			Cache cache = cm.getCache(keyPrefix);
			if (null != cache) {
				// 从一级缓存中删除
				log.debug("---evict key:{} from cache:{}", redisKey, cache.getName());
				cache.evict(redisKey);
			}
		} else {
			log.error("RedisMsgListener onMessage null!");
		}
	}

	private String findRedisPublisherId(String key) {
		String publisherId = key.substring(0, key.indexOf(CacheConsts.REDIS_PUBLISHER_ID_SEPARATE));
		return publisherId;
	}

	/**
	 * 从key中获取前缀，在redis中，key的形式为"CONFIG_INFO:12345"这样的形式。这里前缀为"CONFIG_INFO",同时也是cache的name
	 * 
	 * @param msg
	 * @return
	 */
	private String findKeyprefixFromRedisKey(String key) {
		String keyPrefix = key.substring(0, key.indexOf(CacheConsts.KEYPREFIX_SEPARATE));
		return keyPrefix;
	}

}
