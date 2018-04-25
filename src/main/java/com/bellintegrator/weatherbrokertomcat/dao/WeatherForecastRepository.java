package com.bellintegrator.weatherbrokertomcat.dao;

import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WeatherForecastRepository extends CrudRepository<WeatherForecast, Long> {
    List<WeatherForecast> getWeatherForecastsByCity(String cityName);
}
