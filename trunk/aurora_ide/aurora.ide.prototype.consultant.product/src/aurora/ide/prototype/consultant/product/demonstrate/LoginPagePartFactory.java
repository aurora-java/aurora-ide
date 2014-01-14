package aurora.ide.prototype.consultant.product.demonstrate;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import aurora.ide.meta.extensions.ComponentFactory;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class LoginPagePartFactory implements EditPartFactory {

	private EditorMode editorMode;

	public LoginPagePartFactory(EditorMode editorMode) {
		this.editorMode = editorMode;
	}

	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof ScreenBody) {
			EditPart part = new LoginDiagramPart();
			part.setParent(context);
			part.setModel(model);
			if (part instanceof ComponentPart) {
				((ComponentPart) part).setEditorMode(editorMode);
			}
			return part;
		}
		if (model instanceof Button) {
			if (Button.BUTTON.equals(((Button) model)
					.getComponentType())) {
				EditPart part = new LoginButtonPart();
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
