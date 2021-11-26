package org.komytron.weatherforecast.backend.ws.client.aemet;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.komytron.weatherforecast.backend.ws.exception.NotFoundException;
import org.komytron.weatherforecast.backend.ws.exception.TooManyRequestException;
import org.komytron.weatherforecast.backend.ws.exception.UnauthorizedException;
import org.komytron.weatherforecast.backend.ws.exception.UnknownErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.Map;

@Slf4j
@Component
public class AemetOpenDataClient {

    private static final String URL_ALL_MUNICIPALITY = "/maestro/municipios?api_key={0}";
    private static final String URL_SPECIFIC_MUNICIPALITY = "/maestro/municipio/{0}?api_key={1}";
    private static final String URL_DAILY_FORECAST_BY_MUNICIPALITY = "/prediccion/especifica/municipio/diaria/{0}?api_key={1}";

    @Value("${client.aemet.apiKey}")
    private String AEMET_API_KEY;

    private final WebClient webClient;

    public AemetOpenDataClient(WebClient.Builder webClientBuilder,
                               @Value("${client.aemet.url}") String url) throws SSLException {

        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

        webClient = webClientBuilder.baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(conf -> conf
                                .defaultCodecs()
                                .maxInMemorySize(5 * 1024 * 1024))
                        .build())
                .build();
    }

    public Mono<String> getAllMunicipalities() {

        return webClient
                .get()
                .uri(URL_ALL_MUNICIPALITY, AEMET_API_KEY)
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE)
                .retrieve()
                .onStatus(httpStatus -> HttpStatus.UNAUTHORIZED.equals(httpStatus), clientResponse -> this.getError(clientResponse, new UnauthorizedException()))
                .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus), clientResponse -> this.getError(clientResponse, new NotFoundException()))
                .onStatus(httpStatus -> HttpStatus.TOO_MANY_REQUESTS.equals(httpStatus), clientResponse -> this.getError(clientResponse, new TooManyRequestException()))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> this.getError(clientResponse, new UnknownErrorException()))
                .bodyToMono(String.class);
    }

    public Mono<String> getMunicipality(String municipality) {

        return webClient
                .get()
                .uri(URL_SPECIFIC_MUNICIPALITY,municipality, AEMET_API_KEY)
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE)
                .retrieve()
                .onStatus(httpStatus -> HttpStatus.UNAUTHORIZED.equals(httpStatus), clientResponse -> this.getError(clientResponse, new UnauthorizedException()))
                .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus), clientResponse -> this.getError(clientResponse, new NotFoundException()))
                .onStatus(httpStatus -> HttpStatus.TOO_MANY_REQUESTS.equals(httpStatus), clientResponse -> this.getError(clientResponse, new TooManyRequestException()))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> this.getError(clientResponse, new UnknownErrorException()))
                .bodyToMono(String.class);
    }

    public Mono<String> getDailyForecast(String municipality) {

        String forecastUrl = (String) webClient
                .get()
                .uri(URL_DAILY_FORECAST_BY_MUNICIPALITY, municipality, AEMET_API_KEY)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(httpStatus -> HttpStatus.UNAUTHORIZED.equals(httpStatus), clientResponse -> this.getError(clientResponse, new UnauthorizedException()))
                .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus), clientResponse -> this.getError(clientResponse, new NotFoundException()))
                .onStatus(httpStatus -> HttpStatus.TOO_MANY_REQUESTS.equals(httpStatus), clientResponse -> this.getError(clientResponse, new TooManyRequestException()))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> this.getError(clientResponse, new UnknownErrorException()))
                .bodyToMono(Map.class)
                .block()
                .get("datos");

        return webClient
                .get()
                .uri(forecastUrl)
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE)
                .retrieve()
                .onStatus(httpStatus -> HttpStatus.UNAUTHORIZED.equals(httpStatus), clientResponse -> this.getError(clientResponse, new UnauthorizedException()))
                .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus), clientResponse -> this.getError(clientResponse, new NotFoundException()))
                .onStatus(httpStatus -> HttpStatus.TOO_MANY_REQUESTS.equals(httpStatus), clientResponse -> this.getError(clientResponse, new TooManyRequestException()))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> this.getError(clientResponse, new UnknownErrorException()))
                .bodyToMono(String.class);
    }

    private <T extends RuntimeException> Mono<T> getError(ClientResponse response, T exception) {
        log.error("Error response status {}", response.statusCode());
        response.bodyToMono(String.class)
                .publishOn(Schedulers.boundedElastic())
                .subscribe(body -> log.error("Error response body: {}", body));

        return Mono.error(exception);
    }
}
