package io.jmix.bpm.webinar.processdata.view.controlpanel;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.appsettings.AppSettings;
import io.jmix.bpm.webinar.processdata.entity.Settings;
import io.jmix.bpm.webinar.processdata.service.ProcessService;
import io.jmix.bpm.webinar.processdata.service.ResetService;
import io.jmix.bpm.webinar.processdata.view.main.MainView;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "control-panel-view", layout = MainView.class)
@ViewController(id = "pdt_ControlPanelView")
@ViewDescriptor(path = "control-panel-view.xml")
public class ControlPanelView extends StandardView {

    @Autowired
    private ResetService resetService;
    @Autowired
    private AppSettings appSettings;
    @Autowired
    private ProcessService processService;
    @Autowired
    private RepositoryService repositoryService;

    @ViewComponent
    private TypedTextField<Integer> numOfThreadsField;
    @ViewComponent
    private TypedTextField<Integer> numberOfProcessesField;
    @ViewComponent
    private JmixComboBox<String> processComboBox;

    @Subscribe(id = "resetBtn", subject = "clickListener")
    public void onResetBtnClick(final ClickEvent<JmixButton> event) {
        resetService.resetApp();
    }

    @Subscribe(id = "multiBtn", subject = "clickListener")
    public void onMultiBtnClick(final ClickEvent<JmixButton> event) {
        String processDefinitionKey = processComboBox.getValue();
        Integer numOfThreads = numOfThreadsField.getTypedValue();
        Integer numberOfProcesses = numberOfProcessesField.getTypedValue();
        if (processDefinitionKey == null || numOfThreads == null || numberOfProcesses == null) return;

        processService.runInParallel(numOfThreads, numberOfProcesses, processDefinitionKey);
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        List<String> processDefinitions = getProcessDefinitions();
        processComboBox.setItems(processDefinitions);

        Settings settings = appSettings.load(Settings.class);
        numOfThreadsField.setTypedValue(settings.getThreads());
        numberOfProcessesField.setTypedValue(settings.getProcessPerThread());
    }


    private List<String> getProcessDefinitions() {
        return repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionKey()
                .latestVersion()
                .asc()
                .list()
                .stream()
                .map(ProcessDefinition::getKey)
                .toList();
    }
}