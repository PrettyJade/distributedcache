/**  
* Title: AbstractRedisSetOps.java
* Description: 对redis的hash结构操作抽象类 
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月11日 下午1:05:11  
* @version 1.0  
*/
package com.kedacom.redisops.ops;

import java.util.Arrays;
import java.util.Set;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
public abstract class AbstractRedisOpsSet extends AbstractRedisOps<Set<String>> {

	private SetOperations<String, String> sops;

	public AbstractRedisOpsSet(StringRedisTemplate srt) {
		super(srt);
		sops = srt.opsForSet();
	}

	@Override
	protected Set<String> getFromRedis(String key) {
		Set<String> members = sops.members(getRedisKey(key));
		if ((null == members) || (members.isEmpty())) {
			return null;
		}
		return members;
	}

	@Override
	protected void putToRedis(String key, Set<String> members) {
		srt.multi();
		deleteFromRedis(key);
		sops.add(getRedisKey(key), members.toArray(new String[] {}));
		srt.exec();
	}

	/**
	 * 在集合中增加成员组
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public boolean addMembers(String key, String... members) {
		boolean result = false;
		sops.add(getRedisKey(key), members);
		if (enableMemoryCached) {
			Set<String> memberSet = getFromCacheManager(key);
			if (null != memberSet) {
				// 从cachemanager中取集合，修改集合
				result = memberSet.addAll(Arrays.asList(members));
			} else {
				// 从redis中取，并回写到cache中
				memberSet = getFromRedis(key);
				result = putToCacheManager(key, memberSet);
			}
		}
		pushKeyModified(key);
		return result;
	}

	/**
	 * 在集合中增加成员组
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public boolean removeMembers(String key, String... members) {
		boolean result = false;
		sops.remove(getRedisKey(key), members);
		if (enableMemoryCached) {
			Set<String> memberSet = getFromCacheManager(key);
			if (null != memberSet) {
				// 从cachemanager中取集合，修改集合
				result= memberSet.removeAll(Arrays.asList(members));
			} else {
				// 从redis中取，并回写到cache中
				memberSet = getFromRedis(key);
				result= putToCacheManager(key, memberSet);
			}
		}
		pushKeyModified(key);
		return result;
	}

	/**
	 * 判断成员是否在集合中
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean isMember(String key, String member) {
		if (enableMemoryCached) {
			Set<String> memberSet = getFromCacheManager(key);
			if (null != memberSet) {
				// 从cachemanager中取集合
				return memberSet.contains(member);
			} else {
				// 从redis中取集合
				return sops.isMember(getRedisKey(key), member);
			}
		} else {
			// 从redis中取集合
			return sops.isMember(getRedisKey(key), member);
		}
	}

}
