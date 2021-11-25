package org.komytron.weatherforecast.backend.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class
 */
@SpringBootApplication
public class WeatherForecastWebApp {

    /**
     * Main method that runs Spring Boot.
     *
     * @param args the usual command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(WeatherForecastWebApp.class, args);
    }

}
