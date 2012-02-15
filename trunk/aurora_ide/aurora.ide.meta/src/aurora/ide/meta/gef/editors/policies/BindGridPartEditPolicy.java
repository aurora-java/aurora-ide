package aurora.ide.meta.gef.editors.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.commands.BindGridCommand;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.ide.meta.gef.editors.request.DropBMRequest;

public class BindGridPartEditPolicy extends AbstractEditPolicy {

	@Override
	public Command getCommand(Request request) {
		if (request instanceof DropBMRequest)
			return getBindCommand((DropBMRequest) request);
		return super.getCommand(request);
	}

	protected Command getBindCommand(DropBMRequest request) {
		if (request.getBm() == null)
			return null;
		BindGridCommand cmd = new BindGridCommand();
		// cmd.setDiagram(parentModel);
		// cmd.setChild(new Input());
		cmd.setBm(request.getBm());
		Grid model = (Grid) this.getHost().getModel();
		if (model.getDataset().getModel() != null) {
			// TODO
			MessageDialog.openConfirm(new Shell(), "Dataset绑定",
					"Dataset已经绑定，是否重置？");
		}
		cmd.setGrid((Grid) model);
		ViewDiagramPart diagramPart = this.getDiagramPart(getHost());
		cmd.setDiagram((ViewDiagram) diagramPart.getComponent());
		return cmd;
	}

	protected ViewDiagramPart getDiagramPart(EditPart ep) {
		if (ep instanceof ViewDiagramPart)
			return (ViewDiagramPart) ep;
		return this.getDiagramPart(ep.getParent());
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		if (request instanceof DropBMRequest)
			return this.getHost();
		return super.getTargetEditPart(request);
	}

	@Override
	public boolean understandsRequest(Request req) {
		return super.understandsRequest(req);
	}

}
