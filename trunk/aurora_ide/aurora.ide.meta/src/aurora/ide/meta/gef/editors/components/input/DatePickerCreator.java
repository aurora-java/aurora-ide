package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.InputPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.DatePicker;

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
				, PrototypeImagesUtils.getImageDescriptor("palette/itembar_02.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/itembar_02.png"));

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

	public AuroraComponent createComponent(String type) {
		String t = DatePicker.DATE_PICKER;
		if (t.equalsIgnoreCase(type)) {
			DatePicker c = new DatePicker();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
