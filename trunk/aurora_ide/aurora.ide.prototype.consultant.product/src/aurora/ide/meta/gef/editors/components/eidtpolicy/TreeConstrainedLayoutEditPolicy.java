package aurora.ide.meta.gef.editors.components.eidtpolicy;

import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import aurora.ide.meta.gef.editors.layout.GraphLayoutManager;
import aurora.ide.meta.gef.editors.parts.ComponentPart;

public class TreeConstrainedLayoutEditPolicy extends
		ConstrainedLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		// cant move and drop&drag
		return null;
	}

	@Override
	protected Object getConstraintFor(Point point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getConstraintFor(Rectangle rect) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showSourceFeedback(Request request) {
		if (TreeExpandSupportEditPolicy.SIZE_CHANGED.equals(request.getType()))
			sizeChanged(request);
		super.showSourceFeedback(request);
	}

	private void sizeChanged(Request request) {
		AbstractGraphicalEditPart node = (AbstractGraphicalEditPart) request
				.getExtendedData().get(request.getType());
		sizeChanged(node);
	}

	private void sizeChanged(AbstractGraphicalEditPart node) {
//		GraphLayoutManager.layout((ComponentPart) node.getParent());
//		LayoutManager layoutManager = this.getHostFigure().getLayoutManager();
//		layoutManager.setConstraint(node.getFigure(), getHostFigure());
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public Command getCommand(final Request request) {
		if (TreeExpandSupportEditPolicy.SIZE_CHANGED.equals(request.getType())) {
			return new Command("tree node size changed") {
				public void execute() {
					sizeChanged(request);
				}
			};
		}

		return super.getCommand(request);
	}
}
