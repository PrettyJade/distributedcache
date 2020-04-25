package com.kedacom.redisops;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.kedacom.redisops.ops.dao.ConfigInfoDao;
import com.kedacom.redisops.ops.lock.ThreadAwaitContainer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class RedisOpsApplication implements ApplicationRunner {

	@Autowired
	ThreadAwaitContainer tac;

	public static void main(String[] args) {
		SpringApplication.run(RedisOpsApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		testConfigInfoDaoExpired();
	}

	private void testConfigInfoDaoExpired() {
		ConfigInfoDao ciDao;
		String key = "12345config";
		ciDao = ConfigInfoDao.getInstance();

		ciDao.set(key, "zhangkai");
		log.info("--first-{}", ciDao.get(key));
		try {
			tac.await(key, 5000);
		} catch (Exception e) {
			log.error("await fail:", e);
		}
		log.info("--after 5s-{}", ciDao.get(key));
	}

}
