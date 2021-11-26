package org.komytron.weatherforecast.backend.ws.client.aemet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MunicipalityResponse {

    private String id;
    private String url;
    private String capital;
    private String nombre;
    private String id_old;
    private String latitud;
    private String latitud_dec;
    private String altitud;
    private String num_hab;
    private String zona_comarcal;
    private String destacada;
    private String longitud_dec;
    private String longitud;
}
