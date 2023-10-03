package com.example.takehome.rest.dto;

import com.example.takehome.model.Room;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDto {
    private Long id;
    private String details;

    public RoomDto(Room room) {
        this.id = room.getId();
        this.details = room.getDetails();
    }
}
