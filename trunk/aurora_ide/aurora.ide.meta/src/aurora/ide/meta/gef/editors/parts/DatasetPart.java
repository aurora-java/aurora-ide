package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.figures.DatasetFigure;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;

public class DatasetPart extends ComponentPart {

	private String type;

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {

		Dataset model = getModel();
		DatasetFigure buttonFigure = new DatasetFigure(model);
		buttonFigure.setModel(model);
		return buttonFigure;
		// Label label = new Label();
		// label.setIcon(ImagesUtils.getImage("bm.gif"));
		// // model.getID
		// label.setText("dataset_id");
		// return label;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {

		super.refreshVisuals();
	}

	public Dataset getModel() {
		return (Dataset) super.getModel();
	}

	// public DatasetFigure getFigure() {
	// return (DatasetFigure) super.getFigure();
	// }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new NodeDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}

}
