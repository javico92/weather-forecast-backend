package org.komytron.weatherforecast.backend.ws.exception;

public class UnauthorizedException extends RuntimeException{

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message, Throwable ex) {
        super(message, ex);
    }

}
