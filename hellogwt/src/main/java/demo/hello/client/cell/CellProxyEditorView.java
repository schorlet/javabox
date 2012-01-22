package demo.hello.client.cell;

import java.util.List;
import java.util.logging.Level;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import demo.hello.client.Logger;
import demo.hello.client.Messages;
import demo.hello.shared.ProjectRequestFactory.CellRequest;
import demo.hello.shared.cell.CellProxy;

/**
 * CellProxyEditorView
 */
public class CellProxyEditorView extends ResizeComposite {
    @UiTemplate("CellProxyEditorView.ui.xml")
    public interface ViewBinder extends UiBinder<Widget, CellProxyEditorView> {}

    public interface EditorDriver extends RequestFactoryEditorDriver<CellProxy, CellProxyEditor> {}

    private final EditorDriver editorDriver;

    @UiField(provided = true)
    final Messages messages;

    @UiField(provided = true)
    final CellProxyEditor editor;

    final CellEventHub eventHub;

    @Inject
    CellProxyEditorView(final ViewBinder uiBinder, final CellProxyEditor editor,
        final EditorDriver editorDriver, final CellEventHub eventHub, final Messages messages) {

        this.editor = editor;
        this.editorDriver = editorDriver;

        this.messages = messages;
        initWidget(uiBinder.createAndBindUi(this));
        editorDriver.initialize(editor);

        this.eventHub = eventHub;
    }

    public void edit(final CellProxy cellProxy, final CellRequest newCellRequest) {
        editorDriver.edit(cellProxy, newCellRequest);
    }

    public void display(final CellProxy cellProxy) {
        editorDriver.display(cellProxy);
    }

    @UiHandler("save")
    void onSaveClick(final ClickEvent event) {
        try {
            final RequestContext requestContext = editorDriver.flush();

            if (editorDriver.hasErrors()) {
                final List<EditorError> errors = editorDriver.getErrors();
                for (final EditorError error : errors) {
                    Logger.logp(Level.WARNING, "CellProxyEditorView", "onSaveClick",
                        error.getMessage());
                }

            } else if (requestContext.isChanged()) {
                Logger.logp(Level.INFO, "CellProxyEditorView", "onSaveClick");

                requestContext.fire(new Receiver<Void>() {
                    @Override
                    public void onSuccess(final Void response) {
                        final CellEditEvent cellEditEvent = new CellEditEvent();
                        eventHub.fireFromSource(cellEditEvent,
                            CellEventSource.CELLPROXY_EDITOR_VIEW);
                    }

                    @Override
                    public void onFailure(final ServerFailure error) {
                        Logger.logp(Level.SEVERE, "CellProxyEditorView.onSaveClick", "onFailure",
                            error.getMessage(), error.getStackTraceString());
                        Window.alert(error.getMessage());
                    };
                });
            }
        } catch (final Exception e) {
            Logger.loge(Level.SEVERE, "CellProxyEditorView", "onSaveClick", e);
        }
    }

    @UiHandler("cancel")
    void onCancelClick(final ClickEvent event) {
        final CellEditEvent cellEditEvent = new CellEditEvent();
        eventHub.fireFromSource(cellEditEvent, CellEventSource.CELLPROXY_EDITOR_VIEW);
    }
}
