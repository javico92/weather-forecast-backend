package org.komytron.weatherforecast.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forecast {

    private Date date;
    private Float temperatureAverage;
    private TempUnit temperatureUnit;
    private List<RainProbability> rainProbabilitiy;

}
