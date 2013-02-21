package aurora.ide.meta.gef.editors.components.box;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.requests.SimpleFactory;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.QueryForm;
import aurora.ide.meta.gef.editors.models.QueryFormBody;
import aurora.ide.meta.gef.editors.models.QueryFormToolBar;
import aurora.ide.meta.gef.editors.parts.QueryFormBodyPart;
import aurora.ide.meta.gef.editors.parts.QueryFormPart;
import aurora.ide.meta.gef.editors.parts.QueryFormToolBarPart;

public class QueryFormCreator extends ComponentCreator {

	public QueryFormCreator() {
	}

	public PaletteEntry createPaletteEntry() {
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"QueryForm", "Create a  QueryForm", QueryForm.class,
				new SimpleFactory(QueryForm.class),
				ImagesUtils.getImageDescriptor("palette/form.png"),
				ImagesUtils.getImageDescriptor("palette/form.png"));
		return combined;
	}

	public EditPart createEditPart(Object model) {
		if (model instanceof QueryForm)
			return new QueryFormPart();
		else if (model instanceof QueryFormToolBar)
			return new QueryFormToolBarPart();
		else if (model instanceof QueryFormBody)
			return new QueryFormBodyPart();
		return null;
	}

	public Class<? extends AuroraComponent> clazz() {
		return QueryForm.class;
	}
}
