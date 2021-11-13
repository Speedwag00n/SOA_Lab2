package ilia.nemankov.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadResponseException extends Exception {

    private final int responseCode;

    public BadResponseException(String message, int responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    public BadResponseException(String message, Throwable cause, int responseCode) {
        super(message, cause);
        this.responseCode = responseCode;
    }

}
