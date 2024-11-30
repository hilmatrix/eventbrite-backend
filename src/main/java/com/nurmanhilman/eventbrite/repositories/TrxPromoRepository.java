package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.TrxPromoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrxPromoRepository extends JpaRepository<TrxPromoEntity, Long> {}