package aurora.ide.meta.gef.editors.components.box;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.parts.QueryFormPart;
import aurora.ide.meta.gef.editors.parts.QueryFormToolBarPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.QueryForm;
import aurora.plugin.source.gen.screen.model.QueryFormToolBar;

public class QueryFormCreator extends ComponentCreator {

	public QueryFormCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"QueryForm", "Create a  QueryForm", QueryForm.class,
				new SimpleFactory(QueryForm.class),
				PrototypeImagesUtils.getImageDescriptor("palette/form.png"),
				PrototypeImagesUtils.getImageDescriptor("palette/form.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof QueryForm)
			return new QueryFormPart();
		else if (model instanceof QueryFormToolBar)
			return new QueryFormToolBarPart();
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return QueryForm.class;
	}
}
