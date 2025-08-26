package kr.bi.greenmate.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.cloud.aws.s3")
public class ObjectStorageProperties {
	private final String bucket;
	private final String cdnUrl;
}
