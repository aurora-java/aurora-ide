package aurora.ide.meta.gef.editors.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy;

import aurora.ide.meta.gef.editors.figures.GridSelectionColFigure;
import aurora.ide.meta.gef.editors.models.GridSelectionCol;

public class GridSelectionColPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		GridSelectionColFigure figure = new GridSelectionColFigure();
		figure.setModel(getModel());
		return figure;
	}

	public GridSelectionCol getModel() {
		return (GridSelectionCol) super.getModel();
	}

	public GridSelectionColFigure getFigure() {
		return (GridSelectionColFigure) super.getFigure();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy("translate_selection_to_parent",
				new SelectionHandlesEditPolicy() {

					@Override
					protected List createSelectionHandles() {
						return Collections.EMPTY_LIST;
					}

					public EditPart getTargetEditPart(Request request) {
						if (RequestConstants.REQ_SELECTION.equals(request
								.getType()))
							return getHost().getParent();
						return null;
					}

				});
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	@Override
	public int getResizeDirection() {
		return NONE;
	}
}
