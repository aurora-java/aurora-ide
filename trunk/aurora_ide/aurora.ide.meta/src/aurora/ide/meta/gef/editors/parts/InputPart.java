package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;

import aurora.ide.meta.gef.editors.figures.InputField;
import aurora.ide.meta.gef.editors.models.Input;

public class InputPart extends ComponentPart {

	private String type;

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		InputField inputField = new InputField();
		Input model = (Input) getModel();
		inputField.setModel(model);
		return inputField;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	@Override
	public int getResizeDirection() {
		return EAST_WEST;
	}

}
