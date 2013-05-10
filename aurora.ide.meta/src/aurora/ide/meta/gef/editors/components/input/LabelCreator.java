package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.LabelPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Label;

public class LabelCreator extends ComponentCreator {

	public LabelCreator() {
	}
	public PaletteEntry createPaletteEntry(){
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Label", "Create a new Label", Label.class,
				new SimpleFactory(Label.class) {
					public Object getNewObject() {
						Label newObject = (Label) super.getNewObject();
						return newObject;
					}
				}, PrototypeImagesUtils.getImageDescriptor("palette/label.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/label.png"));
		return combined;
	}
	public EditPart createEditPart(Object model){
		if(model instanceof Label){
			return new LabelPart();
		}
		return null;
	}
	public Class<? extends AuroraComponent> clazz() {
		return Label.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = Label.Label;
		if (t.equalsIgnoreCase(type)) {
			Label c = new Label();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
