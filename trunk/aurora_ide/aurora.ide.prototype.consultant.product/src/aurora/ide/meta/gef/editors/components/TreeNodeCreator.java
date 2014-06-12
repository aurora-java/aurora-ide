package aurora.ide.meta.gef.editors.components;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.part.CustomTreeNodePart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.CustomTreeNode;

public class TreeNodeCreator extends ComponentCreator {

	public TreeNodeCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Node", "Create a new Node", CustomTreeNode.class,
				new SimpleFactory(CustomTreeNode.class),
				PrototypeImagesUtils.getImageDescriptor("tree/leaf.gif"),
				PrototypeImagesUtils.getImageDescriptor("tree/leaf.gif"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof CustomTreeNode) {
			return new CustomTreeNodePart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return CustomTreeNode.class;
	}

	public AuroraComponent createComponent(String type) {
		String t = CustomTreeNode.CUSTOM_ICON;
		if (t.equalsIgnoreCase(type)) {
			CustomTreeNode c = new CustomTreeNode();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
