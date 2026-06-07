package com.svesh.course_work.api;

import java.util.Map;
import java.util.Optional;

public class OpenMeteoAPI implements ApiDefinition {
    @Override
    public String getApiName() {
        return "open-meteo";
    }

    @Override
    public String getApiURL() {
        return "https://api.open-meteo.com";
    }

    @Override
    public Optional<String> getDefaultEndpoint() {
        return Optional.of("v1/forecast");
    }

    @Override
    public Map<String, String> getDefaultPathParams() {
        return Map.of();
    }

    @Override
    public Map<String, String> getDefaultQueryParams() {
        return Map.of(
                "latitude", "52.97",
                "longitude", "35.91",
                "current", "temperature_2m"
        );
    }

    @Override
    public String getInstruction() {
        return """
                Open-Meteo API simple manual
                
                Description:
                    Provides weather information.
                
                Examples of endpoints:
                    v1/forecast
                    v1/marine
                
                Request parameters for forecast:
                    latitude
                    longitude
                    current     //extracted weather values
                
                Parameter format:
                    parameter=value
                
                Example:
                    v1/forecast
                    latitude=50.03
                    longitude=23.56
                    current=temperature_2m,wind_speed_10m
                
                Example of values for the "current" parameter of forecast:
                    - temperature_2m        //The air temperature at a height of 2 meters
                    - wind_speed_10m        //Wind speed at a height of 10 meters
                    - relative_humidity_2m  //Relative humidity at a height of 2 meters
                    - cloud_cover           //Percentage of clouds
                    - precipitation         //The amount of precipitation
                
                Default request:
                    v1/forecast
                    latitude=52.97
                    longitude=35.91
                    current=temperature_2m
                
                Docs:
                    https://open-meteo.com/en/docs
                """;
    }
}
