package com.bellintegrator.weatherbrokertomcat.dao;

import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import org.springframework.data.repository.CrudRepository;

public interface WeatherConditionRepository extends CrudRepository<WeatherCondition, Long> {
}
