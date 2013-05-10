package aurora.ide.meta.gef.editors.components.box;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.BoxPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.VBox;

public class VBoxCreator extends ComponentCreator {

	public VBoxCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"VBox", "Create a  VBox", Grid.class, new SimpleFactory(
						VBox.class),
				PrototypeImagesUtils.getImageDescriptor("palette/vbox.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/vbox.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof VBox) {
			return new BoxPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return VBox.class;
	}

	public AuroraComponent createComponent(String type) {
		String t = VBox.V_BOX;
		if (t.equalsIgnoreCase(type)) {
			VBox c = new VBox();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
