package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}