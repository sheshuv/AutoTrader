package com.fin.autotrader.order;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.autotrader.core.RestApi;
import com.fin.autotrader.models.Scrip;
import com.fin.autotrader.models.SearchScripResult;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Controller
public class AutoTrader {

	@Autowired
	RestApi api;

	@Autowired
	AutoOrder order;

	public void init() {
		try {
			api.login();

			HashMap<String, String> req = new HashMap<>();
			req.put("uid", "FA78201");
			req.put("actid", "FA78201");
			req.put("susertoken", api.authResponse.getSusertoken());
			req.put("t", "c");
			req.put("source", "API");

			String authReq = new ObjectMapper().writeValueAsString(req);
			api.sendMessage(authReq);

			new Thread(() -> {
				while (true) {
					try {
						TimeUnit.SECONDS.sleep(10);
						log.info("sending new message2");
						api.sendMessage("{\"t\":\"h\"}");
					} catch (InterruptedException | IOException e) {
						log.error("error", e);
					}
				}
			}).start();

			SearchScripResult searchscrip = api.searchscrip("BANKNIFTY", "NSE");

			Scrip searchScripResult = searchscrip.getValues().get(0);
			final String token = searchScripResult.getToken();
			AtomicInteger strikePrice = new AtomicInteger(0);
			api.subscribe(token, "NSE", response -> {
				log.info("price {}", response.lp);
				int round = Math.round(Double.valueOf(response.lp).intValue());
				int rounded = ((round + 99) / 100) * 100;
				strikePrice.set(rounded);
				api.unsubscribe(token, "NSE");
				if (strikePrice.get() != 0) {
					order.order(strikePrice);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
