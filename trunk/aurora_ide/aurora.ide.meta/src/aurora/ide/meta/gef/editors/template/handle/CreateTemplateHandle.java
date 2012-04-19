package aurora.ide.meta.gef.editors.template.handle;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class CreateTemplateHandle extends TemplateHandle {
	// private Map<BMReference, List<AuroraComponent>> initModeRelated;

	public void fill(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
		setRowColNum(viewDiagram);
		for (BMReference bm : modelRelated.keySet()) {
			for (Container ac : modelRelated.get(bm)) {
				fillContainer(ac, bm, false);
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

	private void setRowColNum(ViewDiagram viewDiagram) {
		List<BOX> rowCols = new ArrayList<BOX>();
		boolean hasContainer = false;
		for (AuroraComponent ac : viewDiagram.getChildren()) {
			if (ac instanceof BOX) {
				if (((BOX) ac).getChildren().size() == 0) {
					rowCols.add((BOX) ac);
				}
			} else {
				hasContainer = true;
			}
		}
		if (!hasContainer) {
			for (BOX rc : rowCols) {
				rc.setCol(1);
			}
		}
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
