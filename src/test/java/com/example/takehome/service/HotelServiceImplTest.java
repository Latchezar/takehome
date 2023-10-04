package com.example.takehome.service;

import com.example.takehome.exceptions.ErrorCode;
import com.example.takehome.exceptions.ServiceException;
import com.example.takehome.model.Hotel;
import com.example.takehome.model.Room;
import com.example.takehome.repository.HotelRepository;
import com.example.takehome.repository.RoomRepository;
import com.example.takehome.rest.dto.CreateHotelRequest;
import com.example.takehome.rest.dto.CreateRoomRequest;
import com.example.takehome.rest.dto.HotelDto;
import com.example.takehome.rest.dto.RoomDto;
import com.example.takehome.service.impl.HotelServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private RoomRepository roomRepository;
    @InjectMocks
    private HotelServiceImpl hotelService;

    @Test
    void createHotelWithoutRooms() {
        CreateHotelRequest createHotelRequest = CreateHotelRequest.builder()
                                                                  .name("mock-name")
                                                                  .build();
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(i -> i.getArguments()[0]);

        HotelDto dto = hotelService.createHotel(createHotelRequest);

        assertEquals(createHotelRequest.getName(), dto.getName());
    }

    @Test
    void createHotelWithRooms() {
        CreateRoomRequest room = CreateRoomRequest.builder()
                                                  .details("1")
                                                  .maxOccupants(2)
                                                  .build();
        CreateHotelRequest createHotelRequest = CreateHotelRequest.builder()
                                                                  .name("mock-name")
                                                                  .rooms(Set.of(room))
                                                                  .build();
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(i -> i.getArguments()[0]);

        HotelDto dto = hotelService.createHotel(createHotelRequest);

        assertEquals(createHotelRequest.getName(), dto.getName());
        assertEquals(1, dto.getRooms().size());
        assertEquals(room.getDetails(), dto.getRooms().get(0).getDetails());
    }

    @Test
    void addRoomShouldThrowWhenHotelNotFound() {
        final Long hotelId = 3L;
        CreateRoomRequest request = CreateRoomRequest.builder()
                                                     .details("1")
                                                     .build();
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> hotelService.addRoom(hotelId, request));

        assertEquals(ErrorCode.HOTEL_NOT_FOUND, exception.getError());
    }

    @Test
    void addRoomShouldThrowWhenRoomAlreadyExists() {
        final Long hotelId = 3L;
        Hotel hotel = Hotel.builder()
                           .id(hotelId)
                           .build();
        CreateRoomRequest request = CreateRoomRequest.builder()
                                                     .details("1")
                                                     .build();
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(roomRepository.existsByHotelIdAndDetails(hotelId, request.getDetails())).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> hotelService.addRoom(hotelId, request));

        assertEquals(ErrorCode.ROOM_ALREADY_EXISTS_IN_HOTEL, exception.getError());
    }

    @Test
    void addRoomShouldAddRoomToHotel() {
        final Long hotelId = 3L;
        Hotel hotel = Hotel.builder()
                           .id(hotelId)
                           .rooms(new ArrayList<>())
                           .build();
        CreateRoomRequest request = CreateRoomRequest.builder()
                                                     .details("1")
                                                     .build();
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(roomRepository.existsByHotelIdAndDetails(hotelId, request.getDetails())).thenReturn(false);
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(i -> i.getArguments()[0]);

        HotelDto dto = hotelService.addRoom(hotelId, request);

        assertEquals(1, dto.getRooms().size());
        assertEquals(request.getDetails(), dto.getRooms().get(0).getDetails());
    }

    @Test
    void getAllHotelsShouldReturnDtos() {
        Room room = Room.builder()
                        .id(5L)
                        .maxOccupants(3)
                        .details("2")
                        .build();
        Hotel hotel = Hotel.builder()
                           .id(3L)
                           .name("mock-name")
                           .rooms(List.of(room))
                           .build();

        when(hotelRepository.findAll()).thenReturn(List.of(hotel));

        List<HotelDto> result = hotelService.getAllHotels();

        assertEquals(1, result.size());

        HotelDto hotelDto = result.get(0);
        assertEquals(hotel.getId(), hotelDto.getId());
        assertEquals(hotel.getName(), hotelDto.getName());
        assertEquals(1, hotelDto.getRooms().size());

        RoomDto roomDto = hotelDto.getRooms().get(0);
        assertEquals(room.getId(), roomDto.getId());
        assertEquals(room.getDetails(), roomDto.getDetails());
    }
}
