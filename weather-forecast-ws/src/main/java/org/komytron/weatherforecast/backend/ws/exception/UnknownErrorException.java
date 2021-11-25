package org.komytron.weatherforecast.backend.ws.exception;

public class UnknownErrorException extends RuntimeException{

    public UnknownErrorException() {
        super();
    }

    public UnknownErrorException(String message, Throwable ex) {
        super(message, ex);
    }

}
