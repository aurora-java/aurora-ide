package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.InputPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.TextArea;

public class TextAreaCreator extends ComponentCreator {

	public TextAreaCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"TextArea", "Create a new TextArea", TextArea.class,
				new SimpleFactory(TextArea.class),
				PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/input_edit.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof TextArea) {
			return new InputPart();
		}
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return TextArea.class;
	}

	public AuroraComponent createComponent(String type) {
		String t = TextArea.TEXT_AREA;
		if (t.equalsIgnoreCase(type)) {
			TextArea c = new TextArea();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
