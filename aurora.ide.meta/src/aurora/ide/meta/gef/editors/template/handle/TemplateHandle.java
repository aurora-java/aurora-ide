package aurora.ide.meta.gef.editors.template.handle;

import org.eclipse.core.resources.IFile;

import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.search.core.Util;

public abstract class TemplateHandle {
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
}
