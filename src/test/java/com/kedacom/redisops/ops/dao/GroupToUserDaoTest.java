/**  
* Title: GroupToUserDaoTest.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月14日 上午9:24:09  
* @version 1.0  
*/  
package com.kedacom.redisops.ops.dao;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

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
public class GroupToUserDaoTest {

	GroupToUserDao guDao;
	String key = "12345@DEFAULT%unknown";

	@Test
	public void testSet() {
		guDao = GroupToUserDao.getInstance();	
		log.info("---{}", guDao.get(key));
		Set<String> entitySet = new HashSet<String>();
		for (int i = 0; i < 2; i++) {
			entitySet.add("member" + i);
		}
		guDao.set(key, entitySet);
		log.info("---{}", guDao.get(key));
	}
	
	@Test
	public void testAddMembers() {
		guDao = GroupToUserDao.getInstance();
		log.info("---{}", guDao.get(key));
		int num = 4;
		String[] memberArray = new String[num];
		for (int i = 0; i < num; i++) {
			memberArray[i] = ("member" + i);
		}
		guDao.addMembers(key, memberArray);
		log.info("---{}", guDao.get(key));

	}
	
	@Test
	public void testRemoveMembers() {
		guDao = GroupToUserDao.getInstance();
		log.info("---{}", guDao.get(key));
		int num = 4;
		String[] memberArray = new String[num];
		for (int i = 0; i < num; i++) {
			memberArray[i] = ("member" + i);
		}
		guDao.removeMembers(key, memberArray);
		log.info("---{}", guDao.get(key));
	}
	
	@Test
	public void testIsMember() {
		guDao = GroupToUserDao.getInstance();
		log.info("---{}", guDao.get(key));
		boolean ismem = guDao.isMember(key, "member5");
		log.info("---ismember:{}",ismem);
	}

}
