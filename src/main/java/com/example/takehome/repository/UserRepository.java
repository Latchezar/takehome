package com.example.takehome.repository;

import com.example.takehome.model.TakehomeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<TakehomeUser, Long> {
    TakehomeUser findByUsername(String username);
}
