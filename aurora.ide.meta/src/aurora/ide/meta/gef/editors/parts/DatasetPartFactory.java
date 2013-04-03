package aurora.ide.meta.gef.editors.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import aurora.plugin.source.gen.screen.model.Dataset;
import aurora.plugin.source.gen.screen.model.ScreenBody;

/**
 */
public class DatasetPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof ScreenBody)
			part = new DatasetDiagramPart();
		// else if (model instanceof Label)
		// part = new LabelPart();
		else if (model instanceof Dataset) {
			part = new DatasetPart();
			
		}
		if (part != null)
			part.setModel(model);
		return part;
	}
}
