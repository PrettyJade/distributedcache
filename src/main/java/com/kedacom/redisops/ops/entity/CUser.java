package com.kedacom.redisops.ops.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class CUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 486568502218029342L;
	private String uuid;// sessionID
	private String deviceId; // 设备号
	private long lastTime;// session最后的重置时间
	private String webIp;
	private Integer isSave = 0;	//是否为安全系统，0：个人系统(默认),9:安全系统; 
	private String access_token;
	private String jwt_token;
	private Integer webPort; // 视信通登录服务对应的端口。
	
}
