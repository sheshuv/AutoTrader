package com.fin.autotrader.order;

import java.util.concurrent.atomic.AtomicInteger;

import com.fin.autotrader.core.RestApi;
import com.fin.autotrader.models.Direction;
import com.fin.autotrader.models.Scrip;
import com.fin.autotrader.models.SearchScripResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceTraker {

	private RestApi api;

	private AtomicInteger lastPrice = new AtomicInteger(0);

	private AtomicInteger lastReportedPrice = new AtomicInteger(0);

	private Direction direction = null;

	private Integer bufferPoints = 5;

	private AtomicInteger downTrendCounter = new AtomicInteger(0);
	private AtomicInteger upTrendCounter = new AtomicInteger(0);

	public PriceTraker(RestApi api) {
		this.api = api;
	}

	public PriceTraker(RestApi api, Integer bufferPoints) {
		this.api = api;
		this.bufferPoints = bufferPoints;
	}

	public void track(String scripName, String exchange) {
		SearchScripResult searchscrip = api.searchscrip(scripName, exchange);
		Scrip searchScripResult = searchscrip.getValues().get(0);
		final String token = searchScripResult.getToken();
		log.info("token {}", token);
		api.subscribe(token, exchange, response -> {
			log.info("price {}", response.lp);
			if (response.lp != null) {
				int currentPrice = Math.round(Double.valueOf(response.lp).intValue());
				if (lastPrice.get() == 0) {
					lastPrice.set(currentPrice);
				}
				if (getLastPrice().get() > currentPrice) {
					log.info("upTrendCounter {} downTrendCounter {}",upTrendCounter,downTrendCounter);
					if (downTrendCounter.get() >= 3) {
						setDirection(Direction.DOWN);
						downTrendCounter.set(0);
						upTrendCounter.set(0);
					}
					downTrendCounter.incrementAndGet();
				} else {
					if (upTrendCounter.get() >= 3) {
						setDirection(Direction.UP);
						downTrendCounter.set(0);
						upTrendCounter.set(0);
					}
					upTrendCounter.incrementAndGet();
				}
				getLastPrice().set(currentPrice);
			}
		});

	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public AtomicInteger getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(AtomicInteger lastPrice) {
		this.lastPrice = lastPrice;
	}

}
