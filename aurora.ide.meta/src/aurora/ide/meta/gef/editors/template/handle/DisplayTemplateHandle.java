package aurora.ide.meta.gef.editors.template.handle;

import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Label;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class DisplayTemplateHandle extends TemplateHandle {

	public void fill(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
		for (BMReference bm : modelRelated.keySet()) {
			for (Container ac : modelRelated.get(bm)) {
				fillContainer(ac, bm, true);
			}
		}
	}

	protected void fillBox(Container ac, BMReference bm) {
		ac.getChildren().clear();
		for (CommentCompositeMap map : GefModelAssist.getFields(GefModelAssist.getModel(bm.getModel()))) {
			Label label = new Label();
			label.setName(map.getString("name"));
			label.setPrompt(map.getString("prompt") == null ? map.getString("name") : map.getString("prompt"));
			((Container) ac).addChild(label);
		}
	}
}
