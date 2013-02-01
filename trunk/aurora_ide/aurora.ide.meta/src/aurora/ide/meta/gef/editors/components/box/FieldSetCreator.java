package aurora.ide.meta.gef.editors.components.box;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.parts.BoxPart;

public class FieldSetCreator extends ComponentCreator {

	public FieldSetCreator() {
	}
	
	public PaletteEntry createPaletteEntry(){
		CombinedTemplateCreationEntry	combined = new CombinedTemplateCreationEntry("FieldSet",
				"Create a  FieldSet", FieldSet.class, new SimpleFactory(
						FieldSet.class),
				ImagesUtils.getImageDescriptor("palette/fieldset.png"),
				ImagesUtils.getImageDescriptor("palette/fieldset.png"));
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
}
