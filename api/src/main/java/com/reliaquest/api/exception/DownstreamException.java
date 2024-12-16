package com.reliaquest.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DownstreamException extends RuntimeException {
    private final int statusCodeValue;

    private final String errorBody;

    public DownstreamException(final int statusCodeValue, final String errorBody) {
        super("Downstream Error: " + errorBody);
        this.statusCodeValue = statusCodeValue;
        this.errorBody = errorBody;
    }
}
