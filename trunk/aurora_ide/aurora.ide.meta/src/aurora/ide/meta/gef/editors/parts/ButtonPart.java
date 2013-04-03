package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

import aurora.ide.meta.gef.editors.figures.ButtonFigure;
import aurora.plugin.source.gen.screen.model.Button;

public class ButtonPart extends ComponentPart {

	private String type;

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		ButtonFigure buttonFigure = new ButtonFigure();
		Button model = getModel();
		buttonFigure.setModel(model);
		return buttonFigure;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		String title = getModel().getTitle();
		getFigure()
				.setToolTip(
						(title != null && title.length() > 0) ? new Label(title)
								: null);
		super.refreshVisuals();
	}

	public Button getModel() {
		return (Button) super.getModel();
	}

	public ButtonFigure getFigure() {
		return (ButtonFigure) super.getFigure();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		// // installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
		// // new NodeDirectEditPolicy());
		// String mode = this.getEditorMode().getMode();
		// if (EditorMode.Template.equals(mode)) {
		// installEditPolicy(EditPolicy.COMPONENT_ROLE,
		// new TemplateNodeEditPolicy());
		// }
		// if (EditorMode.None.equals(mode))
		// installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}

	@Override
	public int getResizeDirection() {
		return NSEW;
	}

}
