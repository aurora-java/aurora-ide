package aurora.ide.meta.gef.editors.components.box;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.QueryFormBodyPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.QueryFormBody;

public class QueryFormBodyCreator extends ComponentCreator {

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"FormBody", "Create a  FormBody", QueryFormBody.class,
				new SimpleFactory(QueryFormBody.class),
				PrototypeImagesUtils.getImageDescriptor("palette/form.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/form.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof QueryFormBody)
			return new QueryFormBodyPart();
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return QueryFormBody.class;
	}
}
