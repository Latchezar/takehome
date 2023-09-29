package com.example.takehome.exceptions;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceException extends RuntimeException {

    private final ErrorCode error;

    private boolean ignorable = false;

    private Object payload;

    public ServiceException(ErrorCode error) {
        super(error.getDescription());
        this.error = error;
    }

}
