package org.komytron.weatherforecast.backend.ws.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.komytron.weatherforecast.backend.domain.Forecast;
import org.komytron.weatherforecast.backend.domain.RainProbability;
import org.komytron.weatherforecast.backend.domain.TempUnit;
import org.komytron.weatherforecast.backend.ws.client.aemet.AemetOpenDataClient;
import org.komytron.weatherforecast.backend.ws.client.aemet.domain.ForecastResponse;
import org.komytron.weatherforecast.backend.ws.exception.UnknownErrorException;
import org.komytron.weatherforecast.backend.ws.mapper.ForecastMapper;
import org.komytron.weatherforecast.backend.ws.service.ForecastService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastServiceImpl implements ForecastService {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("#{'${aemet.filter.period.values}'.split(',')}")
    private List<String> periodFilter;
    @Value("${aemet.filter.period.enabled:false}")
    private Boolean periodFilterEnabled;

    private final AemetOpenDataClient aemetOpenDataClient;
    private final ForecastMapper forecastMapper;

    @Override
    public Forecast getTomorrowForecast(String municipality, TempUnit tempUnit) {
        String identifier = !StringUtils.isNumeric(municipality) ? municipality.replaceAll("[^\\d.]", "") : municipality;
        log.info("Identifier to call aemet " + identifier);
        ForecastResponse forecastResponse = Optional.ofNullable(aemetOpenDataClient.getDailyForecast(identifier)
                .map(response -> {
                    try {
                        return JSON_MAPPER.readValue(response,  new TypeReference<List<ForecastResponse>>(){});
                    } catch (JsonProcessingException e) {
                        throw new UnknownErrorException("Error parsing json", e);
                    }
                })
                .block()).orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .get();

        ForecastResponse.Day day = Optional.ofNullable(forecastResponse)
                .map(response -> response.getPrediccion().getDia()
                        .stream()
                        .filter(d -> isTomorrow(d.getFecha()))
                        .findFirst()
                        .get()
                ).get();

        return Forecast.builder()
                .date(day.getFecha())
                .temperatureAverage(this.calculateTemperatureAvg(day, tempUnit))
                .temperatureUnit(tempUnit)
                .rainProbabilitiy(this.filterPeriod(forecastMapper.toRainProbabilities(day.getProbPrecipitacion())))
                .build();
    }

    private Float calculateTemperatureAvg(ForecastResponse.Day day, TempUnit tempUnit){

        Double sumTemp = Optional.ofNullable(day.getTemperatura().getDato()).orElse(new ArrayList<>()).stream()
                .filter(temp -> StringUtils.isNumeric(temp.getValue()))
                .mapToDouble(temp -> {
                    double temperature = Double.valueOf(temp.getValue());
                    if(tempUnit.equals(TempUnit.G_FAH)){
                        temperature = celsiusToFahrenheit(temperature);
                    }
                    return temperature;
                })
                .sum();

        Float result = null;
        if(sumTemp != null){
            result = (float) (sumTemp / day.getTemperatura().getDato().size());
        }
        return result;

    }

    private boolean isTomorrow(final Date date){
        Calendar calendarDate = Calendar.getInstance();
        Calendar calendarTomorrow = Calendar.getInstance();
        calendarDate.setTime(date);
        calendarTomorrow.setTime(getTomorrowDay());
        return (calendarDate.get(Calendar.YEAR) == calendarTomorrow.get(Calendar.YEAR)
                &&
                calendarDate.get(Calendar.MONTH) == calendarTomorrow.get(Calendar.MONTH)
                &&
                calendarDate.get(Calendar.DAY_OF_YEAR) == calendarTomorrow.get(Calendar.DAY_OF_YEAR));
    }

    private Date getTomorrowDay(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    private double celsiusToFahrenheit(double celsius) {
        return (celsius * 1.8f) + 32;
    }

    private List<RainProbability> filterPeriod(List<RainProbability> rainProbabilities){
        if(periodFilterEnabled){
            return Optional.ofNullable(rainProbabilities).orElse(new ArrayList<>())
                    .stream()
                    .filter(rainProbability -> periodFilter.contains(rainProbability.getPeriod()))
                    .collect(Collectors.toList());
        }
        return rainProbabilities;
    }

}
