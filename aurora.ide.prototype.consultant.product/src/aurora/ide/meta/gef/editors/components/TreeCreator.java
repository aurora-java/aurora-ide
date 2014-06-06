package aurora.ide.meta.gef.editors.components;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.components.part.CustomTreePart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.CustomTree;

public class TreeCreator extends ComponentCreator {

	public TreeCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Tree", "Create a new Tree", CustomTree.class,
				new SimpleFactory(CustomTree.class),
				aurora.ide.prototype.consultant.product.Activator
						.getImageDescriptor("/icons/full/obj16/image_obj.gif"),
				aurora.ide.prototype.consultant.product.Activator
						.getImageDescriptor("/icons/full/obj16/image_obj.gif"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof CustomTree) {
			return new CustomTreePart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return CustomTree.class;
	}

	public AuroraComponent createComponent(String type) {
		String t = CustomTree.CUSTOM_ICON;
		if (t.equalsIgnoreCase(type)) {
			CustomTree c = new CustomTree();
			c.setComponentType(t);
			return c;
		}
		return null;
	}

}
