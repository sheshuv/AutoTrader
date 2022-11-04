package com.fin.autotrader.models;

public interface SubscriptionCallback {
	public void onMessage(TouchlineResponse response);
}