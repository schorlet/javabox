package com.hellogin.client.view;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * GoodbyeView
 */
public class GoodbyeView extends Composite {
    SimplePanel viewPanel = new SimplePanel();
    Element nameSpan = DOM.createSpan();

    public GoodbyeView() {
        viewPanel.getElement().appendChild(nameSpan);
        initWidget(viewPanel);
    }

    public void setName(final String name) {
        nameSpan.setInnerText("Good-bye, " + name);
    }
}
