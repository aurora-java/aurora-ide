package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.InputPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.NumberField;

public class NumberFieldCreator extends ComponentCreator {

	public NumberFieldCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"NumberField",
				"Create a new NumberField",
				NumberField.class,
				new SimpleFactory(NumberField.class)
				// {
				// public Object getNewObject() {
				// Input newObject = (Input) super.getNewObject();
				// newObject.setType(Input.NUMBER);
				// return newObject;
				// }
				// }
				, PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof NumberField) {
			return new InputPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return NumberField.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = NumberField.NUMBER;
		if (t.equalsIgnoreCase(type)) {
			NumberField c = new NumberField();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
