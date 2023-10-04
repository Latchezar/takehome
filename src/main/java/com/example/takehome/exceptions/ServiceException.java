package com.example.takehome.exceptions;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorCode error;

    public ServiceException(ErrorCode error) {
        super(error.getDescription());
        this.error = error;
    }

}
