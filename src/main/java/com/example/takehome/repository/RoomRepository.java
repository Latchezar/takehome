package com.example.takehome.repository;

import com.example.takehome.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select case when count(r) > 0 then true else false end from Room r where r.hotel.id = :hotelId and r.details = :details")
    boolean existsByHotelIdAndDetails(Long hotelId, String details);
}
