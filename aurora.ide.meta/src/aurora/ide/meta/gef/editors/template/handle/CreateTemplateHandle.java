package aurora.ide.meta.gef.editors.template.handle;

import java.util.List;
import java.util.Map;

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

public class CreateTemplateHandle extends TemplateHandle {

	private ViewDiagram viewDiagram;
	private Map<BMReference, List<Container>> modelRelated;

	// private Map<BMReference, List<AuroraComponent>> initModeRelated;

	public void fill(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
		modelRelated = TemplateHelper.getInstance().getModelRelated();

		for (BMReference bm : modelRelated.keySet()) {
			for (Container ac : modelRelated.get(bm)) {
				fillContainer(ac, bm);
			}
		}
		// List<RowCol> rowCols=new ArrayList<RowCol>();
		// for (AuroraComponent ac : viewDiagram.getChildren()) {
		// if (ac instanceof RowCol) {
		// rowCols.add((RowCol) ac);
		// }
		// }
		// if(!){
		//
		// }
	}

	// private void fillInitModel(TabItem ac, BMReference bm) {
	// String s = getBmPath(bm.getModel());
	// InitModel m = new InitModel();
	// m.setPath(s);
	// ac.getTabRef().setInitModel(m);
	// viewDiagram.getInitModels().add(m);
	// initModels.add(m);
	// ac.getTabRef().setUrl("11");
	// ref.setUrl(((aurora.ide.meta.gef.editors.template.TabRef)
	// c).getUrl());
	// ref.addAllParameter(((aurora.ide.meta.gef.editors.template.TabRef)
	// c).getParas());
	// }

	protected void fillContainer(Container ac, BMReference bm) {
		ResultDataSet ds = new ResultDataSet();
		String s = getBmPath(bm.getModel());
		ds.setOwner(ac);
		ds.setModel(s);
		ac.setDataset(ds);
		ac.setSectionType(Container.SECTION_TYPE_RESULT);
		if (ac instanceof Grid) {
			fillGrid((Grid) ac, bm.getModel(), false);
		} else {
			fillBox(ac, bm);
		}
		ac.setPropertyValue(Container.WIDTH, 1);
	}

	// else if (viewDiagram.getTemplateType().equals(Template.TYPE_DISPLAY)) {
	// for (CommentCompositeMap map :
	// GefModelAssist.getFields(GefModelAssist.getModel(bm.getModel()))) {
	// aurora.ide.meta.gef.editors.models.Label label = new
	// aurora.ide.meta.gef.editors.models.Label();
	// label.setName(map.getString("name"));
	// label.setPrompt(map.getString("prompt") == null ? map.getString("name") :
	// map.getString("prompt"));
	// if (GefModelAssist.getType(map) != null) {
	// label.setType(GefModelAssist.getType(map));
	// }
	// ((Container) ac).addChild(label);
	// }
	// }
}
