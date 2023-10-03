package com.example.takehome.service;

import com.example.takehome.config.security.SecurityUser;
import com.example.takehome.rest.dto.CreateHotelRequest;
import com.example.takehome.rest.dto.CreateRoomRequest;
import com.example.takehome.rest.dto.HotelDto;

import java.util.List;

public interface HotelService {

    HotelDto createHotel(CreateHotelRequest createHotelRequest);

    HotelDto addRoom(Long id, CreateRoomRequest createRoomRequest);

    List<HotelDto> getAllHotels();
}
