package com.bellintegrator.weatherbrokertomcat.config;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JmsConfig {

    @Value("${spring.activemq.broker-url}")
    String brokerUrl;
    @Value("${spring.activemq.password}")
    String password;
    @Value("${spring.activemq.user}")
    String userName;

    @Bean
    public ActiveMQXAConnectionFactory connectionFactory(){
        ActiveMQXAConnectionFactory connectionFactory = new ActiveMQXAConnectionFactory(); // instead of ActiveMQConnectionFactory for non-XA
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setPassword(password);
        connectionFactory.setUserName(userName);
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
        // redelivery policy
        RedeliveryPolicy redeliveryPolicy = connectionFactory.getRedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(10 * 1000);
        redeliveryPolicy.setMaximumRedeliveryDelay(10 * 1000);
        redeliveryPolicy.setMaximumRedeliveries(2);
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setSessionTransacted(true); // for XATransaction with Atomikos
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
}
