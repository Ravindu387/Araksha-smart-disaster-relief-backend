package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.WeatherDTO;

public interface WeatherService {
    WeatherDTO getWeather(String city);
    WeatherDTO getWeather(Double lat, Double lon);
}
