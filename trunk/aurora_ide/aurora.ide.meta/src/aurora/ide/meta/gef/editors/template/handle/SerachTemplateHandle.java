package aurora.ide.meta.gef.editors.template.handle;

import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class SerachTemplateHandle extends TemplateHandle {

	private Map<String, List<String>> refRelat;

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
		outer: for (CompositeMap map : getFieldsWithoutPK(bmc)) {
			String filedName = map.getString("name");
			for (String relationName : refRelat.keySet()) {
				if (refRelat.get(relationName).contains(filedName)) {
					for (CompositeMap ref : bmc.getRefFields()) {
						if (relationName.equals(ref.getString("relationName"))) {
							GridColumn gc = createGridColumn(map, ref);
							grid.addCol(gc);
						}
					}
					continue outer;
				}
			}
			GridColumn gc = createGridColumn(map);
			grid.addCol(gc);
		}
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
		grids.add(grid);
	}

	private GridColumn createGridColumn(CompositeMap map, CompositeMap ref) {
		GridColumn gc = new GridColumn();
		gc.setName(ref.getString("name"));
		gc.setPrompt(map.getString("prompt"));
		if (isDateType(map)) {
			Renderer r = new Renderer();
			r.setFunctionName("Aurora.formatDate");
			r.setRendererType(Renderer.INNER_FUNCTION);
			gc.setRenderer(r);
		}
		return gc;
	}
}
