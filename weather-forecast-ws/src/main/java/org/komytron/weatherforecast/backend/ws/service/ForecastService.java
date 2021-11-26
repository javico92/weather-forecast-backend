package org.komytron.weatherforecast.backend.ws.service;

import org.komytron.weatherforecast.backend.domain.Forecast;
import org.komytron.weatherforecast.backend.domain.TempUnit;

public interface ForecastService {

    Forecast getTomorrowForecast(String municipality, TempUnit tempUnit);

}
