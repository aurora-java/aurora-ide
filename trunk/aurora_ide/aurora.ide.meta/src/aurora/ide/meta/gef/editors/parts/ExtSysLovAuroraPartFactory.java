package aurora.ide.meta.gef.editors.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import aurora.ide.meta.extensions.ComponentFactory;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.ScreenBody;

/**
 */
public class ExtSysLovAuroraPartFactory implements EditPartFactory {

	private EditorMode editorMode;

	public ExtSysLovAuroraPartFactory(EditorMode editorMode) {
		this.editorMode = editorMode;
	}

	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof ScreenBody) {
			EditPart part = new ViewDiagramPart();
			part.setParent(context);
			part.setModel(model);
			if (part instanceof ComponentPart) {
				((ComponentPart) part).setEditorMode(editorMode);
			}
			return part;
		}
		if (model instanceof GridColumn) {
			if (GridColumn.GRIDCOLUMN.equals(((GridColumn) model)
					.getComponentType())) {
				EditPart part = new SysLovGridColumnPart();
				part.setParent(context);
				part.setModel(model);
				if (part instanceof ComponentPart) {
					((ComponentPart) part).setEditorMode(editorMode);
				}
				return part;
			}
		}
		EditPart createEditPart = ComponentFactory
				.createEditPart((AuroraComponent) model);
		if (createEditPart != null) {
			createEditPart.setParent(context);
			createEditPart.setModel(model);
			if (createEditPart instanceof ComponentPart) {
				((ComponentPart) createEditPart).setEditorMode(editorMode);
			}
		}
		return createEditPart;
	}
}
