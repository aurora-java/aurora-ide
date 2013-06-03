package aurora.ide.meta.gef.editors.components.template;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.Dataset;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.Toolbar;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class GridEditorCreator extends ComponentCreator {

	public GridEditorCreator() {
	}

	public PaletteEntry createPaletteEntry() {

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"Grid(Editor)", "Create a  Grid Sample With Editors",
				Grid.class, new SimpleFactory(Grid.class) {
					public Object getNewObject() {
						return createGrid();
					}
				}, PrototypeImagesUtils.getImageDescriptor("palette/grid.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/grid.png"));
		return combined;
	}

	public Grid createGrid() {
		Grid grid = new Grid();
		GridColumn col = new GridColumn();
		col.setEditor(Input.TEXT);
		grid.addCol(col);
		col = new GridColumn();
		col.setEditor(Input.NUMBER);
		grid.addCol(col);
		col = new GridColumn();
		col.setEditor(Input.DATE_PICKER);
		grid.addCol(col);
		col = new GridColumn();
		col.setEditor(Input.Combo);
		grid.addCol(col);
		col = new GridColumn();
		col.setEditor(Input.LOV);
		grid.addCol(col);
		Toolbar tl = new Toolbar();
		Button child = new Button();
		child.setButtonType(Button.ADD);
		tl.addChild(child);
		child = new Button();
		child.setButtonType(Button.DELETE);
		tl.addChild(child);
		grid.setPropertyValue(ComponentInnerProperties.TOOLBAR, tl);
		grid.setPropertyValue(ComponentProperties.navBarType,
				Grid.NAVBAR_COMPLEX);
		grid.getDataset().setPropertyValue(ComponentProperties.selectionModel,
				Dataset.SELECT_MULTI);
		return grid;
	}
}
