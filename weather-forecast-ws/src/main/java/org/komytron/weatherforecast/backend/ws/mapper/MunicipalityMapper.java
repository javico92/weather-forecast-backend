package org.komytron.weatherforecast.backend.ws.mapper;

import org.apache.commons.lang3.StringUtils;
import org.komytron.weatherforecast.backend.domain.Municipality;
import org.komytron.weatherforecast.backend.ws.client.aemet.domain.MunicipalityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MunicipalityMapper {

    @Mappings({
            @Mapping(target = "id", source = "id", qualifiedByName = "formatIdentifier"),
            @Mapping(target = "name", source = "nombre")
    })
    Municipality toMunicipality(MunicipalityResponse entity);

    @Named("formatIdentifier")
    default String formatIdentifier(String id) {
        return StringUtils.isEmpty(id) ? "" : id.replaceAll("[^\\d.]", "");
    }

    List<Municipality> toMunicipalities(List<MunicipalityResponse> entity);
}
