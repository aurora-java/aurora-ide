package aurora.ide.meta.gef.editors.components.box;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.parts.BoxPart;

public class HBoxCreator extends ComponentCreator {

	public HBoxCreator() {
	}
	
	public PaletteEntry createPaletteEntry(){
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry("HBox", "Create a  HBox",
				Grid.class, new SimpleFactory(HBox.class),
				ImagesUtils.getImageDescriptor("palette/hbox.png"),
				ImagesUtils.getImageDescriptor("palette/hbox.png"));
		return combined;
	}
	public EditPart createEditPart(Object model){
		if(model instanceof HBox)
			return new BoxPart();
		return null;
	}
	public Class<? extends AuroraComponent> clazz() {
		return HBox.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = HBox.H_BOX;
		if (t.equalsIgnoreCase(type)) {
			HBox c = new HBox();
			c.setType(t);
			return c;
		}
		return null;
	}
	
}
