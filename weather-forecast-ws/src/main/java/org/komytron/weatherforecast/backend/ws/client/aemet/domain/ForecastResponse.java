package org.komytron.weatherforecast.backend.ws.client.aemet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse {

    private String nombre;
    private String provincia;
    private ForecastResponse.Prediction prediccion;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Prediction{
        private List<ForecastResponse.Day> dia;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Day{
        private Date fecha;
        private List<ForecastResponse.RainProbability> probPrecipitacion;
        private ForecastResponse.Temperature temperatura;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RainProbability{
        private String periodo;
        private String value;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Temperature{
        private List<DataValue> dato;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataValue{
        private Integer hora;
        private String value;
    }
}
