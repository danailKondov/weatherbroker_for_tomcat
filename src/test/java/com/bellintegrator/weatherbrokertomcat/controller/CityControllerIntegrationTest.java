package com.bellintegrator.weatherbrokertomcat.controller;

import com.bellintegrator.weatherbrokertomcat.WeatherbrokerTomcatApplication;
import com.bellintegrator.weatherbrokertomcat.dao.WeatherConditionRepository;
import com.bellintegrator.weatherbrokertomcat.dao.WeatherForecastRepository;
import com.bellintegrator.weatherbrokertomcat.model.WeatherCondition;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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

        restTemplate.exchange("/cityname?cityName=Moscow&degreeParam=celsius&typeInfo=current", HttpMethod.GET, entity, String.class);

        Thread.sleep(6000); // ждем, пока делается запрос, пересылается сообщение и сохраняется в БД

        List<WeatherCondition> list = Lists.newArrayList(conditionRepository.findAll());

        assertFalse(list.isEmpty());
        WeatherCondition condition = list.get(0);
        assertEquals(condition.getCity(), "Moscow");
    }

    @After
    public void destroy() {
        conditionRepository.deleteAll();
        forecastRepository.deleteAll();
    }

}