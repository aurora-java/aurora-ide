package aurora.ide.meta.gef.editors.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.commands.BindFormCommand;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.ide.meta.gef.editors.request.DropBMRequest;

public class BindFormPartEditPolicy extends AbstractEditPolicy {

	@Override
	public Command getCommand(Request request) {
		if (request instanceof DropBMRequest)
			return getBindFormCommand((DropBMRequest) request);
		return super.getCommand(request);
	}

	protected Command getBindFormCommand(DropBMRequest request) {
		if (request.getBm() == null)
			return null;
		BindFormCommand cmd = new BindFormCommand();
		cmd.setBm(request.getBm());
		BOX model = (BOX) this.getHost().getModel();
		if (model.getDataset().getModel() != null) {
			// TODO
			MessageDialog.openConfirm(new Shell(), "Dataset绑定",
					"Dataset已经绑定，是否重置？");
		}
		cmd.setBox((BOX) model);
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
