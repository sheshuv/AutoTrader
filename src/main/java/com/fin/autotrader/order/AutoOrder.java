package com.fin.autotrader.order;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fin.autotrader.core.RestApi;
import com.fin.autotrader.models.Direction;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ToString
public class AutoOrder {

	@Autowired
	private RestApi api;

	private AtomicInteger profitPoints = new AtomicInteger(0);
	private AtomicInteger lossPoints = new AtomicInteger(0);

	private AtomicInteger callOrderProfitsPoints = new AtomicInteger(0);
	private AtomicInteger putOrderProfitsPoints = new AtomicInteger(0);

	private AtomicInteger callOrderLossPoints = new AtomicInteger(0);
	private AtomicInteger putOrderLossPoints = new AtomicInteger(0);

	private AtomicInteger lastCallOrderPrice = new AtomicInteger(0);
	private AtomicInteger lastPutOrderPrice = new AtomicInteger(0);

	private AtomicInteger numberOfCallOrders = new AtomicInteger(0);
	private AtomicInteger numberOfPutOrders = new AtomicInteger(0);

	private Direction direction;

	private boolean stopOrder = false;

	public void order(AtomicInteger strikePrice) {

		PriceTraker tracker = new PriceTraker(api);
		tracker.track("BANKNIFTY", "NSE");
		try {
			TimeUnit.SECONDS.sleep(5);
			while (!stopOrder) {
				log.info("AutoOrder status {}", this);
				if (direction != null && direction != Direction.UP) {
					if (tracker.getDirection() == Direction.UP) {
						log.info("direction change {}", direction);
						cancelPutOrder(tracker.getLastPrice());
						callOrder(tracker.getLastPrice());
					} else {
						log.info("direction change {}", direction);
						cancelCallOrder(tracker.getLastPrice());
						putOrder(tracker.getLastPrice());
					}
				}
				direction = tracker.getDirection();
				TimeUnit.SECONDS.sleep(3);
			}
		} catch (InterruptedException e) {
			log.error("error");
		}

//		log.info("strikeprice {}", strikePrice);
//		String scripString = String.format("BANKNIFTY10NOV22C%s", strikePrice.get() - 300);
//		SearchScripResult searchscrip1 = api.searchscrip(scripString, "NFO");
//		Scrip scrip = searchscrip1.getValues().get(0);

//		api.subscribe(scrip.getToken(), "NFO", response1 -> {
//			log.info("subscribed {}", response1);
//		});

//		log.info("strikeprice {}", strikePrice);
//		scripString = String.format("BANKNIFTY10NOV22P%s", strikePrice.get() + 300);
//		searchscrip1 = api.searchscrip(scripString, "NFO");
//		scrip = searchscrip1.getValues().get(0);
//
//		api.subscribe(scrip.getToken(), "NFO", response1 -> {
//			log.info("subscribed {}", response1);
//		});
	}

	private void callOrder(AtomicInteger lastPrice) {
		numberOfCallOrders.incrementAndGet();
		lastCallOrderPrice.set(lastPrice.get());

	}

	private void putOrder(AtomicInteger lastPrice) {
		numberOfPutOrders.incrementAndGet();
		lastPutOrderPrice.set(lastPrice.get());
	}

	private void cancelCallOrder(AtomicInteger atomicInteger) {
		log.info("cancelCallOrder");
		int lastPrice = lastCallOrderPrice.get();
		if (lastPrice > atomicInteger.get()) {
			int delta = lastPrice - atomicInteger.get();
			callOrderLossPoints.addAndGet(delta);
			profitPoints.addAndGet(lastPrice);
		} else {
			int delta = atomicInteger.get() - lastPrice;
			callOrderProfitsPoints.addAndGet(delta);
			lossPoints.addAndGet(delta);
		}

	}

	private void cancelPutOrder(AtomicInteger atomicInteger) {
		log.info("cancelPutOrder");
		int lastPrice = lastPutOrderPrice.get();
		if (lastPrice > atomicInteger.get()) {
			int delta = lastPrice - atomicInteger.get();
			putOrderLossPoints.addAndGet(delta);
			profitPoints.addAndGet(lastPrice);
		} else {
			int delta = atomicInteger.get() - lastPrice;
			putOrderProfitsPoints.addAndGet(delta);
			lossPoints.addAndGet(delta);
		}
	}
}
