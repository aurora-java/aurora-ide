package aurora.ide.meta.gef.editors.template.handle;

import java.util.List;
import java.util.Map;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;

public class UpdateTemplateHandle extends TemplateHandle {

	private ViewDiagram viewDiagram;
	private Map<BMReference, List<Container>> modelRelated;
	private Map<BMReference, List<AuroraComponent>> initModeRelated;

	public void fill(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
	}

}
