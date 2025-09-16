package kr.bi.greenmate.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class GeminiApiConfig {

	@Bean
	public RestClient geminiRestClient(RestClient.Builder builder) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout((int)Duration.ofSeconds(3).toMillis());
		factory.setReadTimeout((int)Duration.ofSeconds(12).toMillis());

		return builder
			.baseUrl("https://generativelanguage.googleapis.com/v1beta")
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.requestFactory(factory)
			.build();
	}

	@Bean
	public ObjectMapper geminiObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return om;
	}
}
