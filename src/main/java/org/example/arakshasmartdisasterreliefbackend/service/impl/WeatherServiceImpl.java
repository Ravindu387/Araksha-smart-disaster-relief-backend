package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.dto.WeatherDTO;
import org.example.arakshasmartdisasterreliefbackend.service.GeocodingService;
import org.example.arakshasmartdisasterreliefbackend.service.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    @Value("${openweathermap.api.key:YOUR_OPENWEATHERMAP_API_KEY}")
    private String apiKey;

    private final GeocodingService geocodingService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WeatherDTO getWeather(String city) {
        log.info("Fetching weather for city: {}", city);

        if (city == null || city.trim().isEmpty()) {
            city = "Colombo";
        }

        if (isApiKeyMissing()) {
            return generateMockWeather(city);
        }

        int retries = 2;
        while (retries >= 0) {
            try {
                String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s,LK&appid=%s&units=metric",
                        city.trim(), apiKey);
                Map<?, ?> response = restTemplate.getForObject(url, Map.class);
                if (response != null) {
                    return mapResponseToWeatherDTO(response, city);
                }
                break;
            } catch (Exception e) {
                log.warn("OpenWeatherMap city lookup failed for: {}, retrying (retries left: {})", city, retries, e);
                retries--;
            }
        }

        return generateMockWeather(city);
    }

    @Override
    public WeatherDTO getWeather(Double lat, Double lon) {
        log.info("Fetching weather for coordinates: {}, {}", lat, lon);

        if (lat == null || lon == null) {
            return generateMockWeather("Colombo");
        }

        if (isApiKeyMissing()) {
            String city = geocodingService.reverseGeocode(lat, lon);
            if (city.contains(",")) {
                city = city.split(",")[0];
            }
            return generateMockWeather(city);
        }

        int retries = 2;
        while (retries >= 0) {
            try {
                String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric",
                        lat, lon, apiKey);
                Map<?, ?> response = restTemplate.getForObject(url, Map.class);
                if (response != null) {
                    String cityName = (String) response.get("name");
                    return mapResponseToWeatherDTO(response, cityName != null ? cityName : "Disaster Location");
                }
                break;
            } catch (Exception e) {
                log.warn("OpenWeatherMap coordinate lookup failed, retrying (retries left: {})", retries, e);
                retries--;
            }
        }

        String fallbackCity = geocodingService.reverseGeocode(lat, lon);
        if (fallbackCity.contains(",")) {
            fallbackCity = fallbackCity.split(",")[0];
        }
        return generateMockWeather(fallbackCity);
    }

    private boolean isApiKeyMissing() {
        return apiKey == null || apiKey.trim().isEmpty() || "YOUR_OPENWEATHERMAP_API_KEY".equals(apiKey);
    }

    private WeatherDTO mapResponseToWeatherDTO(Map<?, ?> response, String city) {
        Map<?, ?> main = (Map<?, ?>) response.get("main");
        Map<?, ?> wind = (Map<?, ?>) response.get("wind");
        java.util.List<?> weatherList = (java.util.List<?>) response.get("weather");
        Map<?, ?> weather = (Map<?, ?>) weatherList.getFirst();
        Map<?, ?> clouds = (Map<?, ?>) response.get("clouds");

        Double temp = ((Number) main.get("temp")).doubleValue();
        Double humidity = ((Number) main.get("humidity")).doubleValue();
        Double windSpeed = ((Number) wind.get("speed")).doubleValue();
        String desc = (String) weather.get("description");
        
        // Clouds percentage as basic probability fallback
        double cloudiness = clouds != null ? ((Number) clouds.get("all")).doubleValue() : 0.0;
        
        // Calculate a reasonable rain probability
        double rainProb = 0.0;
        if (desc.toLowerCase().contains("thunderstorm") || desc.toLowerCase().contains("heavy rain")) {
            rainProb = 95.0;
        } else if (desc.toLowerCase().contains("rain")) {
            rainProb = 80.0;
        } else if (desc.toLowerCase().contains("drizzle")) {
            rainProb = 60.0;
        } else if (desc.toLowerCase().contains("cloud")) {
            rainProb = Math.min(cloudiness, 50.0);
        } else {
            rainProb = 5.0;
        }

        // Generate warning messages based on wind speed, temperature, and rain
        String warning = "None";
        if (windSpeed > 15.0) {
            warning = "Gale alert: high wind speeds detected. Secure loose structures.";
        } else if (rainProb > 80.0) {
            warning = "Flood warning: persistent rain likely to cause water accumulation.";
        } else if (temp > 35.0) {
            warning = "Heat advisory: limit physical activity outdoors.";
        }

        return WeatherDTO.builder()
                .city(city)
                .temperature(temp)
                .description(desc)
                .rainProbability(rainProb)
                .windSpeed(windSpeed)
                .humidity(humidity)
                .warnings(warning)
                .build();
    }

    private WeatherDTO generateMockWeather(String city) {
        log.info("Generating mock weather report for city: {}", city);
        String cleanCity = city.trim().toLowerCase();
        
        double temp = 29.5;
        double rainProb = 40.0;
        double wind = 10.5;
        double humidity = 70.0;
        String desc = "Partly Cloudy";
        String warnings = "None";

        if (cleanCity.contains("colombo") || cleanCity.contains("gampaha") || cleanCity.contains("kalutara")) {
            temp = 31.0;
            rainProb = 65.0;
            wind = 12.0;
            humidity = 82.0;
            desc = "Scattered Showers";
            warnings = "Minor rain alert: local water logging possible in low-lying roads.";
        } else if (cleanCity.contains("kandy") || cleanCity.contains("matale") || cleanCity.contains("nuwara eliya")) {
            temp = cleanCity.contains("nuwara") ? 17.5 : 24.0;
            rainProb = 50.0;
            wind = 8.0;
            humidity = 78.0;
            desc = "Mist and Light Rain";
            warnings = "Low visibility advisory: heavy fog in mountainous passes.";
        } else if (cleanCity.contains("galle") || cleanCity.contains("matara") || cleanCity.contains("hambantota")) {
            temp = 29.8;
            rainProb = 20.0;
            wind = 22.0; // km/h
            humidity = 75.0;
            desc = "Windy and Clear";
            warnings = "High sea swell warning: coastal small crafts advised to proceed with caution.";
        } else if (cleanCity.contains("jaffna") || cleanCity.contains("mannar") || cleanCity.contains("anuradhapura")) {
            temp = 34.5;
            rainProb = 10.0;
            wind = 15.0;
            humidity = 60.0;
            desc = "Sunny and Dry";
            warnings = "Heat index warning: high temperature levels; ensure volunteers have adequate shade and water.";
        }

        return WeatherDTO.builder()
                .city(city)
                .temperature(temp)
                .description(desc)
                .rainProbability(rainProb)
                .windSpeed(wind)
                .humidity(humidity)
                .warnings(warnings)
                .build();
    }
}
