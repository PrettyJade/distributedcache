/**  
* Title: OrgToOrgDao.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月15日 下午5:04:01  
* @version 1.0  
*/  
package com.kedacom.redisops.ops.dao;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.kedacom.redisops.ops.AbstractRedisOpsZset;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
@Component("orgToOrgDao")
public class OrgToOrgDao extends AbstractRedisOpsZset{
	private static OrgToOrgDao instance;

	@Resource
	private ApplicationContext app;

	@PostConstruct
	private void setInstance() {
		instance = (OrgToOrgDao) app.getBean(this.getClass());
	}

	public static OrgToOrgDao getInstance() {
		return instance;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param srt
	 */
	public OrgToOrgDao(StringRedisTemplate srt) {
		super(srt);
		this.enableMemoryCached = true;
		this.keyPrefix = "ORG_TO_ORG";
	}

	
	

}
