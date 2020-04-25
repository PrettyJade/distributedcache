/**  
* Title: UserInfoDao.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月9日 下午1:23:22  
* @version 1.0  
*/
package com.kedacom.redisops.ops.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.kedacom.redisops.ops.AbstractRedisOpsHash;
import com.kedacom.redisops.ops.entity.CUser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
@Component("userInfoDao")
public class UserInfoDao extends AbstractRedisOpsHash<CUser> {

	private static UserInfoDao instance;

	@Resource
	private ApplicationContext app;

	@PostConstruct
	private void setInstance() {
		instance = (UserInfoDao) app.getBean(this.getClass());
	}

	public static UserInfoDao getInstance() {
		return instance;
	}

	/**
	 * 构造函数
	 * 
	 * @param srt
	 */
	public UserInfoDao(StringRedisTemplate srt) {
		super(srt);
		this.keyPrefix = "USER_LOGIN_DEVICE_INFO";
		this.enableMemoryCached = true;
	}

	
	@Override
	protected CUser loadValueFromDb(String key) {
		log.info("override loadValueFromDb CUser");
		return null;
	}


	@Override
	protected CUser convertMap2T(Map<String, Object> map) {
		CUser user = new CUser();
		user.setAccess_token((String) map.get("access_token"));
		user.setDeviceId((String) map.get("deviceId"));
		user.setWebIp((String) map.get("webIp"));
		String issave = (String) map.get("isSave");
		user.setIsSave(null != issave ? Integer.parseInt(issave) : null);
		return user;
	}

	@Override
	protected Map<String, Object> convertT2Map(CUser entity) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("access_token", entity.getAccess_token());
		map.put("deviceId", entity.getDeviceId());
		map.put("webIp", entity.getWebIp());
		map.put("isSave", entity.getIsSave().toString());
		return map;
	}

}
