package com.fin.autotrader;

import java.util.TimeZone;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fin.autotrader.core.RestApi;
import com.fin.autotrader.models.AuthResponse;
import com.fin.autotrader.order.AutoTrader;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class AutoTraderApplication {

	static AuthResponse authResponse;
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		ApplicationContext app = SpringApplication.run(AutoTraderApplication.class, args);

		AutoTrader login = app.getBean(AutoTrader.class);

		RestApi api = app.getBean(RestApi.class);

		login.init();

	}


	@Bean
	public RestTemplate restTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);

		return new RestTemplate(requestFactory);
	}
}
