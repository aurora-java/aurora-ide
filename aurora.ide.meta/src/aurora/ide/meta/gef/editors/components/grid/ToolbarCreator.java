package aurora.ide.meta.gef.editors.components.grid;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.components.input.TextField;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.parts.ToolbarPart;

public class ToolbarCreator extends ComponentCreator {

	public ToolbarCreator() {
	}
	public PaletteEntry createPaletteEntry(){
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry("Toolbar",
				"Create a  Toolbar ", Toolbar.class, new SimpleFactory(
						Toolbar.class),
				ImagesUtils.getImageDescriptor("palette/toolbar.png"),
				ImagesUtils.getImageDescriptor("palette/toolbar.png"));
		return combined;
	}
	public EditPart createEditPart(Object model){
		if(model instanceof Toolbar)
			return new ToolbarPart();
		return null;
	}
	public Class<? extends AuroraComponent> clazz() {
		return Toolbar.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = Toolbar.TOOLBAR;
		if (t.equalsIgnoreCase(type)) {
			Toolbar c = new Toolbar();
			c.setType(t);
			return c;
		}
		return null;
	}

}
