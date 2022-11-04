package com.fin.autotrader.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.autotrader.models.AuthRequest;
import com.fin.autotrader.models.AuthResponse;
import com.fin.autotrader.models.SearchScripResult;
import com.fin.autotrader.models.SubscriptionCallback;
import com.fin.autotrader.models.TouchlineResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Configuration
public class RestApi {

	@Autowired
	private RestTemplate restTemplate;

	public AuthResponse authResponse;
	public WebSocketSession session;
	
	@Autowired
	AuthRequest req;

	private Map<String, SubscriptionCallback> subscriptions = new ConcurrentHashMap<String, SubscriptionCallback>();

	public SearchScripResult searchscrip(String searchString, String exch) {

		Map<String, String> req = new HashMap<>();
		req.put("uid", authResponse.getActid());
		req.put("stext", URLEncodedUtils.formatSegments(searchString).replaceAll("/", ""));
		req.put("exch", exch);
		String jd;
		try {
			jd = "jData=" + new ObjectMapper().writeValueAsString(req) + "&jKey=" + authResponse.getSusertoken();
			log.info(jd);
			HttpEntity<String> enitty = new HttpEntity<String>(jd);
			ResponseEntity<SearchScripResult> exchange = restTemplate.exchange(ApiEndpoints.endpoint + ApiEndpoints.searchscrip,
					HttpMethod.POST, enitty, new ParameterizedTypeReference<SearchScripResult>() {
					});
			SearchScripResult body = exchange.getBody();
			log.info("body {}", body);
			return body;
		} catch (Exception e) {
			log.error("error", e);
		}
		return null;
	}

	public void login() throws IOException {

		if (Paths.get("auth.json").toFile().exists()) {
			List<String> readLines = IOUtils
					.readLines(Files.newInputStream(Paths.get("auth.json"), StandardOpenOption.READ));
			if (readLines != null) {
				String json = readLines.get(0);
				if (json != null) {
					authResponse = new ObjectMapper().readValue(Files.newInputStream(Paths.get("auth.json")),
							AuthResponse.class);
					log.info("existing response: {}", authResponse);
					if (authResponse.getSusertoken() != null) {
						return;
					}
				}
			}
		}
		AuthRequest authRequest = new AuthRequest();
		String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(authRequest.getPwd());
		authRequest.setPwd(sha256hex);
		Scanner sc = new Scanner(System.in);
		log.info("please enter otp/totp: ");
//		String otp = sc.next();
		authRequest.setFactor2("887330");

		String jd = "jData=" + new ObjectMapper().writeValueAsString(authRequest);
		log.info(jd);
		ResponseEntity<AuthResponse> responseEntity = restTemplate.postForEntity(ApiEndpoints.endpoint + ApiEndpoints.authorize, jd,
				AuthResponse.class);
		authResponse = responseEntity.getBody();
		IOUtils.write(new ObjectMapper().writeValueAsBytes(authResponse),
				Files.newOutputStream(Paths.get("auth.json"), StandardOpenOption.CREATE));
		log.info("login responsae : {}", authResponse);

	}

//	public void startSocket() throws InterruptedException, ExecutionException, IOException {
//		WebSocketClient client = new StandardWebSocketClient();
//		HashMap<String, String> req = new HashMap<>();
//		req.put("uid", authResponse.getActid());
//		req.put("actid", authResponse.getActid());
//		req.put("susertoken", authResponse.getSusertoken());
//		req.put("t", "c");
//		req.put("source", "API");
//
//		String authReq = new ObjectMapper().writeValueAsString(req);
//		ListenableFuture<WebSocketSession> doHandshake = client.doHandshake(new AbstractWebSocketHandler() {
//			@Override
//			public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//				log.error("errro", exception);
//				try {
//					handleTransportError(session, exception);
//				} catch (Exception e) {
//					log.error("error", e);
//				}
//			}
//
//			@Override
//			public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//				log.info("message {}", new ObjectMapper().writeValueAsString(message.getPayload()));
////				handleMessage(session, message);
//			}
//
//			@Override
//			protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//				log.info("text message {}", message);
////				super.handleTextMessage(session, message);
//			}
//		}, Config.WEB_SOCKET_ENDPOINT, "/NorenWSTP");
//		doHandshake.addCallback(result -> {
//			log.info("onSuccess {}", result);
//
//			if (result instanceof StandardWebSocketSession) {
//				StandardWebSocketSession session = (StandardWebSocketSession) result;
////				ConcurrentWebSocketSessionDecorator session = new ConcurrentWebSocketSessionDecorator(session1, 0, 0);
//				log.info("isConnected {}", session.isOpen());
//			}
//		}, ex -> log.info("Throwable {}", ex));
//
//		session = doHandshake.get();
//		log.info("sending new message1");
//		session.sendMessage(new TextMessage(authReq.getBytes()));
//		Scanner sc = new Scanner(System.in);
//		sc.next();
//		TimeUnit.SECONDS.sleep(1);
//
//	}

//	public void startSpringSocket() throws InterruptedException, ExecutionException, IOException {
//		WebSocketClient client = new StandardWebSocketClient();
//		HashMap<String, String> req = new HashMap<>();
//		req.put("uid", authResponse.getActid());
//		req.put("actid", authResponse.getActid());
//		req.put("susertoken", authResponse.getSusertoken());
//		req.put("t", "c");
//		req.put("source", "API");
//
//		String authReq = new ObjectMapper().writeValueAsString(req);
//
//		ListenableFuture<WebSocketSession> doHandshake = client.doHandshake(new AbstractWebSocketHandler() {
//			@Override
//			public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//				log.error("errro", exception);
//				try {
//					handleTransportError(session, exception);
//				} catch (Exception e) {
//					log.error("error", e);
//				}
//			}
//
//			@Override
//			public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//				log.info("message {}", new ObjectMapper().writeValueAsString(message.getPayload()));
////				handleMessage(session, message);
//			}
//
//			@Override
//			protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//				log.info("text message {}", message);
////				super.handleTextMessage(session, message);
//			}
//		}, Config.WEB_SOCKET_ENDPOINT, "/NorenWSTP");
//		doHandshake.addCallback(result -> {
//			log.info("onSuccess {}", result);
//
//			if (result instanceof StandardWebSocketSession) {
//				StandardWebSocketSession session = (StandardWebSocketSession) result;
////				ConcurrentWebSocketSessionDecorator session = new ConcurrentWebSocketSessionDecorator(session1, 0, 0);
//				log.info("isConnected {}", session.isOpen());
//			}
//		}, ex -> log.info("Throwable {}", ex));
//
//		session = doHandshake.get();
//		log.info("sending new message1");
//		session.sendMessage(new TextMessage(authReq.getBytes()));
//		Scanner sc = new Scanner(System.in);
//		sc.next();
//		TimeUnit.SECONDS.sleep(1);
//
//	}

	public synchronized void sendMessage(String message) throws JsonProcessingException, IOException {
		log.info("sending message {}", message);
		if (session != null && session.isOpen()) {
			session.sendMessage(new TextMessage(message.getBytes()));
		}
	}

	@Bean
	public WebSocketConnectionManager wsConnectionManager() {

		// Generates a web socket connection
		WebSocketConnectionManager manager = new WebSocketConnectionManager(new StandardWebSocketClient(),
				new TextWebSocketHandler() {
					@Override
					public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
						log.error("errro", exception);
						try {
							handleTransportError(session, exception);
						} catch (Exception e) {
							log.error("error", e);
						}
					}

					@Override
					public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//						log.info("message {}", message.getPayload());
						TouchlineResponse touchlineResponse = new ObjectMapper()
								.readValue(message.getPayload().toString().getBytes(), TouchlineResponse.class);

//						if (touchlineResponse.t == "ck") {
////	                         trigger("open", [result]);
//						}
						if ("tk".equals(touchlineResponse.t) || "tf".equals(touchlineResponse.t)) {
							SubscriptionCallback subscriptionCallback = subscriptions.get(touchlineResponse.tk);
							if (subscriptionCallback != null) {
								subscriptionCallback.onMessage(touchlineResponse);
							}
						}
//						if (touchlineResponse.t == "dk" || touchlineResponse.t == "df") {
////	                         trigger("quote", [result]);
//						}
//						if (touchlineResponse.t == "om") {
////	                         trigger("order", [result]);
//						}
					}

					@Override
					protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
						log.info("text message {}", message);
//    					super.handleTextMessage(session, message);
					}

					@Override
					public void afterConnectionEstablished(WebSocketSession session1) throws Exception {
						log.info("teafterConnectionEstablished {}", session1);
						session = session1;
					}
				}, // Must be defined to handle messages
				ApiEndpoints.WEB_SOCKET_ENDPOINT);

		// Will connect as soon as possible
		manager.setAutoStartup(true);
		manager.start();
		return manager;
	}
//	public void socket() throws JsonProcessingException, InterruptedException, ExecutionException, IOException {
//		WebSocketClient client = new StandardWebSocketClient();
//
//		HashMap<String, String> req = new HashMap<>();
//		req.put("uid", authResponse.getActid());
//		req.put("actid", authResponse.getActid());
//		req.put("susertoken", authResponse.getSusertoken());
//		req.put("t", "c");
//		req.put("source", "API");
//
//		String authReq = new ObjectMapper().writeValueAsString(req);
//		ListenableFuture<WebSocketSession> doHandshake = client.doHandshake(new AbstractWebSocketHandler() {
////				@Override
////				public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
////					log.error("errro",exception);
////					handleTransportError(session, exception);
////				}
//
//			@Override
//			public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//				log.info("message {}", new ObjectMapper().writeValueAsString(message.getPayload()));
////					handleMessage(session, message);
//			}
//
//			@Override
//			protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//				log.info("text message {}", message);
////					super.handleTextMessage(session, message);
//			}
//		}, Config.WEB_SOCKET_ENDPOINT, "/NorenWSTP");
//		doHandshake.addCallback(result -> {
//			log.info("onSuccess {}", result);
//
//			if (result instanceof StandardWebSocketSession) {
//				StandardWebSocketSession session = (StandardWebSocketSession) result;
////					ConcurrentWebSocketSessionDecorator session = new ConcurrentWebSocketSessionDecorator(session1, 0, 0);
//				log.info("isConnected {}", session.isOpen());
////					try {
////						
////					} catch (IOException e) {
////						log.error("error", e);
////					}
//			}
//		}, ex -> log.info("Throwable {}", ex));
//
//		
//		WebSocketSession session1 = doHandshake.get();
//		session = session1;
//		sendMessage(authReq);
////		log.info("sending new message1");
////		session1.sendMessage(new TextMessage(authReq.getBytes()));
//		TimeUnit.SECONDS.sleep(1);
////		log.info("sending new message2");
//		sendMessage("{\"t\":\"h\"}");
////		session1.sendMessage(new TextMessage("{\"t\":\"h\"}".getBytes()));
//
////		HashMap<String, String> req1 = new HashMap<>();
////		req1.put("t", "t");
////		req1.put("k", "NFO|49477#NFO|49371#NFO|49542#");
////		TimeUnit.SECONDS.sleep(2);
////		log.info("sending new message3");
////		sendMessage(new ObjectMapper().writeValueAsString(req1));
//
////		Thread.currentThread().join();
//	}

	public void subscribe(String token, String exchange, SubscriptionCallback callback) {
		log.info("subscribe {} {}", token, exchange);
		HashMap<String, String> req1 = new HashMap<>();
		req1.put("t", "t");
		req1.put("k", exchange + "|" + token + "#");
		String reqString;
		try {
			reqString = new ObjectMapper().writeValueAsString(req1);
			sendMessage(reqString);
		} catch (IOException e) {
			log.error("error {}", e);
		}
		subscriptions.put(token, callback);
	}

	public void unsubscribe(String token, String exchange) {
		log.info("unsubscribe {} {}", token, exchange);
		HashMap<String, String> req1 = new HashMap<>();
		req1.put("t", "u");
		req1.put("k", exchange + "|" + token + "#");
		String reqString;
		try {
			reqString = new ObjectMapper().writeValueAsString(req1);
			sendMessage(reqString);
		} catch (IOException e) {
			log.error("error {}", e);
		}
		subscriptions.remove(token);
	}
}
