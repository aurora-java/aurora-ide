package aurora.ide.meta.gef.editors.template.handle;

import java.util.List;
import java.util.Map;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class UpdateTemplateHandle extends TemplateHandle {

	private Map<BMReference, List<AuroraComponent>> initModeRelated;

	public UpdateTemplateHandle() {
		initModeRelated = TemplateHelper.getInstance().getInitModelRelated();
	}

	public void fill(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
		for (BMReference bm : modelRelated.keySet()) {
			for (Container ac : modelRelated.get(bm)) {
				fillContainer(ac, bm, false);
			}
		}
	}

}
