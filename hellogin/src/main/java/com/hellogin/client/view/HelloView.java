package com.hellogin.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
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
    InlineHyperlink goodbyeLink;

    public HelloView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setSuffix(final String suffix) {
        nameSpan.setInnerText(suffix);
    }

    public void setToken(final String token) {
        goodbyeLink.setTargetHistoryToken(token);
    }

}
