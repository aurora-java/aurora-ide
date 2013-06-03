package aurora.ide.meta.gef.editors.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

import aurora.ide.meta.gef.editors.models.commands.PasteComponentCommand;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.ide.meta.gef.editors.request.PasteComponentRequest;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.GridColumn;

public class PasteComponentEditPolicy extends AbstractEditPolicy {

	@Override
	public Command getCommand(Request request) {
		if (request instanceof PasteComponentRequest)
			return getPasteCommand((PasteComponentRequest) request);
		return null;
	}

	private Command getPasteCommand(PasteComponentRequest request) {
		Container container = this.getContainer();
		if (GridColumn.GRIDCOLUMN.equals(container.getComponentType())) {
			container = container.getParent();
		}
		return new PasteComponentCommand(request, container);
	}

	public Container getContainer() {
		return (Container) this.getHost().getModel();
	}

	protected ViewDiagramPart getDiagramPart(EditPart ep) {
		if (ep instanceof ViewDiagramPart)
			return (ViewDiagramPart) ep;
		return this.getDiagramPart(ep.getParent());
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		if (request instanceof PasteComponentRequest)
			return this.getHost();
		return super.getTargetEditPart(request);
	}

	@Override
	public boolean understandsRequest(Request req) {
		return super.understandsRequest(req);
	}

}
