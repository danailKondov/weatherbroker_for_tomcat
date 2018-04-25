package com.bellintegrator.weatherbrokertomcat.service;

import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;

import java.util.List;

public interface CityWeatherService {

    void getWeatherForCity(String cityName, String degreeParam, String typeInfo);

    List<WeatherCondition> getActualWeatherFromDbForCity(String cityName);

    List<WeatherForecast> getForecastFromDbForCity(String cityName);
}
