package com.fin.autotrader.models;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@ConfigurationProperties("auth")
@Component
public class AuthRequest {

	private String source = "API";
	private String apkversion = "js:1.0.0";
	private String uid = "FA78201";
	private String pwd = "Sheshu@1";
	private String factor2 = "";
	private String vc = "FA78201_U";
	private String apiSecret = "4ac9ec6346dd5feb9c49a0dbadbad7f9";
	private String imei = "abc1234";
	private String appkey="25f5cc8e6c45b80da5d854557c7ce62339e9ba3ab2ba33269d95b47c65895452";
}
