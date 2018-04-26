package com.bellintegrator.weatherbrokertomcat.jms;

import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Destination;

@Component
public class WeatherJmsProducer {

    private final Logger log = LoggerFactory.getLogger(WeatherJmsProducer.class);

    private JmsTemplate jmsTemplate;

    @Resource(lookup = "forecastweather")
    private Destination forecastDestination;

    @Resource(lookup = "actualweather")
    private Destination actualDestination;

    @Autowired
    public WeatherJmsProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendActualWeather(final WeatherCondition condition) {
        log.info("sending actual weather in JMS...");
        jmsTemplate.convertAndSend(actualDestination, condition);
        log.info("actual weather was send");
    }

    public void sendWeatherForecast(final WeatherForecast forecast) {
        log.info("sending forecast weather in JMS...");
        jmsTemplate.convertAndSend(forecastDestination, forecast);
        log.info("forecast was send");
    }
}
