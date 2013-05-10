package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.InputPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.DateTimePicker;

public class DateTimePickerCreator extends ComponentCreator {

	public DateTimePickerCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry  combined = new CombinedTemplateCreationEntry("DateTimePicker",
				"Create a new DateTimePicker", DateTimePicker.class, new SimpleFactory(
						DateTimePicker.class) 
//		{
//					public Object getNewObject() {
//						Input newObject = (Input) super.getNewObject();
//						newObject.setType(Input.DATETIMEPICKER);
//						return newObject;
//					}
//				}
		, PrototypeImagesUtils.getImageDescriptor("palette/itembar_02.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/itembar_02.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof DateTimePicker) {
			return new InputPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return DateTimePicker.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = DateTimePicker.DATETIMEPICKER;
		if (t.equalsIgnoreCase(type)) {
			DateTimePicker c = new DateTimePicker();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
