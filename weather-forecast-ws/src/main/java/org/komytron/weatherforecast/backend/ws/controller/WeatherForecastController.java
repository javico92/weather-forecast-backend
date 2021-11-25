package org.komytron.weatherforecast.backend.ws.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.komytron.weatherforecast.backend.domain.Forecast;
import org.komytron.weatherforecast.backend.domain.Municipalities;
import org.komytron.weatherforecast.backend.domain.Municipality;
import org.komytron.weatherforecast.backend.domain.TempUnit;
import org.komytron.weatherforecast.backend.ws.service.ForecastService;
import org.komytron.weatherforecast.backend.ws.service.MunicipalityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/weatherforecast")
@RequiredArgsConstructor
public class WeatherForecastController {

    private final MunicipalityService municipalityService;
    private final ForecastService forecastService;

    @GetMapping(path = "/municipalities")
    public Municipalities getAllMunicipalities() {
        return municipalityService.getMunicipalities();
    }

    @GetMapping(path = "/{municipalityCode}/municipality")
    public Municipality getMunicipality(@PathVariable String municipalityCode) {
        return municipalityService.getMunicipality(municipalityCode);
    }

    @GetMapping(path = "/{municipalityCode}/daily/tomorrow")
    public Forecast getTomorrowDailyForecast(@PathVariable String municipalityCode, @RequestParam(value = "tempUnit", required = false, defaultValue = "G_CEL") String tempUnit) {
        return forecastService.getTomorrowForecast(municipalityCode, TempUnit.valueOf(tempUnit));
    }

}
