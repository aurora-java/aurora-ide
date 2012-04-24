package aurora.ide.meta.gef.editors.template.handle;

import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Label;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class DisplayTemplateHandle extends TemplateHandle {

	private Map<String, String> refRelat;

	@Override
	public void fill(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
		for (BMReference bm : modelRelated.keySet()) {
			for (Container ac : modelRelated.get(bm)) {
				BMCompositeMap bmc = new BMCompositeMap(bm.getModel());
				refRelat = getReferenceRelation(bmc);
				fillContainer(ac, bm, bmc);
			}
		}
	}

	@Override
	protected void fillBox(Container ac, BMCompositeMap bmc) {
		ac.getChildren().clear();
		for (CompositeMap map : getFieldsWithoutPK(bmc)) {
			Label label = new Label();
			String name = map.getString("name");
			label.setName(name);
			label.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
			((Container) ac).addChild(label);
			for (String n : refRelat.keySet()) {
				if (name != null && name.equals(refRelat.get(n))) {
					Label l = new Label();
					l.setName(n);
					l.setPrompt(label.getPrompt());
					((Container) ac).addChild(l);
				}
			}
		}
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
					g.setPrompt(gc.getPrompt());
					grid.addCol(g);
				}
			}
		}
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
		grids.add(grid);
	}
}
