package aurora.ide.meta.gef.editors.template.handle;

import java.util.Map;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class SerachTemplateHandle extends TemplateHandle {

	private Map<String, String> refRelat;

	public void fill(ViewDiagram viewDiagram) {
		super.fill(viewDiagram);
	}

	@Override
	protected void fillBox(Container ac, BMReference bm) {
		ac.getChildren().clear();
		BMCompositeMap bmc = new BMCompositeMap(bm.getModel());
		if (refRelat == null) {
			refRelat = getReferenceRelation(bmc);
		}
		for (CompositeMap map : bmc.getFields()) {
			Input input = AuroraModelFactory.createComponent(GefModelAssist.getTypeNotNull(map));
			String name = map.getString("name");
			if (refRelat.containsKey(name)) {
				name = refRelat.get(name);
			}
			input.setName(name);
			String prompt = map.getString("prompt");
			prompt = prompt == null ? map.getString("name") : prompt;
			input.setPrompt(prompt);
			((Container) ac).addChild(input);
		}
	}

	@Override
	protected void fillGrid(Grid grid, IFile bm, boolean isReadOnly) {
		for (int i = 0; i < grid.getChildren().size(); i++) {
			if (grid.getChildren().get(i) instanceof GridColumn) {
				grid.getChildren().remove(i);
				i--;
			}
		}
		grid.getCols().clear();
		BMCompositeMap bmc = new BMCompositeMap(bm);
		if (refRelat == null) {
			refRelat = getReferenceRelation(bmc);
		}
		for (CompositeMap map : bmc.getFields()) {
			GridColumn gc = new GridColumn();
			String name = map.getString("name");
			if (refRelat.containsKey(name)) {
				name = refRelat.get(name);
			}
			gc.setName(name);
			String prompt = map.getString("prompt");
			prompt = prompt == null ? map.getString("name") : prompt;
			gc.setPrompt(prompt);
			if (!isReadOnly) {
				gc.setEditor(GefModelAssist.getTypeNotNull(map));
			}
			grid.addCol(gc);
		}
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
		grids.add(grid);
	}
}
