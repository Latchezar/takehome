package com.example.takehome.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorDetails {

    private Integer errorCode;
    private String description;
    private List<String> errors;

    public ErrorDetails() {
    }

    public ErrorDetails(Integer errorCode,
                        String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    public ErrorDetails(String description) {
        this.description = description;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
