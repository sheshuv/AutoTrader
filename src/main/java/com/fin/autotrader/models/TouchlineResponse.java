package com.fin.autotrader.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class TouchlineResponse {
	public String t;
	public String s;
	public String uid;
	public String e;
	public String tk;
	public String ts;
	public String pp;
	public String ls;
	public String ti;
	public String lp;
	public String pc;
	public String c;
	public String ft;
}