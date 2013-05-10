package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.InputPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Combox;

public class ComboxCreator extends ComponentCreator {

	public ComboxCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Combox",
				"Create a new Combox",
				Combox.class,
				new SimpleFactory(Combox.class)
				// {
				// public Object getNewObject() {
				// Input newObject = (Input) super.getNewObject();
				// newObject.setType(Input.Combo);
				// return newObject;
				// }
				// }
				, PrototypeImagesUtils.getImageDescriptor("palette/itembar_01.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/itembar_01.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof Combox) {
			return new InputPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return Combox.class;
	}
	public AuroraComponent createComponent(String type){
		if(Combox.Combo.equalsIgnoreCase(type)) {
			Combox c = new Combox();
			c.setComponentType(Combox.Combo);
			return c;
		}
		return null;
	}
}
