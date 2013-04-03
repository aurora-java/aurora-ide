package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;

import aurora.ide.meta.gef.editors.figures.TabItemFigure;
import aurora.ide.meta.gef.editors.policies.TabItemChangEditPolicy;
import aurora.plugin.source.gen.screen.model.TabItem;

public class TabItemPart extends ComponentPart {

	@Override
	protected IFigure createFigure() {
		TabItemFigure f = new TabItemFigure();
		TabItem model = getModel();
		f.setModel(model);
		return f;
	}

	public TabItem getModel() {
		return (TabItem) super.getModel();
	}

	public TabItemFigure getFigure() {
		return (TabItemFigure) super.getFigure();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		this.installEditPolicy("TabItemChangEditPolicy", new TabItemChangEditPolicy());
	}

	@Override
	public void performRequest(Request req) {
		super.performRequest(req);
//		this.getViewer().getEditDomain().getCommandStack();
		
		if (REQ_OPEN.equals(req.getType())) {
			Command command = this.getCommand(req);
			if(command !=null&&command.canExecute()){
				CommandStack commandStack = this.getViewer().getEditDomain().getCommandStack();
				commandStack.execute(command);
			}
			
//			TabFolderPart parent = (TabFolderPart) getParent();
//			parent.getModel().disSelectAll();
//			getModel().setCurrent(true);
		}
	}

	@Override
	public int getResizeDirection() {
		return EAST_WEST;
	}
}
