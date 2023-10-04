package com.example.takehome.service.async;

import com.example.takehome.client.AvailabilityClient;
import com.example.takehome.common.BookingStatus;
import com.example.takehome.model.Booking;
import com.example.takehome.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingAsyncService {

    private final AvailabilityClient availabilityClient;
    private final BookingRepository bookingRepository;

    @Async
    @Transactional
    public void processBookingAfterInitialSave(Booking booking) {
        log.info("Processing Booking after save...");
        Map<Long, Map<Long, Map<LocalDate, Integer>>> availability = availabilityClient
                .getHotelAvailability(booking.getCheckinDate().toLocalDate(), booking.getCheckoutDate().toLocalDate(), booking.getHotel().getId());

        BookingStatus status = BookingStatus.REJECTED;
        if (isRoomAvailable(availability, booking.getHotel().getId(), booking.getRoom().getId())) {
            status = BookingStatus.CONFIRMED;
            try {
                availabilityClient.updateRoomAvailabilityAfterBooking(booking.getHotel().getId(), booking.getRoom().getId(),
                                                                      booking.getCheckinDate().toLocalDate(), booking.getCheckoutDate().toLocalDate());
            } catch (Exception e) {
                log.error(e.getMessage());
                status = BookingStatus.REJECTED;
            }
        }
        booking.setStatus(status);
        log.info("Updating booking {} with status {}", booking.getId(), status);
        bookingRepository.save(booking);
    }

    private boolean isRoomAvailable(Map<Long, Map<Long, Map<LocalDate, Integer>>> availability,
                                    Long hotelId,
                                    Long roomId) {
        if (!availability.containsKey(hotelId)) {
            return false;
        }

        Map<Long, Map<LocalDate, Integer>> hotelAvailability = availability.get(hotelId);
        if (!hotelAvailability.containsKey(roomId)) {
            return false;
        }

        for (Map.Entry<LocalDate, Integer> entry : hotelAvailability.get(roomId).entrySet()) {
            if (entry.getValue() < 1) {
                return false;
            }
        }

        return true;
    }
}
