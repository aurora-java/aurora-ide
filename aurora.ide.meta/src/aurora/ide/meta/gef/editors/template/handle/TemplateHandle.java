package aurora.ide.meta.gef.editors.template.handle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.InitModel;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.QueryDataSet;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.search.core.Util;

public abstract class TemplateHandle {
	protected ViewDiagram viewDiagram;
	protected Map<BMReference, List<Container>> modelRelated;
	protected Map<BMReference, List<TabItem>> initModelRelated;
	protected Map<BMReference, String> queryModelRelated;
	protected Map<String, AuroraComponent> auroraComponents;
	private List<Grid> grids;
	private List<TabItem> refTabItems;

	public TemplateHandle() {
		initModelRelated = TemplateHelper.getInstance().getInitModelRelated();
		queryModelRelated = TemplateHelper.getInstance().getQueryModelRelated();
		auroraComponents = TemplateHelper.getInstance().getAuroraComponents();
		modelRelated = TemplateHelper.getInstance().getModelRelated();
		grids = new ArrayList<Grid>();
		refTabItems = new ArrayList<TabItem>();
	}

	public void fill(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
		for (BMReference bm : modelRelated.keySet()) {
			for (Container ac : modelRelated.get(bm)) {
				fillContainer(ac, bm, true);
			}
		}

		for (BMReference bm : queryModelRelated.keySet()) {
			AuroraComponent ac = auroraComponents.get(queryModelRelated.get(bm));
			if (ac instanceof Container) {
				fillQueryBox((Container) ac, bm);
			}
		}

		for (BMReference bm : initModelRelated.keySet()) {
			for (TabItem ac : initModelRelated.get(bm)) {
				fillTabItem(ac, bm);
			}
		}
	}

	protected void fillBox(Container ac, BMReference bm) {
		ac.getChildren().clear();
		for (CompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm.getModel()))) {
			Input input = new Input();
			input.setName(map.getString("name"));
			input.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
			input.setType(GefModelAssist.getTypeNotNull(map));
			((Container) ac).addChild(input);
		}
	}

	protected String getBmPath(IFile bm) {
		if (bm == null) {
			return "";
		}
		String s = Util.toPKG(bm.getFullPath());
		if (s.endsWith(".bm")) {
			s = s.substring(0, s.lastIndexOf(".bm"));
		}
		return s;
	}

	protected void fillContainer(Container ac, BMReference bm, boolean isReadOnly) {
		Dataset ds = ac.getDataset();
		String s = getBmPath(bm.getModel());
		ds.setModel(s);
		ac.setDataset(ds);
		ac.setSectionType(Container.SECTION_TYPE_RESULT);
		if (ac instanceof Grid) {
			fillGrid((Grid) ac, bm.getModel(), isReadOnly);
		} else {
			fillBox(ac, bm);
		}
		// ac.setPropertyValue(Container.WIDTH, 226);
	}

	protected void fillGrid(Grid grid, IFile bm, boolean isReadOnly) {
		for (int i = 0; i < grid.getChildren().size(); i++) {
			if (grid.getChildren().get(i) instanceof GridColumn) {
				grid.getChildren().remove(i);
				i--;
			}
		}

		for (CompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm))) {
			GridColumn gc = new GridColumn();
			gc.setName(map.getString("name"));
			gc.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
			if (!isReadOnly) {
				gc.setEditor(GefModelAssist.getTypeNotNull(map));
			}
			grid.addCol(gc);
		}
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
		grids.add(grid);
	}

	protected void fillQueryBox(Container ac, BMReference bm) {
		if (ac.getSectionType() == null || "".equals(ac.getSectionType())) {
			ac.setSectionType(Container.SECTION_TYPE_QUERY);
			String s = getBmPath(bm.getModel());
			QueryDataSet ds = new QueryDataSet();
			ds.setModel(s);
			ac.setDataset(ds);
		}

		CompositeMap map = GefModelAssist.getModel(bm.getModel());
		for (CompositeMap queryMap : GefModelAssist.getQueryFields(GefModelAssist.getModel(bm.getModel()))) {
			Input input = new Input();
			CompositeMap fieldMap = GefModelAssist.getCompositeMap(map.getChild("fields"), "name", queryMap.getString("field"));
			if (fieldMap == null) {
				fieldMap = queryMap;
			}
			input.setName(fieldMap.getString("name"));
			input.setPrompt(fieldMap.getString("prompt") == null ? fieldMap.getString("name") : fieldMap.getString("prompt"));
			input.setType(GefModelAssist.getTypeNotNull(fieldMap));
			ac.addChild(input);
		}
	}

	protected void fillTabItem(TabItem ac, BMReference bm) {
		String s = getBmPath(bm.getModel());
		InitModel m = new InitModel();
		m.setPath(s);
		ac.getTabRef().setInitModel(m);
		viewDiagram.getInitModels().add(m);
		refTabItems.add(ac);
	}

	public List<Grid> getGrids() {
		return grids;
	}

	public List<TabItem> getRefTabItems() {
		return refTabItems;
	}

}
