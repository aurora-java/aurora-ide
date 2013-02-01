package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.parts.InputPart;

public class DatePickerCreator extends ComponentCreator {

	public DatePickerCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"DatePicker",
				"Create a new DatePicker",
				DatePicker.class,
				new SimpleFactory(DatePicker.class)
				// {
				// public Object getNewObject() {
				// Input newObject = (Input) super.getNewObject();
				// newObject.setType(Input.CAL);
				// return newObject;
				// }
				// }
				, ImagesUtils.getImageDescriptor("palette/itembar_02.png"),
				ImagesUtils.getImageDescriptor("palette/itembar_02.png"));

		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof DatePicker) {
			return new InputPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return DatePicker.class;
	}
}
