/**  
* Title: CacheConsts.java
* Description: cache配置中常量定义
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月16日 下午7:02:18  
* @version 1.0  
*/
package com.kedacom.redisops.ops.config;

/**
 * @author zhang.kai
 *
 */
public interface CacheConsts {
	/* 基本操作的常量 */
	int LOAD_FROM_DB_RETRY_TIMES = 5; // 从数据库载入的重试次数
	int LOAD_FROM_DB_WAIT_TIME = 20; // 从数据库载入的等待时间，单位为毫秒

	/* cache manager的配置常量 */
	int CAFFEINE_INITIALCAPACITY = 100; // caffeine.initialCapacity
	int CAFFEINE_EXPIREAFTERWRITE = 600;// expireAfterWrite,单位为秒

	/* redis消息监听topic */
	String REDIS_CACHE_MODIFY_TOPIC = "REDIS_CACHE_MODIFY_TOPIC"; // redis的cache更新通知

	/* 分隔符定义 */
	String KEYPREFIX_SEPARATE = ":"; // key的前缀分隔符
	String REDIS_PUBLISHER_ID_SEPARATE = "$";

}
