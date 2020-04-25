/**  
* Title: ConfigInfoDao.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月16日 下午6:47:08  
* @version 1.0  
*/
package com.kedacom.redisops.ops.dao;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.kedacom.redisops.ops.AbstractRedisOpsString;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
@Component("configInfoDao")
public class ConfigInfoDao extends AbstractRedisOpsString {

	private static ConfigInfoDao instance;

	@Resource
	private ApplicationContext app;

	@PostConstruct
	private void setInstance() {
		instance = (ConfigInfoDao) app.getBean(this.getClass());
	}

	public static ConfigInfoDao getInstance() {
		return instance;
	}

	/**
	 * 构造函数
	 * 
	 * @param srt
	 */
	public ConfigInfoDao(StringRedisTemplate srt) {
		super(srt);
		this.enableMemoryCached = true;
		this.keyPrefix = "CONFIG_INFO";
	}
	
	

}
