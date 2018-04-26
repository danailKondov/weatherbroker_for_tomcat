package com.bellintegrator.weatherbrokertomcat.jms;

import com.bellintegrator.weatherbrokertomcat.dao.WeatherConditionRepository;
import com.bellintegrator.weatherbrokertomcat.dao.WeatherForecastRepository;
import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class WeatherJmsListener {

    private final Logger log = LoggerFactory.getLogger(WeatherJmsListener.class);

    private WeatherConditionRepository actualConditionRepository;
    private WeatherForecastRepository forecastRepository;

    @Autowired
    public WeatherJmsListener(WeatherConditionRepository actualConditionRepository, WeatherForecastRepository forecastRepository) {
        this.actualConditionRepository = actualConditionRepository;
        this.forecastRepository = forecastRepository;
    }

//    @JmsListener(destination = "weatherCondition.topic")
    @JmsListener(destination = "actual")
    @Transactional
    public void receiveWeatherCondition(final WeatherCondition condition) {
        log.info("Jms listener received WeatherCondition");
        actualConditionRepository.save(condition);
        log.info("WeatherCondition was saved in DB");
    }

//    @JmsListener(destination = "weatherForecast.topic")
    @JmsListener(destination = "forecast")
    @Transactional
    public void receiveWeatherForecast(final WeatherForecast forecast) {
        log.info("Jms listener received WeatherForecast");
        forecastRepository.save(forecast);
        log.info("WeatherForecast was saved in DB");
    }
}
