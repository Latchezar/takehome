package com.example.takehome.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {

    @NotBlank(message = "Room details must not be blank")
    private String details;

    @NotNull(message = "Room must have max occupants")
    private Integer maxOccupants;
}
