/**  
* Title: ConfigInfoDaoTest.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月16日 下午6:51:12  
* @version 1.0  
*/  
package com.kedacom.redisops.ops.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigInfoDaoTest {

	ConfigInfoDao ciDao;
	String key = "12345config";
	
	@Test
	public void testGet() {
		ciDao = ciDao.getInstance();
		log.info("---{}", ciDao.get(key));
	}
	
	@Test
	public void testSet() {
		ciDao = ciDao.getInstance();
		ciDao.set(key, "zhangkai");
		log.info("---{}",  ciDao.get(key));
	}

}
