package com.bellintegrator.weatherbrokertomcat.controller;

public interface CityController {
    void getWeatherForCity(String cityName, String degreeParam, String typeInfo);
}
