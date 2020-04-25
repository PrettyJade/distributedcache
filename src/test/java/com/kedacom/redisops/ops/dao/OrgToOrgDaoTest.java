/**  
* Title: OrgToOrgDaoTest.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月16日 上午9:41:34  
* @version 1.0  
*/
package com.kedacom.redisops.ops.dao;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhang.kai
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrgToOrgDaoTest {

	OrgToOrgDao otoDao;
	String key = "kkclub";

	@Test
	public void testGet() {
		otoDao = OrgToOrgDao.getInstance();
		Set<TypedTuple<String>> set1 = otoDao.get(key);
		set1.forEach(svalue -> {
			log.info("---{}:{}", svalue.getValue(), svalue.getScore());
		});
	}

	@Test
	public void testSet() {
		otoDao = OrgToOrgDao.getInstance();
		otoDao.set(key, genZset(3));
		Set<TypedTuple<String>> set1 = otoDao.get(key);
		set1.forEach(svalue -> {
			log.info("---{}:{}", svalue.getValue(), svalue.getScore());
		});
	}

	@Test
	public void testAddMembers() {
		otoDao = OrgToOrgDao.getInstance();
		Integer[] ints = new Integer[3];
		DefaultTypedTuple<String> dtt1 = new DefaultTypedTuple<String>("add4" , new Double(4));
		DefaultTypedTuple<String> dtt2 = new DefaultTypedTuple<String>("add5" , new Double(5));
		otoDao.addMembers(key, dtt1,dtt2);
		Set<TypedTuple<String>> set1 = otoDao.get(key);
		set1.forEach(svalue -> {
			log.info("---{}:{}", svalue.getValue(), svalue.getScore());
		});
	}
	
	@Test
	public void testRemoveMembers() {
		otoDao = OrgToOrgDao.getInstance();
		otoDao.removeMembers(key, "add4","add5");
		Set<TypedTuple<String>> set1 = otoDao.get(key);
		set1.forEach(svalue -> {
			log.info("---{}:{}", svalue.getValue(), svalue.getScore());
		});
		
	}

	private Set<TypedTuple<String>> genZset(int num) {
		Set<TypedTuple<String>> zset = new LinkedHashSet<TypedTuple<String>>();
		for (int i = 0; i < num; i++) {
			DefaultTypedTuple<String> dtt = new DefaultTypedTuple<String>("value" + i, new Double(i));
			zset.add(dtt);
		}
		return zset;
	}

}
