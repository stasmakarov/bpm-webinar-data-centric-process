package io.jmix.bpm.webinar.processdata.view.processinstancelist;

import com.vaadin.flow.router.Route;
import io.jmix.bpmflowui.view.processinstance.ProcessInstanceListView;
import io.jmix.flowui.view.DefaultMainViewParent;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "bpm/processinstances", layout = DefaultMainViewParent.class)
@ViewController(id = "bpm_ProcessInstance.list")
@ViewDescriptor(path = "pdt-process-instance-list-view.xml")
public class PdtProcessInstanceListView extends ProcessInstanceListView {
    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        applyFilter();
    }
}