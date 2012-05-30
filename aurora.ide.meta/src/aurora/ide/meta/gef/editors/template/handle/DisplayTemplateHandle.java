package aurora.ide.meta.gef.editors.template.handle;

import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Label;
import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class DisplayTemplateHandle extends TemplateHandle {

	private Map<String, List<String>> refRelat;

	@Override
	public void fill(ViewDiagram viewDiagram) {
		setColNum(viewDiagram, 1);
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
		outer: for (CompositeMap map : getFieldsWithoutPK(bmc)) {
			String renderer = null;
			if (isDateType(map)) {
				renderer = "Aurora.formatDate";
			}
			String filedName = map.getString("name");
			for (String relationName : refRelat.keySet()) {
				if (refRelat.get(relationName).contains(filedName)) {
					for (CompositeMap ref : bmc.getRefFields()) {
						if (relationName.equals(BMCompositeMap.getMapAttribute(ref, "relationName"))) {
							ac.addChild(createLabel(map.getString("prompt"), ref.getString("name"), renderer));
						}
					}
					continue outer;
				}
			}
			ac.addChild(createLabel(map.getString("prompt"), filedName, renderer));
		}

	}

	private Label createLabel(String prompt, String name, String renderer) {
		Label label = new Label();
		label.setName(name);
		label.setPrompt(prompt);
		if (renderer != null) {
			label.setRenderer(renderer);
		}
		return label;
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
						if (relationName.equals(BMCompositeMap.getMapAttribute(ref, "relationName"))) {
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
