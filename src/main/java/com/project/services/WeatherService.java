package com.project.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.api.response.WeatherResponse;
import com.project.cache.AppCache;
import com.project.constants.PlaceHolders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final AppCache appCache;
    private final RedisService redisService;

    @Autowired
    public WeatherService(RestTemplate restTemplate, AppCache appCache, RedisService redisService) {
        this.restTemplate = restTemplate;
        this.appCache = appCache;
        this.redisService = redisService;
    }

    public WeatherResponse getWeather(String city) {
        try {
            WeatherResponse weatherResponse = redisService.getCache("weather_of_" + city, WeatherResponse.class);
            if (weatherResponse != null){
                return weatherResponse;
            } else {
                String finalApi = appCache.cache.get(AppCache.keys.WEATHER_API.toString()).replace(PlaceHolders.CITY, city).replace(PlaceHolders.API_KEY, apiKey);
                ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class);
                if (response.getBody() != null){
                    redisService.setCache("weather_of_" + city, response.getBody(), 300L);
                }
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("Error occurred with fetching: ", e);
            throw new RuntimeException("Failed to fetch weather data", e);
        }
    }
}
