package demo.hello.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class ModuleTestGwt implements EntryPoint {

    public void onModuleLoad() {
        RootPanel.get().add(new Label("ModuleTestGwt"));
    }
}
