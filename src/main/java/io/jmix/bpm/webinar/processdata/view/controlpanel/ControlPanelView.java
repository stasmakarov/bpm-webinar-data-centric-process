package io.jmix.bpm.webinar.processdata.view.controlpanel;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.bpm.webinar.processdata.service.ResetService;
import io.jmix.bpm.webinar.processdata.view.main.MainView;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "control-panel-view", layout = MainView.class)
@ViewController(id = "pdt_ControlPanelView")
@ViewDescriptor(path = "control-panel-view.xml")
public class ControlPanelView extends StandardView {

    @Autowired
    private ResetService resetService;

    @Subscribe(id = "resetBtn", subject = "clickListener")
    public void onResetBtnClick(final ClickEvent<JmixButton> event) {
        resetService.resetApp();
    }
}