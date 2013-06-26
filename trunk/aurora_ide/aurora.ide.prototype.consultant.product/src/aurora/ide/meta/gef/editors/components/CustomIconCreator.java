package aurora.ide.meta.gef.editors.components;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.components.part.CustomIconPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.CustomICon;

public class CustomIconCreator extends ComponentCreator {

	public CustomIconCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Image", "Create a new Image", CustomICon.class,
				new SimpleFactory(CustomICon.class),
				aurora.ide.prototype.consultant.product.Activator
						.getImageDescriptor("/icons/full/obj16/image_obj.gif"),
				aurora.ide.prototype.consultant.product.Activator
						.getImageDescriptor("/icons/full/obj16/image_obj.gif"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof CustomICon) {
			return new CustomIconPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return CustomICon.class;
	}

	public AuroraComponent createComponent(String type) {
		String t = CustomICon.CUSTOM_ICON;
		if (t.equalsIgnoreCase(type)) {
			CustomICon c = new CustomICon();
			c.setComponentType(t);
			return c;
		}
		return null;
	}

}
