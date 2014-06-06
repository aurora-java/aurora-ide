package aurora.ide.meta.gef.editors.components;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.components.part.CustomTreeContainerPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;

public class TreeNodeContainerCreator extends ComponentCreator {

	public TreeNodeContainerCreator() {
	}
	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"NodeContainer", "Create a new NodeContainer", CustomTreeContainerNode.class,
				new SimpleFactory(CustomTreeContainerNode.class),
				aurora.ide.prototype.consultant.product.Activator
						.getImageDescriptor("/icons/full/obj16/image_obj.gif"),
				aurora.ide.prototype.consultant.product.Activator
						.getImageDescriptor("/icons/full/obj16/image_obj.gif"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof CustomTreeContainerNode) {
			return new CustomTreeContainerPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return CustomTreeContainerNode.class;
	}

	public AuroraComponent createComponent(String type) {
		String t = CustomTreeContainerNode.CUSTOM_ICON;
		if (t.equalsIgnoreCase(type)) {
			CustomTreeContainerNode c = new CustomTreeContainerNode();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
