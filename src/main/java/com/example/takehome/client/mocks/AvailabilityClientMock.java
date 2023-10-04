package com.example.takehome.client.mocks;

import com.example.takehome.client.AvailabilityClient;
import com.example.takehome.model.Hotel;
import com.example.takehome.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvailabilityClientMock implements AvailabilityClient {

    private static final Integer MIN_AVAILABILITY = 0;
    private static final Integer MAX_AVAILABILITY = 100;

    private final HotelRepository hotelRepository;

    @Override
    public Map<Long, Map<Long, Map<LocalDate, Integer>>> getHotelAvailability(LocalDate checkinDate,
                                                                              LocalDate checkoutDate,
                                                                              Long... hotelIds) {
        Map<Long, Map<Long, Map<LocalDate, Integer>>> hotelAvailability = new HashMap<>();
        List<Hotel> hotels = hotelRepository.findAllById(List.of(hotelIds));
        Random random = new Random();
        hotels.forEach(hotel -> {
            Map<Long, Map<LocalDate, Integer>> roomAvailability = new HashMap<>();
            hotel.getRooms().forEach(room -> {
                Map<LocalDate, Integer> availability = new TreeMap<>();
                LocalDate current = checkinDate;
                do {
                    availability.put(current, random.nextInt((MAX_AVAILABILITY - MIN_AVAILABILITY) + 1) + MIN_AVAILABILITY);
                    current = current.plusDays(1);
                }
                while (!current.isAfter(checkoutDate));
                roomAvailability.put(room.getId(), availability);
            });
            hotelAvailability.put(hotel.getId(), roomAvailability);
        });
        return hotelAvailability;
    }

    @Override
    public void updateRoomAvailabilityAfterBooking(Long hotelId,
                                                   Long roomId,
                                                   LocalDate checkinDate,
                                                   LocalDate checkoutDate) {
        boolean isMockError = System.currentTimeMillis() % 7 == 0;

        if (isMockError) {
            log.error("Error updating room availability after booking");
            throw new IllegalArgumentException("Room isn't available for the given dates");
        }

        log.info("Room availability updated...");
    }
}
