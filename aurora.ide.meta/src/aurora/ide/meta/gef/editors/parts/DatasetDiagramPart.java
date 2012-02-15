package aurora.ide.meta.gef.editors.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import aurora.ide.meta.gef.editors.layout.DatasetLayout;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;

public class DatasetDiagramPart extends ContainerPart {

	// CommandStackListener stackListener = new CommandStackListener() {
	// public void commandStackChanged(EventObject event) {
	// if (!GraphAnimation.captureLayout(getFigure()))
	// return;
	// while (GraphAnimation.step())
	// getFigure().getUpdateManager().performUpdate();
	// GraphAnimation.end();
	// }
	// };

	@Override
	protected IFigure createFigure() {
//		Figure f = new Figure() {
//			public void setBounds(Rectangle rect) {
//				int x = bounds.x, y = bounds.y;
//
//				boolean resize = (rect.width != bounds.width)
//						|| (rect.height != bounds.height), translate = (rect.x != x)
//						|| (rect.y != y);
//
//				if (isVisible() && (resize || translate))
//					erase();
//				if (translate) {
//					int dx = rect.x - x;
//					int dy = rect.y - y;
//					primTranslate(dx, dy);
//				}
//				bounds.width = rect.width;
//				bounds.height = rect.height;
//				if (resize || translate) {
//					fireFigureMoved();
//					repaint();
//				}
//			}
//		};
		Figure f = new FreeformLayer();
		f.setLayoutManager(new DatasetLayout(this));
//		f.setSize(600, 80);
		return f;
	}

	protected List<?> getModelChildren() {
		List<AuroraComponent> children = ((Container) this.getComponent())
				.getChildren();
		List<AuroraComponent> result = new ArrayList<AuroraComponent>();
		for (AuroraComponent a : children) {
			if ((a instanceof Dataset)) {
				result.add(a);
			}
		}
		return result;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FlowLayoutEditPolicy() {

			@Override
			protected Command createAddCommand(EditPart child, EditPart after) {
				return null;
			}

			@Override
			protected Command createMoveChildCommand(EditPart child,
					EditPart after) {
				return null;
			}

			@Override
			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}

			@Override
			protected boolean isLayoutHorizontal() {
				return true;
			}
			

		});
		// installEditPolicy("Drop BM", new AutoCreateFormGridEditPolicy());

	}

	@Override
	public void activate() {
		super.activate();
		// getViewer().getEditDomain().getCommandStack()
		// .addCommandStackListener(stackListener);
	}

	@Override
	public void deactivate() {
		// getViewer().getEditDomain().getCommandStack()
		// .removeCommandStackListener(stackListener);
		super.deactivate();
	}

	@Override
	protected void addChild(EditPart child, int index) {
		if (child == null)
			return;
		super.addChild(child, index);
	}

	@Override
	public Command getCommand(Request request) {
		// TODO Auto-generated method stub
		return super.getCommand(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		// TODO Auto-generated method stub
		return super.getTargetEditPart(request);
	}

}
