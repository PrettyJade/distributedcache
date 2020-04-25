/**  
* Title: AbstractRedisZsetOps.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月15日 下午4:12:10  
* @version 1.0  
*/
package com.kedacom.redisops.ops;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

/**
 * @author zhang.kai
 *
 */
public abstract class AbstractRedisOpsZset extends AbstractRedisOps<Set<TypedTuple<String>>> {

	ZSetOperations<String, String> zops;

	public AbstractRedisOpsZset(StringRedisTemplate srt) {
		super(srt);
		zops = srt.opsForZSet();
	}

	@Override
	protected Set<TypedTuple<String>> getFromRedis(String key) {
		Set<TypedTuple<String>> ztsSet = zops.rangeWithScores(getRedisKey(key), 0, -1);
		if ((null == ztsSet) || (ztsSet.isEmpty())) {
			return null;
		}
		return ztsSet;
	}

	@Override
	protected void putToRedis(String key, Set<TypedTuple<String>> value) {
		srt.multi();
		deleteFromRedis(key);
		zops.add(getRedisKey(key), value);
		srt.exec();
	}

	/**
	 * 在有序集合中增加成员组
	 * 
	 * @param key
	 * @param members
	 * @return
	 */

	public boolean addMembers(String key, TypedTuple<String>... members) {
		Set<TypedTuple<String>> memberSet = new LinkedHashSet<TypedTuple<String>>(Arrays.asList(members));
		zops.add(getRedisKey(key), memberSet);
		// 在redis更新完成后，更新内存
		if (enableMemoryCached) {
			delFromCacheManager(key);
			get(key);
		}
		pushKeyModified(key);
		return true;
	}

	/**
	 * 在集合中增加成员组
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public boolean removeMembers(String key, String... members) {
		zops.remove(getRedisKey(key), members);
		// 在redis更新完成后，更新内存
		if (enableMemoryCached) {
			delFromCacheManager(key);
			get(key);
		}
		pushKeyModified(key);
		return true;
	}

}
