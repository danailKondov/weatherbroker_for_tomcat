package com.bellintegrator.weatherbrokertomcat.controller;

import com.bellintegrator.weatherbrokertomcat.exceptionhandler.exceptions.WeatherException;
import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;
import com.bellintegrator.weatherbrokertomcat.service.CityWeatherService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CityController.class)
public class CityControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CityWeatherService service;

    @Before
    public void init() {
        List<WeatherCondition> list = new ArrayList<>();
        WeatherCondition condition = new WeatherCondition();
        condition.setCity("Moscow");
        list.add(condition);
        when(service.getActualWeatherFromDbForCity("Moscow")).thenReturn(list);

        doThrow(new WeatherException("Wrong city name!"))
                .when(service)
                .getWeatherForCity("NoSuchCity", "celsius", "current");

        List<WeatherForecast> forecasts = new ArrayList<>();
        WeatherForecast forecast = new WeatherForecast();
        forecast.setCity("Moscow");
        forecasts.add(forecast);
        when(service.getForecastFromDbForCity("Moscow")).thenReturn(forecasts);
    }

    @Test
    public void getWeatherForCityTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/get/cityname")
                .param("cityName", "Moscow")
                .param("degreeParam", "celsius")
                .param("typeInfo", "current"))
                .andExpect(status().isOk());
        verify(service).getWeatherForCity("Moscow", "celsius", "current");
    }

    @Test
    public void getActualWeatherWhenNoSuchCityTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/get/cityname")
                .param("cityName", "NoSuchCity")
                .param("degreeParam", "celsius")
                .param("typeInfo", "current"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("exception", hasProperty("message", is("Wrong city name!"))))
                .andExpect(view().name("/index.html"))
                .andExpect(content().string(Matchers.containsString("Wrong city name!")))
                .andDo(print());
    }

    @Test
    public void getActualWeatherTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/get/actual/{cityName}", "Moscow")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].city", is("Moscow")))
                .andDo(print());
    }

    @Test
    public void getWeatherForecastTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/get/forecast/{cityName}", "Moscow")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].city", is("Moscow")))
                .andDo(print());
    }
}