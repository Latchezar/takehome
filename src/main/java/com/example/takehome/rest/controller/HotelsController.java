package com.example.takehome.rest.controller;

import com.example.takehome.rest.dto.CreateHotelRequest;
import com.example.takehome.rest.dto.CreateRoomRequest;
import com.example.takehome.rest.dto.HotelDto;
import com.example.takehome.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelsController {

    private final HotelService hotelService;

    @PostMapping
    public HotelDto createHotel(@RequestBody CreateHotelRequest createHotelRequest) {
        return hotelService.createHotel(createHotelRequest);
    }

    @PostMapping("/{id}/add-room")
    public HotelDto addRoom(@PathVariable Long id, @RequestBody CreateRoomRequest createRoomRequest) {
        return hotelService.addRoom(id, createRoomRequest);
    }

    @GetMapping
    public List<HotelDto> getAllHotels() {
        return hotelService.getAllHotels();
    }
}
