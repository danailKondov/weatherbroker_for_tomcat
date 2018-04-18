package com.bellintegrator.weatherbrokertomcat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SpringBootApplication
@EnableJms
@EnableWebMvc
@Configuration
@ComponentScan("com.bellintegrator.weatherbrokertomcat")
@PropertySource("classpath:application.properties")
public class WeatherbrokerTomcatApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WeatherbrokerTomcatApplication.class, args);
	}

	@Autowired
	private Environment environment;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WeatherbrokerTomcatApplication.class);
	}

	@Bean
	RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		ObjectMapper mapper = new ObjectMapper();
//		mapper.setDateFormat(df);
		converter.setObjectMapper(mapper);
		restTemplate.getMessageConverters().add(converter);
		return restTemplate;
	}
/*
	@Bean
	public InternalResourceViewResolver jspViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/");
		//viewResolver.setSuffix(".jsp");
		viewResolver.setContentType("text/html");
//		viewResolver.setOrder(1000);
		return viewResolver;
	}*/

	@Bean
	public ActiveMQConnectionFactory connectionFactory(){
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(environment.getProperty("spring.activemq.broker-url"));
		connectionFactory.setPassword(environment.getProperty("spring.activemq.password"));
		connectionFactory.setUserName(environment.getProperty("spring.activemq.user"));
//		connectionFactory.setTrustedPackages(new ArrayList(Arrays.asList((
//				"com.bellintegrator.weatherbroker.model.ForecastForDay," +
//				"com.bellintegrator.weatherbroker.model.WeatherForecast," +
//				"com.bellintegrator.weatherbroker.model.WeatherCondition")
//				.split(","))));
		connectionFactory.setTrustAllPackages(true);
		// When true the consumer will check for duplicate messages and properly
		// handle the message to make sure that it is not processed twice inadvertently.
		connectionFactory.setCheckForDuplicates(true);
		// The size of the message window that will be audited for duplicates and out of order messages.
		connectionFactory.setAuditDepth(10000);
		// Maximum number of producers that will be audited.
		connectionFactory.setAuditMaximumProducerNumber(128);
		return connectionFactory;
	}

	@Bean
	public JmsTemplate jmsTemplate(){
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(connectionFactory());
		template.setPubSubDomain(true); // pub/sub
		return template;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
//		factory.setConcurrency("1-1");
		factory.setPubSubDomain(true); // pub/sub
		return factory;
	}

	@Bean
	@Description("Thymeleaf template resolver serving HTML 5")
	public ClassLoaderTemplateResolver templateResolver() {

		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

		templateResolver.setPrefix("templates/");
		templateResolver.setCacheable(false);
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCharacterEncoding("UTF-8");

		return templateResolver;
	}

	@Bean
	@Description("Thymeleaf template engine with Spring integration")
	public SpringTemplateEngine templateEngine() {

		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());

		return templateEngine;
	}

	@Bean
	@Description("Thymeleaf view resolver")
	public ViewResolver viewResolver() {

		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();

		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setCharacterEncoding("UTF-8");

		return viewResolver;
	}

}
