package io.jmix.bpm.webinar.processdata.view.user;

import io.jmix.bpm.webinar.processdata.entity.User;
import io.jmix.bpm.webinar.processdata.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "users", layout = MainView.class)
@ViewController(id = "pdt_User.list")
@ViewDescriptor(path = "user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListView extends StandardListView<User> {
}