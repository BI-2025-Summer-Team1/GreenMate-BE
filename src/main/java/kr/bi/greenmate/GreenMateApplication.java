package kr.bi.greenmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableRetry
@EnableAsync
public class GreenMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreenMateApplication.class, args);
	}

}
