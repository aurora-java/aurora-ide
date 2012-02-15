package aurora.ide.meta.gef.editors.parts;

import java.util.EventObject;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStackListener;

import aurora.ide.meta.gef.editors.figures.ViewDiagramLayout;
import aurora.ide.meta.gef.editors.policies.AutoCreateFormGridEditPolicy;
import aurora.ide.meta.gef.editors.policies.DiagramLayoutEditPolicy;

public class ViewDiagramPart extends ContainerPart {

	CommandStackListener stackListener = new CommandStackListener() {
		public void commandStackChanged(EventObject event) {
			if (!GraphAnimation.captureLayout(getFigure()))
				return;
			while (GraphAnimation.step())
				getFigure().getUpdateManager().performUpdate();
			GraphAnimation.end();
		}
	};

	@Override
	protected IFigure createFigure() {
		Figure figure = new FreeformLayer() {
//			protected boolean isValid() {
//				System.out.println("isValid");
//				return super.isValid();
//				// return false;
//			}
//
//			public void invalidate() {
//				System.out.println("invalidate");
//				
//				super.invalidate();
//			}
//
//			public void revalidate() {
//				
//				System.out.println("revalidate");
//				super.revalidate();
//			}
		};
		ViewDiagramLayout manager = new ViewDiagramLayout(false, this);
		figure.setLayoutManager(manager);
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
		installEditPolicy("Drop BM", new AutoCreateFormGridEditPolicy());

	}

	@Override
	public void showSourceFeedback(Request request) {
		// TODO Auto-generated method stub
		super.showSourceFeedback(request);
		// this.getDiagramPart(null).ge
	}

	@Override
	public void showTargetFeedback(Request request) {
		// TODO Auto-generated method stub
		super.showTargetFeedback(request);
	}

	@Override
	public void activate() {
		super.activate();
		getViewer().getEditDomain().getCommandStack()
				.addCommandStackListener(stackListener);
	}

	@Override
	public void deactivate() {
		getViewer().getEditDomain().getCommandStack()
				.removeCommandStackListener(stackListener);
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

		return super.getTargetEditPart(request);
	}

}
