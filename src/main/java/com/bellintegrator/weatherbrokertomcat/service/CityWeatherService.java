package com.bellintegrator.weatherbrokertomcat.service;

import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;

import java.util.List;

public interface CityWeatherService {

    /**
     * Метод получает параметры для запроса прогноза погоды или актуального состояния и
     * помещает результат в JMS очередь.
     *
     * @param cityName имя города
     * @param degreeParam единицы измерения температуры
     * @param typeInfo прогноз погоды или актуальное состояние
     */
    void getWeatherForCity(String cityName, String degreeParam, String typeInfo);

    /**
     * Метод получает список актуальной информации по погоде в городе в разное время.
     *
     * @param cityName имя города
     * @return список с информацией о погоде
     */
    List<WeatherCondition> getActualWeatherFromDbForCity(String cityName);

    /**
     * Метод получает список с прогнозами погоды в городе, сделанными в разное время.
     *
     * @param cityName имя города
     * @return список с прогнозами
     */
    List<WeatherForecast> getForecastFromDbForCity(String cityName);
}
