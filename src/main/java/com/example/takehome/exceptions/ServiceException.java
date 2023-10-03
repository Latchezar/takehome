package com.example.takehome.exceptions;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorCode error;

    private boolean ignorable = false;

    private Object payload;

    public ServiceException(ErrorCode error) {
        super(error.getDescription());
        this.error = error;
    }

}
