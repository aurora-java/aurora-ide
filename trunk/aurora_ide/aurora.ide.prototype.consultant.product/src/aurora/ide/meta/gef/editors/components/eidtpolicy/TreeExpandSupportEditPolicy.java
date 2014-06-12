package aurora.ide.meta.gef.editors.components.eidtpolicy;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;

import aurora.ide.meta.gef.editors.components.figure.TreeLayoutManager;
import aurora.ide.meta.gef.editors.components.part.CustomTreeContainerPart;
import aurora.ide.meta.gef.editors.components.part.CustomTreePart;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;

public class TreeExpandSupportEditPolicy extends GraphicalEditPolicy {
	public EditPart getTargetEditPart(Request request) {
		// System.out.println("request tpye : " + request.getType());
		return super.getTargetEditPart(request);
	}

	private boolean expanded = true;
	private Listener l = new Listener();
	public static final String EXPAND_SUPPORT = "EXPAND_SUPPORT";
	public static final String SIZE_CHANGED = "SIZE_CHANGED";

	private class Listener extends MouseListener.Stub {
		public void mousePressed(MouseEvent me) {
			Point location = me.getLocation();
			CustomTreeContainerPart host = (CustomTreeContainerPart) getHost();
			Rectangle bounds = host.getFigure().getBounds();
			if (location.x - bounds.x < 16 && location.y - bounds.y < 16)
				expand();
		}

		public void expand() {
			final CustomTreeContainerPart host = (CustomTreeContainerPart) getHost();
			host.getViewer().getEditDomain().getCommandStack().execute(new Command("expand") {
				public void execute() {
					host.getComponent().setPropertyValue(
							CustomTreeContainerNode.CONTAINER_EXPAND, expanded = !expanded);
				}
			});
//			host.getComponent().setPropertyValue(
//					CustomTreeContainerNode.CONTAINER_EXPAND, expanded = !expanded);
//			host.expand(expanded = !expanded);
			getTreeRoot(host).layout();
			
			

//			return new Command("expand") {
//				public void execute() {
//					CustomTreeContainerPart host = (CustomTreeContainerPart) getHost();
//					host.getComponent().setPropertyValue(
//							CustomTreeContainerNode.CONTAINER_EXPAND, true);
//				}
//			};
//		
			
			
			// AbstractGraphicalEditPart parent = (AbstractGraphicalEditPart)
			// host
			// .getParent();
			// unused
			// notifyParent();
			// parent.getParent();
			// EditPolicy editPolicy = parent.getEditPolicy(LAYOUT_ROLE);
			// if (editPolicy != null) {
			// Request request = new Request(SIZE_CHANGED);
			// Map hashMap = new HashMap();
			// hashMap.put(SIZE_CHANGED, host);
			// request.setExtendedData(hashMap);
			// editPolicy.showSourceFeedback(request);
			// }
			// LayoutManager layoutManager =
			// parent.getFigure().getLayoutManager();
			// layoutManager.setConstraint(host.getFigure(), SIZE_CHANGED);

		}

		private CustomTreePart getTreeRoot(EditPart editPart) {
			EditPart parent = editPart.getParent();

			if (parent instanceof CustomTreePart)
				return (CustomTreePart) parent;
			return getTreeRoot(parent);
		}
	}

	// unused
	private void notifyParent() {
		TreeExpandSupportEditPolicy treeExpandSupportEditPolicy = ((TreeExpandSupportEditPolicy) getHost()
				.getParent().getEditPolicy("Expand support"));
		if (treeExpandSupportEditPolicy != null)
			treeExpandSupportEditPolicy.sizeChanged();
	}

	@Override
	public void activate() {
		super.activate();
		CustomTreeContainerPart host = (CustomTreeContainerPart) getHost();
		host.getFigure().addMouseListener(l);

	}

	// unused
	public void sizeChanged() {
		AbstractGraphicalEditPart host = (AbstractGraphicalEditPart) getHost();
		IFigure figure = host.getFigure();
		Dimension newSize = calculateNewSize(figure);
		figure.setSize(newSize);
		notifyParent();
	}

	// unused
	private Dimension calculateNewSize(IFigure figure) {
		Dimension newSize = new Dimension();
		List children = figure.getChildren();
		for (int i = 0; i < children.size(); i++) {
			IFigure node = (IFigure) children.get(i);
			Dimension nodeSize = node.getSize();
			newSize.width = newSize.width >= nodeSize.width
					+ TreeLayoutManager.X_STEP ? newSize.width : nodeSize.width
					+ TreeLayoutManager.X_STEP;
			newSize.height += nodeSize.height;
		}
		newSize.height += TreeLayoutManager.NODE_DEFUAULT_HIGHT;
		return newSize;
	}

	@Override
	public void deactivate() {
		CustomTreeContainerPart host = (CustomTreeContainerPart) getHost();
		host.getFigure().removeMouseListener(l);
		super.deactivate();
	}

	public Command getCommand(Request request) {

		if (REQ_CREATE.equals(request.getType())) {
			return new Command("expand") {
				public void execute() {
					CustomTreeContainerPart host = (CustomTreeContainerPart) getHost();
					host.getComponent().setPropertyValue(
							CustomTreeContainerNode.CONTAINER_EXPAND, true);
				}
			};
		}

		return null;
	}
}
