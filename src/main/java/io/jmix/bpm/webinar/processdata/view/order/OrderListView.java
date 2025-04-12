package io.jmix.bpm.webinar.processdata.view.order;

import com.vaadin.flow.router.Route;
import io.jmix.bpm.webinar.processdata.entity.Order;
import io.jmix.bpm.webinar.processdata.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "orders", layout = MainView.class)
@ViewController(id = "pdt_Order.list")
@ViewDescriptor(path = "order-list-view.xml")
@LookupComponent("ordersDataGrid")
@DialogMode(width = "64em")
public class OrderListView extends StandardListView<Order> {
}