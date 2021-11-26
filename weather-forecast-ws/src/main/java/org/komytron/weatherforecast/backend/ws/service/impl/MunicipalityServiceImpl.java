package org.komytron.weatherforecast.backend.ws.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.komytron.weatherforecast.backend.domain.Municipalities;
import org.komytron.weatherforecast.backend.domain.Municipality;
import org.komytron.weatherforecast.backend.ws.client.aemet.AemetOpenDataClient;
import org.komytron.weatherforecast.backend.ws.client.aemet.domain.MunicipalityResponse;
import org.komytron.weatherforecast.backend.ws.exception.UnknownErrorException;
import org.komytron.weatherforecast.backend.ws.mapper.MunicipalityMapper;
import org.komytron.weatherforecast.backend.ws.service.MunicipalityService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MunicipalityServiceImpl implements MunicipalityService {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final AemetOpenDataClient aemetOpenDataClient;
    private final MunicipalityMapper municipalityMapper;

    @Override
    public Municipality getMunicipality(String municipality) {
        MunicipalityResponse municipalityResponse = Optional.ofNullable(aemetOpenDataClient.getMunicipality(municipality)
                .map(response -> {
                    try {
                        return JSON_MAPPER.readValue(response,  new TypeReference<List<MunicipalityResponse>>(){});
                    } catch (JsonProcessingException e) {
                        throw new UnknownErrorException("Error parsing json", e);
                    }
                })
                .block()).orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .get();

        return municipalityMapper.toMunicipality(municipalityResponse);
    }

    @Override
    public Municipalities getMunicipalities() {

        List<MunicipalityResponse> municipalities = aemetOpenDataClient.getAllMunicipalities()
                .map(response -> {
                    try {
                         return JSON_MAPPER.readValue(response,  new TypeReference<List<MunicipalityResponse>>(){});
                    } catch (JsonProcessingException e) {
                        throw new UnknownErrorException("Error parsing json", e);
                    }
                })
                .doOnError(throwable -> log.error("Error getting municipalities", throwable))
                .block();

        return Municipalities.builder().municipalities(municipalityMapper.toMunicipalities(municipalities)).build();
    }

}
