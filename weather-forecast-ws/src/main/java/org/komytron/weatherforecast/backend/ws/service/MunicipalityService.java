package org.komytron.weatherforecast.backend.ws.service;

import org.komytron.weatherforecast.backend.domain.Municipalities;
import org.komytron.weatherforecast.backend.domain.Municipality;

public interface MunicipalityService {

    Municipality getMunicipality(String municipality);

    Municipalities getMunicipalities();

}
