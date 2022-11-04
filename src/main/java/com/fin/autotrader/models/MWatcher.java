package com.fin.autotrader.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class MWatcher {
 private String exch;
 private String token;
 private String tsym;
 private String weekly;
 private String dname;
 private String pp;
 private String ls;
 private String ti;
}