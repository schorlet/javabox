package demo.hello.client.cell;

import java.util.List;
import java.util.logging.Level;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.Messages;
import demo.hello.shared.cell.CellDTO;

/**
 * CellDtoEditorView
 */
public class CellDtoEditorView extends ResizeComposite {
    @UiTemplate("CellDtoEditorView.ui.xml")
    public interface ViewBinder extends UiBinder<Widget, CellDtoEditorView> {}

    public interface EditorDriver extends SimpleBeanEditorDriver<CellDTO, CellDtoEditor> {}

    private final EditorDriver editorDriver;

    @UiField(provided = true)
    final Messages messages;

    @UiField(provided = true)
    final CellDtoEditor editor;

    final CellEventHub eventHub;

    @Inject
    CellDtoEditorView(final ViewBinder uiBinder, final CellDtoEditor editor,
        final EditorDriver editorDriver, final CellEventHub eventHub, final Messages messages) {

        this.editor = editor;
        this.editorDriver = editorDriver;

        this.messages = messages;
        initWidget(uiBinder.createAndBindUi(this));
        editorDriver.initialize(editor);

        this.eventHub = eventHub;
    }

    public void edit(final CellDTO cellDTO) {
        editorDriver.edit(cellDTO);
    }

    @UiHandler("save")
    void onSaveClick(final ClickEvent event) {
        try {
            editorDriver.flush();

            if (editorDriver.hasErrors()) {
                final List<EditorError> errors = editorDriver.getErrors();
                for (final EditorError error : errors) {
                    Logger
                        .logp(Level.WARNING, "CellDtoEditorView", "getErrors", error.getMessage());
                }
            } else {
                final CellEditEvent cellEditEvent = new CellEditEvent();
                eventHub.fireFromSource(cellEditEvent, CellEventSource.CELLDTO_EDITOR_VIEW);
            }
        } catch (final Exception e) {
            Logger.loge(Level.SEVERE, "CellDtoEditorView", "onSaveClick", e);
        }
    }

    @UiHandler("cancel")
    void onCancelClick(final ClickEvent event) {
        final CellEditEvent cellEditEvent = new CellEditEvent();
        eventHub.fireFromSource(cellEditEvent, CellEventSource.CELLDTO_EDITOR_VIEW);
    }
}
