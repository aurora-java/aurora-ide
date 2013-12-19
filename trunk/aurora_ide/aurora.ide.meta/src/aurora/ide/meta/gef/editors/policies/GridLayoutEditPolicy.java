package aurora.ide.meta.gef.editors.policies;

import java.util.List;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DropRequest;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.models.commands.CreateComponentCommand;
import aurora.ide.meta.gef.editors.models.commands.MoveChildCmpCmd;
import aurora.ide.meta.gef.editors.models.commands.MoveRemoteChildCmpCmd;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.GridColumnPart;
import aurora.ide.meta.gef.editors.parts.GridPart;
import aurora.ide.meta.gef.editors.parts.GridSelectionColPart;
import aurora.ide.meta.gef.editors.parts.NavbarPart;
import aurora.ide.meta.gef.editors.parts.ToolbarPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.GridColumn;

public class GridLayoutEditPolicy extends FlowLayoutEditPolicy {

	private EditPart targetEditPart;

	protected Command getCreateCommand(CreateRequest request) {
		if (request.getNewObject() instanceof AuroraComponent) {
			EditPart host = getHost();
			Container parentModel = (Container) host.getModel();
			AuroraComponent ac = (AuroraComponent) request.getNewObject();
			if (!parentModel.isResponsibleChild(ac)) {
				return null;
			}
			EditPart reference = getInsertionReference(request);
			CreateComponentCommand cmd = new CreateComponentCommand();
			cmd.setTargetContainer(parentModel);
			cmd.setChild(ac);
			cmd.setReferenceModel((AuroraComponent) (reference == null ? null
					: reference.getModel()));
			return cmd;
		}
		return null;
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		targetEditPart = super.getTargetEditPart(request);
		if ((targetEditPart instanceof GridColumnPart)
				&& (request instanceof DropRequest)) {
			GridColumnPart gcp = (GridColumnPart) targetEditPart;
			GridColumnFigure figure = (GridColumnFigure) gcp.getFigure();
			GridColumn model = (GridColumn) gcp.getModel();
			Rectangle bounds = figure.getBounds().getCopy();
			figure.translateToAbsolute(bounds);
			if (((DropRequest) request).getLocation().y > bounds.y
					+ model.getHeadHight()) {
				targetEditPart = targetEditPart.getParent();
			}
		}
		// if (targetEditPart == null)
		// System.out.println("getTargetEditPart:" + null);
		// if (targetEditPart instanceof GridColumnPart)
		// System.out.println("getTargetEditPart:GridColumnPart  "
		// + ((GridColumnPart) targetEditPart).getModel().getPrompt());
		// else
		// System.out.println("getTargetEditPart:"
		// + targetEditPart.getClass().getSimpleName());
		return targetEditPart;
	}

	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		ResizeComponentEditPolicy p = new ResizeComponentEditPolicy();
		ComponentPart cp = (ComponentPart) child;
		p.setResizeDirections(cp.getResizeDirection());
		return p;
	}

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {
		if (getHost() == null)
			return null;
		MoveRemoteChildCmpCmd cmd = new MoveRemoteChildCmpCmd();
		cmd.setComponentToMove((AuroraComponent) child.getModel());
		if (getHost().getModel() instanceof Container) {
			Container dest = (Container) getHost().getModel();
			AuroraComponent ac = (AuroraComponent) child.getModel();
			if (!dest.isResponsibleChild(ac))
				return null;
			cmd.setTargetContainer(dest);
		}
		cmd.setReferenceComponent(after == null ? null
				: (AuroraComponent) after.getModel());
		return cmd;
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		MoveChildCmpCmd cmd = new MoveChildCmpCmd();
		cmd.setComponentToMove((AuroraComponent) child.getModel());
		cmd.setReferenceComponent(after == null ? null
				: (AuroraComponent) after.getModel());
		return cmd;
	}

	protected boolean isLayoutHorizontal() {
		return getHost().getModel() instanceof GridColumn;
	}

	@Override
	public Command getCommand(Request request) {
		if (MetaPlugin.isDemonstrate)
			return null;
		return super.getCommand(request);
	}

	public void showTargetFeedback(Request request) {
		if (MetaPlugin.isDemonstrate)
			return ;
		if (getHost() instanceof GridPart) {
			if ((request instanceof CreateRequest || request instanceof ChangeBoundsRequest)
					&& !(REQ_RESIZE.equals(request.getType()))) {
				ComponentPart ref = (ComponentPart) getInsertionReference(request);
				if (ref == null || (ref instanceof ToolbarPart)
						|| (ref instanceof NavbarPart)
						|| (ref instanceof GridSelectionColPart)) {
					List<?> children = getHost().getChildren();
					ComponentPart last = null;
					for (Object o : children) {
						if ((o instanceof ToolbarPart)
								|| (o instanceof NavbarPart)) {
							break;
						}
						last = (ComponentPart) o;
					}
					if (last != null) {
						Rectangle rect = last.getFigure().getBounds()
								.getShrinked(-4, -2);
						Polyline linefb = getLineFeedback();
						linefb.setStart(rect.getTopRight());
						linefb.setEnd(rect.getBottomRight());
						return;
					}
				}
			}
		}
		super.showTargetFeedback(request);
	}
}