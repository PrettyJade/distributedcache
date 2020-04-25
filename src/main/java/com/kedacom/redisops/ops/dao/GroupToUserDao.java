/**  
* Title: GroupToUserDao.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月11日 下午1:47:00  
* @version 1.0  
*/
package com.kedacom.redisops.ops.dao;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.kedacom.redisops.ops.AbstractRedisOpsSet;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
@Component("groupToUserDao")
public class GroupToUserDao extends AbstractRedisOpsSet{

	private static GroupToUserDao instance;

	@Resource
	private ApplicationContext app;

	@PostConstruct
	private void setInstance() {
		instance = (GroupToUserDao) app.getBean(this.getClass());
	}

	public static GroupToUserDao getInstance() {
		return instance;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param srt
	 */
	public GroupToUserDao(StringRedisTemplate srt) {
		super(srt);
		this.enableMemoryCached = true;
		this.keyPrefix = "GROUP_TO_USER";
	}

	@Override
	protected Set<String> loadValueFromDb(String key) {
		log.info("...GroupToUserDao loadValueFromDb!");
		return null;
	}

}
