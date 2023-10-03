package com.example.takehome.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateHotelRequest {

    @NotBlank(message = "Hotel Name must not be blank")
    private String name;

    private Set<CreateRoomRequest> rooms;
}
