package com.hellogin.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * HelloView
 */
public class HelloView extends Composite {
    private static HelloViewUiBinder uiBinder = GWT.create(HelloViewUiBinder.class);

    @UiTemplate("HelloView.ui.xml")
    interface HelloViewUiBinder extends UiBinder<Widget, HelloView> {}

    @UiField
    SpanElement nameSpan;

    @UiField
    Anchor goodbyeLink;

    public HelloView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setName(final String name) {
        nameSpan.setInnerText(name);
    }

    public HasClickHandlers goodBye() {
        return goodbyeLink;
    }

}
