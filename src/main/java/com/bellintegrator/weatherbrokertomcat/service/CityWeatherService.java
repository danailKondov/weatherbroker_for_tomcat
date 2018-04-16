package com.bellintegrator.weatherbrokertomcat.service;

public interface CityWeatherService {

    void getWeatherForCity(String cityName, String degreeParam, String typeInfo);
}
