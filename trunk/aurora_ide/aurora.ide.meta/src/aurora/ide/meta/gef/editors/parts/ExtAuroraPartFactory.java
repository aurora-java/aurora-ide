package aurora.ide.meta.gef.editors.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import aurora.ide.meta.extensions.ComponentFactory;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.ScreenBody;

/**
 */
public class ExtAuroraPartFactory implements EditPartFactory {

	private EditorMode editorMode;

	public ExtAuroraPartFactory(EditorMode editorMode) {
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
		// List<ExtensionComponent> extensionComponents = ExtensionLoader
		// .getExtensionComponents();
		//
		// for (ExtensionComponent ec : extensionComponents) {
		// EditPart createEditPart = ec.getCreator().createEditPart(model);
		// if (createEditPart == null) {
		// continue;
		// }
		// createEditPart.setParent(context);
		// createEditPart.setModel(model);
		// if (createEditPart instanceof ComponentPart) {
		// ((ComponentPart) createEditPart).setEditorMode(editorMode);
		// }
		// return createEditPart;
		// }
		// return null;
	}
}
