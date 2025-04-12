package io.jmix.bpm.webinar.processdata.repository;

import io.jmix.bpm.webinar.processdata.entity.Product;
import io.jmix.core.repository.JmixDataRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface ProductRepository extends JmixDataRepository<Product, UUID> {
}