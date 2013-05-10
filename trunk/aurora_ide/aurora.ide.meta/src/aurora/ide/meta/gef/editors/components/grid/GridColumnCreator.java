package aurora.ide.meta.gef.editors.components.grid;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.GridColumnPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.GridColumn;

public class GridColumnCreator extends ComponentCreator {

	public GridColumnCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Grid Column", "Create a  Grid Column", GridColumn.class,
				new SimpleFactory(GridColumn.class),
				PrototypeImagesUtils.getImageDescriptor("palette/column.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/column.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof GridColumn)
			return new GridColumnPart();
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return GridColumn.class;
	}
	public AuroraComponent createComponent(String type) {
		String t = GridColumn.GRIDCOLUMN;
		if (t.equalsIgnoreCase(type)) {
			GridColumn c = new GridColumn();
			c.setComponentType(t);
			return c;
		}
		return null;
	}
}
