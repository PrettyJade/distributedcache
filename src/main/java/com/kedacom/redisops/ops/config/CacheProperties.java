/**  
* Title: CacheProperties.java
* Description:  
* Copyright: Copyright (c) 2020 
* Company: www.kedacom.com 
* @author zhang.kai  
* @date 2020年4月22日 下午3:27:33  
* @version 1.0  
*/  
package com.kedacom.redisops.ops.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author zhang.kai
 *
 */
@Component
@ConfigurationProperties(prefix="vline.cache")
@Data
public class CacheProperties {
	private String defaultspec;
	private Map<String,String> cachespecs;

}
