package io.jmix.bpm.webinar.processdata.service;

import io.jmix.appsettings.AppSettings;
import io.jmix.bpm.webinar.processdata.entity.Order;
import io.jmix.bpm.webinar.processdata.entity.Product;
import io.jmix.bpm.webinar.processdata.entity.Settings;
import io.jmix.bpm.webinar.processdata.repository.ProductRepository;
import io.jmix.bpm.webinar.processdata.security.SystemAuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("pdt_InventoryService")
public class InventoryService {

    @Autowired
    private SystemAuthHelper systemAuthHelper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AppSettings appSettings;

    public boolean doReservation(Order order) {
        return systemAuthHelper.runWithSystemAuth(() -> {
            UUID productId = order.getProduct().getId();
            Optional<Product> optionalProduct = productRepository.findById(productId);

            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                Long inStock = product.getInStock();
                Long reserved = product.getReserved();
                Long quantity = order.getQuantity();

                if (inStock >= quantity) {
                    product.setInStock(inStock - quantity);
                    product.setReserved(reserved + quantity);
                    productRepository.save(product);
                    return true;
                }
            }
            return false;
        });
    }

    public void init() {
        Long inStock = appSettings.load(Settings.class).getInStock();
        for (Product product : productRepository.findAll()) {
            product.setReserved(0L);
            product.setDelivered(0L);
            product.setInStock(inStock);
            productRepository.save(product);
        }
    }
}