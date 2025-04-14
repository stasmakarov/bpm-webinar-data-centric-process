package io.jmix.bpm.webinar.processdata.service;

import io.jmix.appsettings.AppSettings;
import io.jmix.bpm.webinar.processdata.entity.Order;
import io.jmix.bpm.webinar.processdata.entity.OrderStatus;
import io.jmix.bpm.webinar.processdata.entity.Product;
import io.jmix.bpm.webinar.processdata.entity.Settings;
import io.jmix.bpm.webinar.processdata.repository.OrderRepository;
import io.jmix.bpm.webinar.processdata.repository.ProductRepository;
import io.jmix.bpm.webinar.processdata.security.SystemAuthHelper;
import io.jmix.core.DataManager;
import jakarta.persistence.OptimisticLockException;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component("pdt_OrderService")
public class OrderService {

    private final Random random = new Random();
    private long nextOrderNumber = 1L;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DataManager dataManager;

    @Autowired
    private SystemAuthHelper systemAuthHelper;

    @Autowired
    private AppSettings appSettings;

    public long getNextOrderNumber() {
        return nextOrderNumber;
    }

    public void setNextOrderNumber(long nextOrderNumber) {
        this.nextOrderNumber = nextOrderNumber;
    }

    public Order createRandomOrder(DelegateExecution execution) {
        return systemAuthHelper.runWithSystemAuth(() -> {
            Product product = findRandomProduct();
            if (product == null) return null;

            Settings settings = appSettings.load(Settings.class);
            long orderNumber = getNextOrderNumber();
            setNextOrderNumber(orderNumber + 1);

            Long maxQuantity = settings.getMaxQuantity();
            long quantity = random.nextLong(1L, maxQuantity);

            Order order = orderRepository.create();
            order.setNumber(orderNumber);
            order.setProduct(product);
            order.setQuantity(quantity);
            order.setStatus(OrderStatus.NEW);
            order.setProcessInstanceId(execution.getProcessInstanceId());
            order.setBusinessKey(execution.getProcessInstanceBusinessKey());
            orderRepository.save(order);
            return order;
        });
    }

    public void createRandomValuesForOrder(DelegateExecution execution) {
        systemAuthHelper.runWithSystemAuth(() -> {
            Product product = findRandomProduct();
            if (product == null) return null;

            Settings settings = appSettings.load(Settings.class);
            long orderNumber = getNextOrderNumber();
            setNextOrderNumber(orderNumber + 1);

            Long maxQuantity = settings.getMaxQuantity();
            long quantity = random.nextLong(1L, maxQuantity);

            execution.setVariable("number", orderNumber);
            execution.setVariable("product", product);
            execution.setVariable("quantity", quantity);
            execution.setVariable("processInstanceId", execution.getProcessInstanceId());
            return null;
        });
    }

    public String createRandomOrderForAsync(DelegateExecution execution) {
        return systemAuthHelper.runWithSystemAuth(() -> {
            Product product = findRandomProduct();
            if (product == null) return null;

            Settings settings = appSettings.load(Settings.class);
            long orderNumber = getNextOrderNumber();
            setNextOrderNumber(orderNumber + 1);

            Long maxQuantity = settings.getMaxQuantity();
            long quantity = random.nextLong(1L, maxQuantity);

            Order order = orderRepository.create();
            order.setNumber(orderNumber);
            order.setProduct(product);
            order.setQuantity(quantity);
            order.setStatus(OrderStatus.NEW);
            order.setProcessInstanceId(execution.getProcessInstanceId());
            order.setBusinessKey(execution.getProcessInstanceBusinessKey());
            orderRepository.save(order);
            return order.getId().toString();
        });
    }

    @Transactional
    private Long getOrderNumber() {
        for (int i = 0; true; i++) {
            try {
                Settings settings = appSettings.load(Settings.class);
                Long orderNumber = settings.getNextOrderNumber();
                settings.setNextOrderNumber(orderNumber + 1L);
                dataManager.save(settings);
                return orderNumber;
            } catch (OptimisticLockException e) {
                if (i == 2) throw e;
                try {
                    //noinspection BusyWait
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    public Product findRandomProduct() {
        return systemAuthHelper.runWithSystemAuth(() -> {
            List<Product> products = dataManager.load(Product.class)
                    .all()
                    .list();
            if (!products.isEmpty()) {
                int i = random.nextInt(products.size());
                return products.get(i);
            } else
                return null;
        });

    }

    public Order getOrderByNumber(Long number) {
        return systemAuthHelper.runWithSystemAuth(() -> {
            return orderRepository.findByNumber(number).orElse(null);
        });
    }

    public void setStatus(Order order, int statusId) {
        systemAuthHelper.runWithSystemAuth(() -> {
            order.setStatus(OrderStatus.fromId(statusId));
            orderRepository.save(order);
            return null;
        });
    }
 public void setStatusByOrderId(String orderId, int statusId) {
        systemAuthHelper.runWithSystemAuth(() -> {
            Optional<Order> optionalOrder = orderRepository.findById(UUID.fromString(orderId));
            if (optionalOrder.isEmpty()) return null;

            Order order = optionalOrder.get();
            order.setStatus(OrderStatus.fromId(statusId));
            orderRepository.save(order);
            return null;
        });
    }

    public void clear() {
        systemAuthHelper.runWithSystemAuth(() -> {
            orderRepository.deleteAll();
            setNextOrderNumber(1L);
            return null;
        });
    }

}