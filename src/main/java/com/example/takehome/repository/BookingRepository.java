package com.example.takehome.repository;

import com.example.takehome.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b where b.bookedBy.id = :userId")
    List<Booking> findAllByUserId(Long userId);
}
