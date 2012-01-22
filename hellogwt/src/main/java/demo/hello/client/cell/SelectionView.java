package demo.hello.client.cell;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import demo.hello.client.Messages;

/**
 * SelectionView
 */
public class SelectionView extends ResizeComposite {
    @UiTemplate("SelectionView.ui.xml")
    public interface ViewBinder extends UiBinder<Widget, SelectionView> {}

    @UiField(provided = true)
    final Messages messages;

    @UiField
    TextBox aText;

    @UiField
    Button filter;

    @UiField
    Button regex;

    @UiField
    Button clear;

    @Inject
    SelectionView(final ViewBinder uiBinder, final Messages messages) {
        this.messages = messages;
        initWidget(uiBinder.createAndBindUi(this));
    }

    public HasClickHandlers filter() {
        return filter;
    }

    public HasClickHandlers regex() {
        return regex;
    }

    public HasClickHandlers clear() {
        return clear;
    }

    public String getA() {
        return aText.getText();
    }

    public void setA(final String a) {
        aText.setText(a);
    }

    public void regexEnabled(final boolean enabled) {
        regex.setEnabled(enabled);
    }
}
