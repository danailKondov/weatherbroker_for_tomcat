package com.bellintegrator.weatherbrokertomcat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
//@EnableEurekaClient
public class WeatherbrokerTomcatApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WeatherbrokerTomcatApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WeatherbrokerTomcatApplication.class);
	}

	@Bean
	RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//		SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy");
		ObjectMapper mapper = new ObjectMapper();
//		mapper.setDateFormat(df);
		converter.setObjectMapper(mapper);
//		restTemplate.getMessageConverters().clear(); // без этого будут работать конвертеры по умолчанию и да
		restTemplate.getMessageConverters().add(converter);
		return restTemplate;
	}
}
