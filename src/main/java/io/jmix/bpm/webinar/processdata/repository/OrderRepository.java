package io.jmix.bpm.webinar.processdata.repository;

import io.jmix.bpm.webinar.processdata.entity.Order;
import io.jmix.core.repository.JmixDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JmixDataRepository<Order, UUID> {
    Optional<Order> findByNumber(Long number);
}