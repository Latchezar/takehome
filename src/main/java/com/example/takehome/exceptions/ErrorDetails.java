package com.example.takehome.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorDetails {

    private Integer errorCode;
    private String description;
}
