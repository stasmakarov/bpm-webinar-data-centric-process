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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component("pdt_OrderService")
public class OrderService {

    private final Random random = new Random();

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DataManager dataManager;

    @Autowired
    private SystemAuthHelper systemAuthHelper;
    @Autowired
    private AppSettings appSettings;


    public Order createRandomOrder() {
        return systemAuthHelper.runWithSystemAuth(() -> {
            Product product = findRandomProduct();
            if (product == null) return null;

            Settings settings = appSettings.load(Settings.class);
            Long orderNumber = settings.getNextOrderNumber();
            settings.setNextOrderNumber(orderNumber + 1L);
            Long maxQuantity = settings.getMaxQuantity();
            long quantity = random.nextLong(1L, maxQuantity);

            Order order = orderRepository.create();
            order.setNumber(orderNumber);
            order.setProduct(product);
            order.setQuantity(quantity);
            order.setStatus(OrderStatus.NEW);
            orderRepository.save(order);
            return order;
        });
    }

    private Product findRandomProduct() {
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

    public void clear() {
        systemAuthHelper.runWithSystemAuth(() -> {
            orderRepository.deleteAll();
            Settings settings = appSettings.load(Settings.class);
            settings.setNextOrderNumber(1L);
            return null;
        });
    }

}