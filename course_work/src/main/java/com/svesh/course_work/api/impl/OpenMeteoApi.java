package com.svesh.course_work.api.impl;

import com.svesh.course_work.api.ApiDefinition;
import com.svesh.course_work.api.ParamSpec;

import java.util.Map;
import java.util.Set;

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
    public Map<String, ParamSpec> getParamSpecs() {
        return Map.of(
                "latitude", ParamSpec.freeRequired(),
                "longitude", ParamSpec.freeRequired(),
                "current", ParamSpec.optionalMulti(Set.of(
                        "temperature_2m", "wind_speed_10m", "relative_humidity_2m",
                        "cloud_cover", "precipitation"))
        );
    }

    @Override
    public Map<String, String> getDefaultQueryParams() {
        return Map.of(
                "latitude", "52.97",
                "longitude", "35.91"
        );
    }

    @Override
    public String getInstruction() {
        return """
                Open-Meteo API simple manual
                
                Description:
                    Provides weather information.
                
                Request parameters for forecast:
                    latitude    //(required parameter)
                    longitude   //(required parameter)
                    current     //(optional parameter) extracted weather values
                
                Parameter format:
                    parameter=value
                
                Required parameters (default values used if not specified):
                    latitude=52.97
                    longitude=35.91
                
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
                """;
    }
}
