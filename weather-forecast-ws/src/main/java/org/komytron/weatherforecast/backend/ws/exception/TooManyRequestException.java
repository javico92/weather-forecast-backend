package org.komytron.weatherforecast.backend.ws.exception;

public class TooManyRequestException extends RuntimeException{

    public TooManyRequestException() {
        super();
    }

    public TooManyRequestException(String message, Throwable ex) {
        super(message, ex);
    }

}
