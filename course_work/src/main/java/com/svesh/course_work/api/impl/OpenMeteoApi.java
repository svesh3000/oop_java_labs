package com.svesh.course_work.api.impl;

import com.svesh.course_work.api.ApiDefinition;

import java.util.Map;

public class OpenMeteoApi implements ApiDefinition {
    @Override
    public String getApiName() {
        return "open-meteo";
    }

    @Override
    public String getApiUrl() {
        return "https://api.open-meteo.com/v1/forecast";
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
                
                Request parameters for forecast:
                    latitude
                    longitude
                    current     //extracted weather values
                
                Parameter format:
                    parameter=value
                
                Example:
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
                    latitude=52.97
                    longitude=35.91
                    current=temperature_2m
                """;
    }
}
