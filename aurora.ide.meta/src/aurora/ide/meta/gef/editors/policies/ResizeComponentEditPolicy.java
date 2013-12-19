package aurora.ide.meta.gef.editors.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.ResizableHandleKit;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.tools.ResizeTracker;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.ide.meta.gef.editors.models.commands.ResizeCmpCmd;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class ResizeComponentEditPolicy extends ResizableEditPolicy implements
		PositionConstants {
	private int resizeDirections = NSEW;
	private Label label = null;

	/**
	 * Constructs a new {@link ResizableEditPolicy}.
	 * 
	 * @since 3.7
	 */
	public ResizeComponentEditPolicy() {
	}

	/**
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#createSelectionHandles()
	 */
	protected List createSelectionHandles() {
		if (resizeDirections == NONE) {
			// non resizable, so delegate to super implementation
			return super.createSelectionHandles();
		}
		// resizable in at least one direction
		List list = new ArrayList();
		createMoveHandle(list);
		if ((resizeDirections & NORTH) == NORTH) {
			createResizeHandle(list, NORTH);
		}
		if ((resizeDirections & EAST) == EAST) {
			createResizeHandle(list, EAST);
		}
		if ((resizeDirections & SOUTH) == SOUTH) {
			createResizeHandle(list, SOUTH);
		}
		if ((resizeDirections & WEST) == WEST) {
			createResizeHandle(list, WEST);
		}
		//
		createResizeHandle(list, SOUTH_EAST);
		createResizeHandle(list, SOUTH_WEST);
		createResizeHandle(list, NORTH_WEST);
		createResizeHandle(list, NORTH_EAST);
		return list;
	}

	/**
	 * Dispatches erase requests to more specific methods.
	 * 
	 * @see org.eclipse.gef.EditPolicy#eraseSourceFeedback(org.eclipse.gef.Request)
	 */
	public void eraseSourceFeedback(Request request) {
		if (REQ_RESIZE.equals(request.getType())) {
			eraseChangeBoundsFeedback((ChangeBoundsRequest) request);
			removeFeedback(label);
			label = null;
		} else
			super.eraseSourceFeedback(request);
	}

	protected void showChangeBoundsFeedback(ChangeBoundsRequest request) {
		super.showChangeBoundsFeedback(request);
		// /////
		if (REQ_RESIZE.equals(request.getType())) {
			Rectangle rect = getDragSourceFeedbackFigure().getBounds()
					.getCopy();
			Label label = createResizeFeedBack();
			label.setForegroundColor(ColorConstants.TITLETEXT);
			label.setTextAlignment(Label.LEFT);
			String str = String.format("%d x %d", rect.width, rect.height);
			label.setText(str);
			Point md = request.getMoveDelta();
			label.setLocation(md.y != 0 ? rect.getTopRight() : rect
					.getBottomRight());
			label.setSize(FigureUtilities.getTextExtents(str, label.getFont()));
		}

	}

	protected Label createResizeFeedBack() {
		if (label == null) {
			label = new Label();
			addFeedback(label);
		}
		return label;
	}

	/**
	 * Returns the command contribution for the given resize request. By
	 * default, the request is re-dispatched to the host's parent as a
	 * {@link org.eclipse.gef.RequestConstants#REQ_RESIZE_CHILDREN}. The
	 * parent's edit policies determine how to perform the resize based on the
	 * layout manager in use.
	 * 
	 * @param request
	 *            the resize request
	 * @return the command contribution obtained from the parent
	 */
	protected Command getResizeCommand(ChangeBoundsRequest request) {
		// ChangeBoundsRequest req = new
		// ChangeBoundsRequest(REQ_RESIZE_CHILDREN);
		// req.setEditParts(getHost());
		//
		// req.setMoveDelta(request.getMoveDelta());
		// req.setSizeDelta(request.getSizeDelta());
		// req.setLocation(request.getLocation());
		// req.setExtendedData(request.getExtendedData());
		// req.setResizeDirection(request.getResizeDirection());
		ResizeCmpCmd cmd = new ResizeCmpCmd();
		cmd.setHostModel((AuroraComponent) getHost().getModel());
		cmd.setSizeDelt(request.getSizeDelta());
		return cmd;
	}

	protected void showSelection() {
		if (MetaPlugin.isDemonstrate == false)
			addSelectionHandles();
	}
}
