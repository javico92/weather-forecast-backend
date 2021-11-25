package org.komytron.weatherforecast.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Municipality implements Serializable {

    private String id;
    private String name;
    private String capital;
    private String url;

}
