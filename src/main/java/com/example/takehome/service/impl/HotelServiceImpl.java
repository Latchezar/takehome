package com.example.takehome.service.impl;

import com.example.takehome.exceptions.ErrorCode;
import com.example.takehome.exceptions.ServiceException;
import com.example.takehome.model.Hotel;
import com.example.takehome.model.Room;
import com.example.takehome.repository.HotelRepository;
import com.example.takehome.repository.RoomRepository;
import com.example.takehome.rest.dto.CreateHotelRequest;
import com.example.takehome.rest.dto.CreateRoomRequest;
import com.example.takehome.rest.dto.HotelDto;
import com.example.takehome.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('hotels.create', 'hotels.manage')")
    public HotelDto createHotel(CreateHotelRequest createHotelRequest) {
        Hotel hotel = Hotel.builder()
                           .name(createHotelRequest.getName())
                           .rooms(new ArrayList<>())
                           .build();
        if (CollectionUtils.isNotEmpty(createHotelRequest.getRooms())) {
            hotel.getRooms().addAll(createHotelRequest.getRooms().stream()
                                                      .map(room -> Room.builder()
                                                                       .details(room.getDetails())
                                                                       .maxOccupants(room.getMaxOccupants())
                                                                       .hotel(hotel)
                                                                       .build())
                                                      .toList());
        }
        return new HotelDto(hotelRepository.save(hotel));
    }

    @Override
    @PreAuthorize("hasAuthority('hotels.manage')")
    public HotelDto addRoom(Long id,
                            CreateRoomRequest createRoomRequest) {
        Hotel hotel = hotelRepository.findById(id)
                                     .orElseThrow(() -> new ServiceException(ErrorCode.HOTEL_NOT_FOUND));

        if (roomRepository.existsByHotelIdAndDetails(hotel.getId(), createRoomRequest.getDetails())) {
            throw new ServiceException(ErrorCode.ROOM_ALREADY_EXISTS_IN_HOTEL);
        }

        hotel.getRooms().add(Room.builder()
                                 .hotel(hotel)
                                 .details(createRoomRequest.getDetails())
                                 .maxOccupants(createRoomRequest.getMaxOccupants())
                                 .build());

        return new HotelDto(hotelRepository.save(hotel));
    }

    @Override
    public List<HotelDto> getAllHotels() {
        return hotelRepository.findAll().stream().map(HotelDto::new).toList();
    }
}
