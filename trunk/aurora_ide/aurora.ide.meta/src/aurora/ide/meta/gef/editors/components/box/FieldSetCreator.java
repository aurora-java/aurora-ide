package aurora.ide.meta.gef.editors.components.box;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.BoxPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.FieldSet;

public class FieldSetCreator extends ComponentCreator {

	public FieldSetCreator() {
	}
	
	public PaletteEntry createPaletteEntry(){
		CombinedTemplateCreationEntry	combined = new CombinedTemplateCreationEntry("FieldSet",
				"Create a  FieldSet", FieldSet.class, new SimpleFactory(
						FieldSet.class),
				PrototypeImagesUtils.getImageDescriptor("palette/fieldset.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/fieldset.png"));
		return combined;
	}
	public EditPart createEditPart(Object model){
		if(model instanceof FieldSet)
			return new BoxPart();
		return null;
	}
	public Class<? extends AuroraComponent> clazz() {
		return FieldSet.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = FieldSet.FIELD_SET;
		if (t.equalsIgnoreCase(type)) {
			FieldSet c = new FieldSet();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
