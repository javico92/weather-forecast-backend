package org.komytron.weatherforecast.backend.ws.mapper;

import org.komytron.weatherforecast.backend.domain.RainProbability;
import org.komytron.weatherforecast.backend.ws.client.aemet.domain.ForecastResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ForecastMapper {

    @Mappings({
            @Mapping(target = "period", source = "periodo"),
            @Mapping(target = "probability", source = "value")
    })
    RainProbability toRainProbability(ForecastResponse.RainProbability entity);

    List<RainProbability> toRainProbabilities(List<ForecastResponse.RainProbability> entity);

}
