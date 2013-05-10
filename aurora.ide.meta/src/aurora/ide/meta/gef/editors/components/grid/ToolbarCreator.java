package aurora.ide.meta.gef.editors.components.grid;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.ToolbarPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Toolbar;

public class ToolbarCreator extends ComponentCreator {

	public ToolbarCreator() {
	}
	public PaletteEntry createPaletteEntry(){
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry("Toolbar",
				"Create a  Toolbar ", Toolbar.class, new SimpleFactory(
						Toolbar.class),
				PrototypeImagesUtils.getImageDescriptor("palette/toolbar.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/toolbar.png"));
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
			c.setComponentType(t);
			return c;
		}
		return null;
	}

}
