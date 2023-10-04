package com.example.takehome.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DATABASE_ERROR(10001, "Something went wrong, please send us feedback and try again", HttpStatus.INTERNAL_SERVER_ERROR),

    USER_NOT_FOUND(12001, "User not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(12002, "Invalid Credentials", HttpStatus.UNAUTHORIZED),

    HOTEL_NOT_FOUND(13001, "Hotel Not Found", HttpStatus.NOT_FOUND),
    ROOM_ALREADY_EXISTS_IN_HOTEL(13002, "Room already exists in the given hotel", HttpStatus.BAD_REQUEST),
    ROOM_NOT_FOUND(13003, "Room Not Found", HttpStatus.BAD_REQUEST),
    ROOM_MAX_OCCUPANTS_EXCEEDED(13004, "Room Max Occupants Exceeded", HttpStatus.BAD_REQUEST),

    CHECKIN_DATE_MUST_NOT_BE_BEFORE_TODAY(14001, "Checkin date must no be before today", HttpStatus.BAD_REQUEST),
    CHECKOUT_DATE_MUST_BE_AFTER_CHECKIN_DATE(14002, "Chekout date must be after checkin date", HttpStatus.BAD_REQUEST);

    private final Integer code;
    private final String description;
    private final HttpStatus status;

    ErrorCode(Integer code,
              String description,
              HttpStatus status) {
        this.code = code;
        this.description = description;
        this.status = status;
    }
}
