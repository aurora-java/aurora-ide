package aurora.ide.meta.gef.editors.components.grid;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.GridPart;
import aurora.ide.meta.gef.editors.parts.GridSelectionColPart;
import aurora.ide.meta.gef.editors.parts.NavbarPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridSelectionCol;
import aurora.plugin.source.gen.screen.model.Navbar;

public class GridCreator extends ComponentCreator {

	public GridCreator() {
	}

	public PaletteEntry createPaletteEntry() {

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Grid", "Create a  Grid", Grid.class, new SimpleFactory(
						Grid.class),
				PrototypeImagesUtils.getImageDescriptor("palette/grid.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/grid.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof Grid)
			return new GridPart();
		if (model instanceof Navbar)
			return new NavbarPart();
		if (model instanceof GridSelectionCol)
			return new GridSelectionColPart();
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return Grid.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = Grid.GRID;
		if (t.equalsIgnoreCase(type)) {
			Grid c = new Grid();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
