package io.jmix.bpm.webinar.processdata.view.controlpanel;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.appsettings.AppSettings;
import io.jmix.bpm.webinar.processdata.entity.Settings;
import io.jmix.bpm.webinar.processdata.rabbit.RabbitService;
import io.jmix.bpm.webinar.processdata.service.ProcessService;
import io.jmix.bpm.webinar.processdata.service.ResetService;
import io.jmix.bpm.webinar.processdata.view.main.MainView;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "control-panel-view", layout = MainView.class)
@ViewController(id = "pdt_ControlPanelView")
@ViewDescriptor(path = "control-panel-view.xml")
public class ControlPanelView extends StandardView {
    private static final Logger log = LoggerFactory.getLogger(ControlPanelView.class);

    @Autowired
    private ResetService resetService;
    @Autowired
    private AppSettings appSettings;
    @Autowired
    private ProcessService processService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private Notifications notifications;

    @ViewComponent
    private TypedTextField<Integer> numOfThreadsField;
    @ViewComponent
    private TypedTextField<Integer> numberOfProcessesField;
    @ViewComponent
    private JmixComboBox<String> processComboBox;
    @ViewComponent
    private JmixButton startReadingBtn;
    @ViewComponent
    private JmixButton stopReadingBtn;

    @Subscribe(id = "resetBtn", subject = "clickListener")
    public void onResetBtnClick(final ClickEvent<JmixButton> event) {
        resetService.resetApp();
        log.info("🟥Application reset");
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

        if (rabbitService.isRabbitRunning()) {
            startReadingBtn.setEnabled(false);
            stopReadingBtn.setEnabled(true);
        } else {
            startReadingBtn.setEnabled(true);
            stopReadingBtn.setEnabled(false);
        }
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

    @Subscribe(id = "startReadingBtn", subject = "clickListener")
    public void onStartReadingBtnClick(final ClickEvent<JmixButton> event) {
        if (rabbitService.isRabbitAvailable()) {
            boolean started = rabbitService.startAllContainers();
            if (started) {
                startReadingBtn.setEnabled(false);
                stopReadingBtn.setEnabled(true);
            } else {
                notifications.create("One or more Rabbit listeners didn't start")
                        .withType(Notifications.Type.ERROR)
                        .show();
            }
        } else {
            notifications.create("Rabbit is unavailable")
                    .withType(Notifications.Type.ERROR)
                    .show();
        }
    }

    @Subscribe(id = "stopReadingBtn", subject = "clickListener")
    public void onStopReadingBtnClick(final ClickEvent<JmixButton> event) {
        if (rabbitService.stopAllContainers()) {
            startReadingBtn.setEnabled(true);
            stopReadingBtn.setEnabled(false);
        } else {
            notifications.create("One or more Rabbit listener failed to stop")
                    .withType(Notifications.Type.ERROR)
                    .show();
        }
    }


}