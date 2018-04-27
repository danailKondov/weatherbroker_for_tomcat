package com.bellintegrator.weatherbrokertomcat.service.impl;

import com.bellintegrator.weatherbrokertomcat.dao.WeatherConditionRepository;
import com.bellintegrator.weatherbrokertomcat.dao.WeatherForecastRepository;
import com.bellintegrator.weatherbrokertomcat.exceptionhandler.exceptions.WeatherException;
import com.bellintegrator.weatherbrokertomcat.jms.WeatherJmsProducer;
import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;
import com.bellintegrator.weatherbrokertomcat.views.actual.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class CityWeatherServiceImplTest {

    @Mock
    private WeatherJmsProducer jmsProducer;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherConditionRepository actualConditionRepository;

    @Mock
    private WeatherForecastRepository forecastRepository;

    @InjectMocks
    private CityWeatherServiceImpl weatherService;

    @Test
    public void getWeatherForCityWhenSuccessfulTest() {

        WeatherActualView view = getView();
        ResponseEntity result = new ResponseEntity<>(view, HttpStatus.FOUND);
        when(restTemplate.getForEntity(anyString(), any(), anyMap())).thenReturn(result);


        weatherService.getWeatherForCity("Moscow", "celsius", "current");

        WeatherCondition condition = new WeatherCondition(view, "Moscow", "celsius");
        verify(jmsProducer).sendActualWeather(condition);
    }

    private WeatherActualView getView() {
        Condition condition = new Condition();
        condition.setCode("32");
        condition.setDate("Mon, 09 Apr 2018 01:00 PM MSK");
        condition.setTemp(16);
        condition.setText("Sunny");
        Item item = new Item();
        item.setCondition(condition);
        Channel channel = new Channel();
        channel.setItem(item);
        Results results = new Results();
        results.setChannel(channel);
        Query query = new Query();
        query.setResults(results);
        WeatherActualView view = new WeatherActualView();
        view.setQuery(query);
        return view;
    }

    @Test(expected = WeatherException.class)
    public void getWeatherForCityWhenWrongTypeInfoTest() {
        weatherService.getWeatherForCity("Moscow", "celsius", "wrongType");
    }

    @Test(expected = WeatherException.class)
    public void getWeatherForCityWhenWrongCityNameTest() {
        WeatherActualView view = new WeatherActualView();
        Query query = new Query();
        view.setQuery(query);
        ResponseEntity result = new ResponseEntity<>(view, HttpStatus.FOUND);
        when(restTemplate.getForEntity(anyString(), any(), anyMap())).thenReturn(result);

        weatherService.getWeatherForCity("noSuchCity", "celsius", "current");
    }

    @Test
    public void getActualWeatherFromDBbyNameWhenSuccessfulTest() {
        List<WeatherCondition> list = new ArrayList<>();
        list.add(new WeatherCondition());
        when(actualConditionRepository.findWeatherConditionsByCity("moscow")).thenReturn(list);

        List<WeatherCondition> result = weatherService.getActualWeatherFromDbForCity("moscow");

        assertEquals(list, result);
    }

    @Test(expected = WeatherException.class)
    public void getActualWeatherFromDBbyNameWhenNoCityTest() {
        List<WeatherCondition> list = new ArrayList<>();
        when(actualConditionRepository.findWeatherConditionsByCity("no city in DB")).thenReturn(list);

        weatherService.getActualWeatherFromDbForCity("no city in DB");
    }

    @Test
    public void getWeatherForecastFromDBbyNameWhenSuccessfulTest() {
        List<WeatherForecast> list = new ArrayList<>();
        list.add(new WeatherForecast());
        when(forecastRepository.getWeatherForecastsByCity("moscow")).thenReturn(list);

        List<WeatherForecast> result = weatherService.getForecastFromDbForCity("moscow");

        assertEquals(list, result);
    }

    @Test(expected = WeatherException.class)
    public void getWeatherForecastFromDBbyNameWhenNoCityTest() {
        List<WeatherForecast> list = new ArrayList<>();
        when(forecastRepository.getWeatherForecastsByCity("moscow")).thenReturn(list);

        weatherService.getForecastFromDbForCity("moscow");
    }

}