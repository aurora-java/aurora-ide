package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.parts.CheckBoxPart;

public class CheckBoxCreator extends ComponentCreator {

	public CheckBoxCreator() {
	}
	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry("CheckBox",
				"Create a CheckBox", CheckBox.class, new SimpleFactory(
						CheckBox.class),
				ImagesUtils.getImageDescriptor("palette/checkbox_01.png"),
				ImagesUtils.getImageDescriptor("palette/checkbox_01.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof CheckBox) {
			return new CheckBoxPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return CheckBox.class;
	}
	public AuroraComponent createComponent(String type){
		if(CheckBox.CHECKBOX.equalsIgnoreCase(type)) {
			CheckBox c = new CheckBox();
			c.setType(CheckBox.CHECKBOX);
			return c;
		}
		return null;
	}
}
