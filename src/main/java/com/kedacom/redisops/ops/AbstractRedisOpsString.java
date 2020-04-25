/**  
* Title: AbstractRedisOpsString.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月16日 下午5:29:41  
* @version 1.0  
*/
package com.kedacom.redisops.ops;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
public abstract class AbstractRedisOpsString extends AbstractRedisOps<String> {

	private ValueOperations<String, String> strops;

	public AbstractRedisOpsString(StringRedisTemplate srt) {
		super(srt);
		strops = srt.opsForValue();
	}

	@Override
	protected String getFromRedis(String key) {
		String value = strops.get(getRedisKey(key));
		return value;
	}

	@Override
	protected void putToRedis(String key, String value) {
		strops.set(getRedisKey(key), value);
	}
}
