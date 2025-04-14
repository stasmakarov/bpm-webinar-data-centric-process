package io.jmix.bpm.webinar.processdata.view.neworderform;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.appsettings.AppSettings;
import io.jmix.bpm.webinar.processdata.entity.Product;
import io.jmix.bpm.webinar.processdata.entity.Settings;
import io.jmix.bpm.webinar.processdata.service.OrderService;
import io.jmix.bpm.webinar.processdata.view.main.MainView;
import io.jmix.bpmflowui.processform.ProcessFormContext;
import io.jmix.bpmflowui.processform.annotation.OutputVariable;
import io.jmix.bpmflowui.processform.annotation.ProcessForm;
import io.jmix.bpmflowui.processform.annotation.ProcessVariable;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@ProcessForm(outputVariables = {
        @OutputVariable(name = "number", type = Long.class),
        @OutputVariable(name = "product", type = Product.class),
        @OutputVariable(name = "quantity", type = Long.class)
})
@Route(value = "new-order-form", layout = MainView.class)
@ViewController(id = "pdt_NewOrderForm")
@ViewDescriptor(path = "new-order-form.xml")
public class NewOrderForm extends StandardView {

    private final Random random = new Random();

    @Autowired
    private ProcessFormContext processFormContext;
    @Autowired
    private AppSettings appSettings;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RuntimeService runtimeService;

    @ProcessVariable(name = "number")
    @ViewComponent
    private TypedTextField<Long> numberField;
    @ProcessVariable(name = "product")
    @ViewComponent
    private EntityComboBox<Product> productField;
    @ProcessVariable(name = "quantity")
    @ViewComponent
    private TypedTextField<Long> quantityField;

    @Subscribe(id = "startProcessBtn", subject = "clickListener")
    protected void onStartProcessBtnClick(ClickEvent<JmixButton> event) {
        String definitionKey = processFormContext.getProcessDefinition().getKey();
        String businessKey = "Ord.#" + numberField.getValue();
        Map<String, Object> variables = new HashMap<>();

        variables.put("number", numberField.getTypedValue());
        variables.put("product", productField.getValue());
        variables.put("quantity", quantityField.getTypedValue());
        variables.put("businessKey", businessKey);

        runtimeService.startProcessInstanceByKey(definitionKey, businessKey, variables);

//        processFormContext.processStarting()
//                .saveInjectedProcessVariables()
//                .start();
        closeWithDefaultAction();
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        long orderNumber = orderService.getNextOrderNumber();
        orderService.setNextOrderNumber(orderNumber + 1);
        numberField.setTypedValue(orderNumber);

        Product product = orderService.findRandomProduct();
        productField.setValue(product);

        Settings settings = appSettings.load(Settings.class);
        Long maxQuantity = settings.getMaxQuantity();
        long quantity = random.nextLong(1L, maxQuantity);
        quantityField.setTypedValue(quantity);
    }


}