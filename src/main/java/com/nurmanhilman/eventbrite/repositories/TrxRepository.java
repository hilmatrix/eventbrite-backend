package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.TrxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TrxRepository extends JpaRepository<TrxEntity, Long> {
    // add custom query needed
}
