package kr.bi.greenmate;

import kr.bi.greenmate.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableConfigurationProperties(JwtProperties.class)
@ConfigurationPropertiesScan
@SpringBootApplication
@EnableJpaAuditing
public class GreenMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenMateApplication.class, args);
    }

}
