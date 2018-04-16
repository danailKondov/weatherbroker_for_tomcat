package com.bellintegrator.weatherbrokertomcat.dao;

import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;
import org.springframework.data.repository.CrudRepository;

public interface WeatherForecastRepository extends CrudRepository<WeatherForecast, Long> {
}
