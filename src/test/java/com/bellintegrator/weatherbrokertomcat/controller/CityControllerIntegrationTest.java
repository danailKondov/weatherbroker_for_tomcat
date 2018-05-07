package com.bellintegrator.weatherbrokertomcat.controller;

import com.bellintegrator.weatherbrokertomcat.WeatherbrokerTomcatApplication;
import com.bellintegrator.weatherbrokertomcat.dao.WeatherConditionRepository;
import com.bellintegrator.weatherbrokertomcat.dao.WeatherForecastRepository;
import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import com.bellintegrator.weatherbrokertomcat.model.WeatherForecast;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = WeatherbrokerTomcatApplication.class // !!!
)
public class CityControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WeatherConditionRepository conditionRepository;

    @Autowired
    private WeatherForecastRepository forecastRepository;

    private HttpHeaders headers;

    @Before
    public void init() {
        conditionRepository.deleteAll();
        forecastRepository.deleteAll();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * Тест проверяет работу контроллера, сервиса и JMS-брокера.
     * Система должна принять в виде параметра имя города, послать запрос на Yahoo,
     * получить ответ, переслать его по JMS и сохранить в БД.
     *
     * Для выполнения теста необходимо поднять JMS-брокер ActiveMQ
     * со стандартными настройками.
     */
    @Test
    public void getActualWeatherForCityWhenSuccessfulTest() throws InterruptedException {

        HttpEntity<Map> entity = new HttpEntity<>(headers);

        assertTrue(Lists.newArrayList(conditionRepository.findAll()).isEmpty()); // проверяем, что БД пустая

        restTemplate.exchange("/get/cityname?cityName=Moscow&degreeParam=celsius&typeInfo=current", HttpMethod.GET, entity, String.class);

        Thread.sleep(6000); // ждем, пока делается запрос, пересылается сообщение и сохраняется в БД

        List<WeatherCondition> list = Lists.newArrayList(conditionRepository.findAll());
        assertFalse(list.isEmpty());
        WeatherCondition condition = list.get(0);
        assertEquals(condition.getCity(), "Moscow");
    }

    @Test
    public void getWeatherForecastForCityWhenSuccessfulTest() throws InterruptedException {
        HttpEntity<Map> entity = new HttpEntity<>(headers);

        assertTrue(Lists.newArrayList(forecastRepository.findAll()).isEmpty());

        restTemplate.exchange("/get/cityname?cityName=Moscow&degreeParam=celsius&typeInfo=forecast", HttpMethod.GET, entity, String.class);

        Thread.sleep(6000);

        List<WeatherForecast> list = Lists.newArrayList(forecastRepository.findAll());
        assertFalse(list.isEmpty());
        WeatherForecast forecast = list.get(0);
        assertEquals(forecast.getCity(), "Moscow");
    }

    @Test
    public void getActualWeatherFromDbWhenSuccessful() {
        HttpEntity<Map> entity = new HttpEntity<>(headers);
        WeatherCondition condition = new WeatherCondition();
        condition.setCity("Moscow");
        conditionRepository.save(condition);
        long id = conditionRepository.findWeatherConditionsByCity("Moscow").get(0).getId();

        ResponseEntity<String> response = restTemplate.exchange("/get/actual/Moscow", HttpMethod.GET, entity, String.class);

        String expected = "[{\"id\":" + id + "," +
                "\"city\":\"Moscow\"," +
                "\"date\":null," +
                "\"temp\":null," +
                "\"tempType\":null," +
                "\"description\":null}]";
        String result = response.getBody();
        assertThat(result, is(expected));
    }

    @Test
    public void getWeatherForecastFromDbWhenSuccessfulTest() {
        HttpEntity<Map> entity = new HttpEntity<>(headers);
        WeatherForecast forecast = new WeatherForecast();
        forecast.setCity("Moscow");
        forecastRepository.save(forecast);
        long id = forecastRepository.getWeatherForecastsByCity(forecast.getCity()).get(0).getId();

        ResponseEntity<String> response = restTemplate.exchange("/get/forecast/Moscow", HttpMethod.GET, entity, String.class);

        String expected = "[{\"id\":" + id + "," +
                "\"date\":null," +
                "\"city\":\"Moscow\"," +
                "\"tempType\":null," +
                "\"forecasts\":[]}]";
        String result = response.getBody();
        assertThat(result, is(expected));
    }

    @After
    public void destroy() {
        conditionRepository.deleteAll();
        forecastRepository.deleteAll();
    }

}