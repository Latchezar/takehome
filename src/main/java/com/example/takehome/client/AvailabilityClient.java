package com.example.takehome.client;

import java.time.LocalDate;
import java.util.Map;

public interface AvailabilityClient {

    Map<Long, Map<Long, Map<LocalDate, Integer>>> getHotelAvailability(LocalDate checkinDate,
                                                                       LocalDate checkoutDate,
                                                                       Long... hotelIds);

    void updateRoomAvailabilityAfterBooking(Long hotelId,
                                            Long roomId,
                                            LocalDate checkinDate,
                                            LocalDate checkoutDate);
}
