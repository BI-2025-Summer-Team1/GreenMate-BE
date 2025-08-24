package kr.bi.greenmate.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	private final MultipartJackson2HttpMessageConverter converter;

	public WebMvcConfig(MultipartJackson2HttpMessageConverter converter) {
		this.converter = converter;
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, converter);
	}
}
