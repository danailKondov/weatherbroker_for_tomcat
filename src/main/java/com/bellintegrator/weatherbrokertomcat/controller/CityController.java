package com.bellintegrator.weatherbrokertomcat.controller;

import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;
import com.bellintegrator.weatherbrokertomcat.service.CityWeatherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/get")
@Api(value = "weatherbroker", description = "Операции для запроса погоды на Yahoo и получения готовых прогнозов из БД")
public class CityController {

    private final Logger log = LoggerFactory.getLogger(CityController.class);

    private CityWeatherService service;

    @Autowired
    public CityController(CityWeatherService service) {
        this.service = service;
    }

    /**
     * Метод обрабатывает запрос на получение погоды.
     *
     * @param cityName имя города
     * @param degreeParam единицы измерения температуры
     * @param typeInfo прогноз погоды или актуальное состояние
     */
    @ApiOperation(value = "Метод обрабатывает запрос на получение погоды")
    @GetMapping(value = "/cityname")
    public void getWeatherForCity(@RequestParam("cityName") String cityName, @RequestParam("degreeParam") String degreeParam, @RequestParam("typeInfo") String typeInfo) {
        log.info("New request for weather with params: " + cityName + "; " + degreeParam + "; " + typeInfo);
        service.getWeatherForCity(cityName, degreeParam, typeInfo);
    }

    /**
     * Метод возвращает актуальную погоду в городе.
     *
     * @param cityName имя города
     * @return список с актуальной на разное время погодой
     */
    @ApiOperation(value = "Метод возвращает актуальную погоду в городе", response = List.class)
    @GetMapping(value = "/actual/{cityName}", produces = "application/json") // "produces" is for swagger
    public ResponseEntity<List<WeatherCondition>> getActualWeather(@PathVariable String cityName) {
        log.info("Request for actual weather from DB for city: " + cityName);
        List<WeatherCondition> conditions = service.getActualWeatherFromDbForCity(cityName);
        log.info("Got actual weather: " + conditions);
        return new ResponseEntity<>(conditions, HttpStatus.FOUND);
    }

    /**
     * Метод возвращает прогноз погоды в городе.
     *
     * @param cityName имя города
     * @return список с прогнозами погоды, сделанными в разное время
     */
    @ApiOperation(value = "Метод возвращает прогноз погоды в городе", response = List.class)
    @GetMapping(value = "/forecast/{cityName}", produces = "application/json") // "produces" is for swagger
    public ResponseEntity<List<WeatherForecast>> getWeatherForecast(@PathVariable String cityName) {
        log.info("Request for weather forecast from DB for city: " + cityName);
        List<WeatherForecast> forecasts = service.getForecastFromDbForCity(cityName);
        log.info("Got forecast: " + forecasts);
        return new ResponseEntity<>(forecasts, HttpStatus.FOUND);
    }
}
