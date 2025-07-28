package kr.bi.greenmate.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private final String secret;
    private final long accessTokenValidityInMs;

    public JwtProperties(String secret, long accessTokenValidityInMs){
        this.secret = secret;
        this.accessTokenValidityInMs = accessTokenValidityInMs;
    }
}
