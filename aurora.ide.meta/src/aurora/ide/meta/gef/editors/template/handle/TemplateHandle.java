package aurora.ide.meta.gef.editors.template.handle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMCompositeMap;
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
	protected List<Grid> grids;
	protected List<TabItem> refTabItems;

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
				fillContainer(ac, bm);
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

	protected void fillBox(Container ac, BMCompositeMap bmc) {
		ac.getChildren().clear();
		for (CompositeMap map : getFieldsWithoutPK(bmc)) {
			Input input = AuroraModelFactory.createComponent(GefModelAssist.getTypeNotNull(map));
			input.setName(map.getString("name"));
			input.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
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

	protected void fillContainer(Container ac, BMReference bm) {
		Dataset ds = ac.getDataset();
		String s = getBmPath(bm.getModel());
		ds.setModel(s);
		ac.setDataset(ds);
		ac.setSectionType(Container.SECTION_TYPE_RESULT);
		BMCompositeMap bmc = new BMCompositeMap(bm.getModel());
		if (ac instanceof Grid) {
			fillGrid((Grid) ac, bmc);
		} else {
			fillBox(ac, bmc);
		}
	}

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
		}
		grid.setNavbarType(Grid.NAVBAR_COMPLEX);
		grid.setSelectionMode(ResultDataSet.SELECT_MULTI);
		grids.add(grid);
	}

	protected GridColumn createGridColumn(CompositeMap map) {
		GridColumn gc = new GridColumn();
		gc.setName(map.getString("name"));
		String prompt = map.getString("prompt");
		prompt = prompt == null ? map.getString("name") : prompt;
		gc.setPrompt(prompt);
		return gc;
	}

	protected void fillQueryBox(Container ac, BMReference bm) {
		if (ac.getSectionType() == null || "".equals(ac.getSectionType())) {
			ac.setSectionType(Container.SECTION_TYPE_QUERY);
			String s = getBmPath(bm.getModel());
			QueryDataSet ds = new QueryDataSet();
			ds.setModel(s);
			ac.setDataset(ds);
		}
		ac.getChildren().clear();
		BMCompositeMap bmc = new BMCompositeMap(bm.getModel());
		for (CompositeMap queryMap : getQueryFields(bmc)) {
			Input input = AuroraModelFactory.createComponent(GefModelAssist.getTypeNotNull(queryMap));
			input.setName(queryMap.getString("name"));
			input.setPrompt(queryMap.getString("prompt") == null ? queryMap.getString("name") : queryMap.getString("prompt"));
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

	protected Map<String, String> getReferenceRelation(BMCompositeMap bmc) {
		Map<String, String> refRelat = new HashMap<String, String>();
		for (CompositeMap ref : bmc.getRefFields()) {
			String relationName = ref.getString("relationName");
			if (relationName == null) {
				continue;
			}
			for (CompositeMap relat : bmc.getRelations()) {
				if (relationName.equals(relat.getString("name"))) {
					for (Object refer : relat.getChildsNotNull()) {
						refRelat.put(((CompositeMap) refer).getString("localfield"), ref.getString("name"));
					}
				}
			}
		}
		return refRelat;
	}

	protected List<CompositeMap> getQueryFields(BMCompositeMap bmc) {
		List<CompositeMap> qfs = bmc.getQueryFields();
		List<CompositeMap> fields = getFieldsWithoutPK(bmc);
		List<CompositeMap> queryFields = new ArrayList<CompositeMap>();
		for (CompositeMap qf : qfs) {
			for (CompositeMap field : fields) {
				if (field.getString("name").equals(qf.getString("field"))) {
					queryFields.add(field);
				}
			}
		}
		return queryFields;
	}

	protected List<CompositeMap> getFieldsWithoutPK(BMCompositeMap bmc) {
		List<CompositeMap> pks = bmc.getPrimaryKeys();
		List<CompositeMap> fields = bmc.getFields();
		List<CompositeMap> fieldsWithoutPK = new ArrayList<CompositeMap>();
		for (CompositeMap pk : pks) {
			for (CompositeMap field : fields) {
				if (field.getString("name").equals(pk.getString("name"))) {
					continue;
				}
				fieldsWithoutPK.add(field);
			}
		}
		return fieldsWithoutPK;
	}
}
