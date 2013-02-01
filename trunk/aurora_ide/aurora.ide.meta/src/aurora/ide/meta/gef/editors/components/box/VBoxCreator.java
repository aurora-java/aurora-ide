package aurora.ide.meta.gef.editors.components.box;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.VBox;
import aurora.ide.meta.gef.editors.parts.BoxPart;

public class VBoxCreator extends ComponentCreator {

	public VBoxCreator() {
	}
	
	public PaletteEntry createPaletteEntry(){
		CombinedTemplateCreationEntry	combined = new CombinedTemplateCreationEntry("VBox", "Create a  VBox",
				Grid.class, new SimpleFactory(VBox.class),
				ImagesUtils.getImageDescriptor("palette/vbox.png"),
				ImagesUtils.getImageDescriptor("palette/vbox.png"));
		return combined;
	}
	public EditPart createEditPart(Object model){
		if(model instanceof VBox){
			return new BoxPart();
		}
		return null;
	}
	public Class<? extends AuroraComponent> clazz() {
		return VBox.class;
	}

}
