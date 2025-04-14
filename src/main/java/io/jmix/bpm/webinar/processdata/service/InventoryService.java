package io.jmix.bpm.webinar.processdata.service;

import io.jmix.appsettings.AppSettings;
import io.jmix.bpm.webinar.processdata.entity.Order;
import io.jmix.bpm.webinar.processdata.entity.Product;
import io.jmix.bpm.webinar.processdata.entity.Settings;
import io.jmix.bpm.webinar.processdata.repository.OrderRepository;
import io.jmix.bpm.webinar.processdata.repository.ProductRepository;
import io.jmix.bpm.webinar.processdata.security.SystemAuthHelper;
import io.jmix.pessimisticlock.LockManager;
import io.jmix.pessimisticlock.entity.LockInfo;
import org.flowable.engine.delegate.BpmnError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component("pdt_InventoryService")
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private SystemAuthHelper systemAuthHelper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AppSettings appSettings;
    @Autowired
    private LockManager lockManager;

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

    public boolean doReservationWithBpmnError(Order order) {
        try {
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
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            while (cause != null) {
                if (cause instanceof JpaOptimisticLockingFailureException) {
                    log.warn("🆘 Optimistic locking on commit for order {}", order.getId(), cause);
                    throw new BpmnError("RESERVATION_CONFLICT", "Conflict during reservation");
                }
                cause = cause.getCause();
            }
            throw ex; // что-то другое
        }
    }

    @Transactional
    public boolean doReservationByOrderIdWithBpmnError(String orderId) {
        return systemAuthHelper.runWithSystemAuth(() -> {
            UUID uuid = UUID.fromString(orderId);

            Order order = orderRepository.findById(uuid).orElse(null);
            if (order == null) return false;

            Product product = productRepository.findById(order.getProduct().getId()).orElse(null);
            if (product == null) return false;

            long inStock = product.getInStock();
            long reserved = product.getReserved();
            long quantity = order.getQuantity();

            if (inStock < quantity) return false;

            product.setInStock(inStock - quantity);
            product.setReserved(reserved + quantity);

            try {
                productRepository.save(product);
            } catch (Exception ex) {
                if (isOptimisticLockingFailure(ex)) {
                    log.warn("🆘 Optimistic locking on commit for order {}", orderId, ex);
                    // Сразу выбрасываем BpmnError — чтобы не продолжать
                    throw new BpmnError("RESERVATION_CONFLICT", "Conflict during reservation");
                } else {
                    // Любая другая ошибка — тоже остановка, но другая ветка процесса
                    log.warn("🆘 Unexpected error during reservation {}", ex.getMessage(), ex);
                    throw new BpmnError("GENERIC_RESERVATION_ERROR", "Unexpected error");
                }
            }

            return true;
        });
    }

    private boolean isOptimisticLockingFailure(Throwable ex) {
        while (ex != null) {
            if (ex instanceof JpaOptimisticLockingFailureException) {
                return true;
            }
            ex = ex.getCause();
        }
        return false;
    }


    public boolean wrappedReservation(String orderId) {
        try {
            transactionalReservation(orderId);
            return true;
        } catch (BpmnError e) {
            // Вот здесь важно: выбрасываем именно BpmnError из Flowable!
            throw e;
        } catch (Exception ex) {
            throw ex;
        }
    }




    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean transactionalReservation(String orderId) {
        return systemAuthHelper.runWithSystemAuth(() -> {
            UUID uuid = UUID.fromString(orderId);

            Order order = orderRepository.findById(uuid).orElse(null);
            if (order == null) return false;

            Product product = productRepository.findById(order.getProduct().getId()).orElse(null);
            if (product == null) return false;

            long inStock = product.getInStock();
            long reserved = product.getReserved();
            long quantity = order.getQuantity();

            if (inStock < quantity) return false;

            product.setInStock(inStock - quantity);
            product.setReserved(reserved + quantity);
            try {
                productRepository.save(product);
            } catch (Exception ex) {
                if (isOptimisticLockingFailure(ex)) {
                    log.warn("💥 Caught optimistic lock failure");
                    throw new BpmnError("RESERVATION_CONFLICT");
                } else {
                    throw new BpmnError("GENERIC_RESERVATION_ERROR");
                }
            }
            return true;
        });
    }

    public boolean reservationWithLock(String orderId) {
        return systemAuthHelper.runWithSystemAuth(() -> {
            UUID uuid = UUID.fromString(orderId);

            Order order = orderRepository.findById(uuid).orElse(null);
            if (order == null) return false;

            Product product = productRepository.findById(order.getProduct().getId()).orElse(null);
            if (product == null) return false;

            LockInfo lockInfo = lockManager.lock(product);
            if (lockInfo != null) {
                log.warn("Product is already locked by {}", lockInfo.getUsername());
                throw new BpmnError("RESERVATION_CONFLICT", "Conflict during reservation");
            }

            try {
                // ⬇️ Обязательно перечитать в рамках новой транзакции и "свежего" EntityManager
                product = productRepository.findById(order.getProduct().getId()).orElse(null);
                if (product == null) return false;

                long inStock = product.getInStock();
                long reserved = product.getReserved();
                long quantity = order.getQuantity();

                if (inStock < quantity) return false;

                product.setInStock(inStock - quantity);
                product.setReserved(reserved + quantity);
                productRepository.save(product);

                return true;
            } finally {
                lockManager.unlock(product);
            }
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