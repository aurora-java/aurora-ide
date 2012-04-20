package aurora.ide.meta.gef.editors.template.handle;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.QueryDataSet;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.search.core.Util;
import aurora.presentation.component.std.DataSet;

public abstract class TemplateHandle {
	protected ViewDiagram viewDiagram;
	protected Map<BMReference, List<Container>> modelRelated;

	public TemplateHandle() {
		modelRelated = TemplateHelper.getInstance().getModelRelated();
	}

	public abstract void fill(ViewDiagram viewDiagram);

	protected void fillBox(Container ac, BMReference bm) {
		ac.getChildren().clear();
		for (CommentCompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm.getModel()))) {
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

		for (CommentCompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm))) {
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
	}
	
	protected void fillQueryBox(Container ac, BMReference bm) {
		if (ac.getSectionType() == null || "".equals(ac.getSectionType())) {
			ac.setSectionType(Container.SECTION_TYPE_QUERY);
			String s = getBmPath(bm.getModel());
			QueryDataSet ds = new QueryDataSet();
			ds.setModel(s);
			ac.setDataset(ds);
		}

		CommentCompositeMap map = GefModelAssist.getModel(bm.getModel());
		for (CommentCompositeMap queryMap : GefModelAssist.getQueryFields(GefModelAssist.getModel(bm.getModel()))) {
			Input input = new Input();
			CommentCompositeMap fieldMap = GefModelAssist.getCompositeMap((CommentCompositeMap) map.getChild("fields"), "name", queryMap.getString("field"));
			if (fieldMap == null) {
				fieldMap = queryMap;
			}
			input.setName(fieldMap.getString("name"));
			input.setPrompt(fieldMap.getString("prompt") == null ? fieldMap.getString("name") : fieldMap.getString("prompt"));
			input.setType(GefModelAssist.getTypeNotNull(fieldMap));
			ac.addChild(input);
		}
	}
}
