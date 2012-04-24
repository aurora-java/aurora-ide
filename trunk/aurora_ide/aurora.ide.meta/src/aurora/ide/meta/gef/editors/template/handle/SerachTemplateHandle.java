package aurora.ide.meta.gef.editors.template.handle;

import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class SerachTemplateHandle extends TemplateHandle {

	private Map<String, String> refRelat;

	@Override
	public void fill(ViewDiagram viewDiagram) {
		super.fill(viewDiagram);
	}

	@Override
	protected void fillContainer(Container ac, BMReference bm, BMCompositeMap bmc) {
		refRelat = getReferenceRelation(bmc);
		super.fillContainer(ac, bm, bmc);
	}

	@Override
	protected void fillBox(Container ac, BMCompositeMap bmc) {
		super.fillBox(ac, bmc);
	}

	@Override
	protected void fillGrid(Grid grid, BMCompositeMap bmc) {
		for (int i = 0; i < grid.getChildren().size(); i++) {
			if (grid.getChildren().get(i) instanceof GridColumn) {
				grid.getChildren().remove(i);
				i--;
			}
		}
		grid.getCols().clear();
		for (CompositeMap map : getFieldsWithoutPK(bmc)) {
			GridColumn gc = createGridColumn(map);
			grid.addCol(gc);
			String name = map.getString("name");
			for (String n : refRelat.keySet()) {
				if (name != null && name.equals(refRelat.get(n))) {
					GridColumn g = new GridColumn();
					g.setName(n);
					String prompt = map.getString("prompt");
					prompt = prompt == null ? map.getString("name") : prompt;
					g.setPrompt(prompt);
					grid.addCol(g);
				}
			}
		}
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
		grids.add(grid);
	}
}
