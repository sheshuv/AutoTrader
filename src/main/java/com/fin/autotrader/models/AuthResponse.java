package com.fin.autotrader.models;

import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class AuthResponse {
	private String request_time;
	private String actid;
	private String uname;
	ArrayList<Object> prarr = new ArrayList<Object>();
	private String stat;
	private String susertoken;
	private String email;
	private String uid;
	private String brnchid;
	ArrayList<String> orarr = new ArrayList<>();
	ArrayList<String> exarr = new ArrayList<>();
	ArrayList<Integer> values = new ArrayList<>();
	Map<Integer,MWatcher[]> mws;
	private String brkname;
	private String lastaccesstime;
	private String emsg;

}
