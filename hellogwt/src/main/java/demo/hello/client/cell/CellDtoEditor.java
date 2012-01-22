package demo.hello.client.cell;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;

import demo.hello.client.Resources;
import demo.hello.shared.cell.CellDTO;

/**
 * CellDtoEditor
 */
public class CellDtoEditor extends ResizeComposite implements Editor<CellDTO> {
    @UiTemplate("CellDtoEditor.ui.xml")
    public interface ViewBinder extends UiBinder<Widget, CellDtoEditor> {}

    final Resources resources;

    @UiField
    TextBox a;

    @Editor.Ignore
    @UiField
    TextBox b_box;

    @UiField
    CheckBox c;

    @UiField
    DateBox d;

    LeafValueEditor<Double> b = new LeafValueEditor<Double>() {
        @Override
        public Double getValue() {
            final String value = b_box.getValue();
            return value == null || value.isEmpty() ? null : Double.valueOf(value);
        }

        @Override
        public void setValue(final Double value) {
            final String text = value == null ? null : String.valueOf(value);
            b_box.setValue(text);
        }
    };

    @Inject
    CellDtoEditor(final ViewBinder uiBinder, final Resources resources) {
        this.resources = resources;
        initWidget(uiBinder.createAndBindUi(this));
    }

}
