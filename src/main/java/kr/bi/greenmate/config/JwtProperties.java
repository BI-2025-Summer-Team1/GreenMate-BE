package kr.bi.greenmate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private final String secret;
	private final long accessTokenValidityInMs;

	public JwtProperties(String secret, long accessTokenValidityInMs) {
		this.secret = secret;
		this.accessTokenValidityInMs = accessTokenValidityInMs;
	}
}
