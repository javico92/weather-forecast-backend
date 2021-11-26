package org.komytron.weatherforecast.backend.ws.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message, Throwable ex) {
        super(message, ex);
    }

}
