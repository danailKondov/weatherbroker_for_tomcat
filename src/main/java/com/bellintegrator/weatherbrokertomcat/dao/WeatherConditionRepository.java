package com.bellintegrator.weatherbrokertomcat.dao;

import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WeatherConditionRepository extends CrudRepository<WeatherCondition, Long> {
    List<WeatherCondition> findWeatherConditionsByCity(String cityName);
}
