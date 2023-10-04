package com.example.takehome.rest.dto;

import com.example.takehome.model.Hotel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class HotelDto {

    private Long id;
    private String name;
    private List<RoomDto> rooms;
    private LocalDateTime createdAt;

    public HotelDto(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.rooms = hotel.getRooms().stream()
                          .map(RoomDto::new).toList();
        this.createdAt = hotel.getCreatedAt();
    }
}
