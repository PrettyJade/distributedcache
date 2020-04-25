/**  
* Title: UserInfoDaoTest.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月14日 上午9:30:49  
* @version 1.0  
*/
package com.kedacom.redisops.ops.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kedacom.redisops.ops.entity.CUser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserInfoDaoTest {

	UserInfoDao uiDao;

	@Test
	public void testGet() {
		uiDao = UserInfoDao.getInstance();
		String key = "12345@DEFAULT%unknown";
		log.info("---{}", uiDao.get(key));
		uiDao.setField(key, "deviceId", "zhangkai1");
		log.info("---{}", uiDao.get(key));
	}

	@Test
	public void testSet() {
		uiDao = UserInfoDao.getInstance();
		String key = "54321@DEFAULT%unknown";
		CUser user = newEntity();
		uiDao.set(key, user);
		log.info("---{}", uiDao.get(key));
	}

	private CUser getEntityFromRedis(String key) {
		CUser user = uiDao.get(key);
		return user;
	}

	private CUser newEntity() {
		CUser user = new CUser();
		user.setAccess_token("access_token1");
		user.setDeviceId("deviceId1");
		user.setIsSave(1);
		user.setWebIp("127.1.1.1");
		return user;
	}
}
