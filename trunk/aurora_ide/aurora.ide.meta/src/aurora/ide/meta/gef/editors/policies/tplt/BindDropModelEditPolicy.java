package aurora.ide.meta.gef.editors.policies.tplt;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.commands.BindDropModelCommand;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.ide.meta.gef.editors.request.DropBMRequest;

public class BindDropModelEditPolicy extends AbstractEditPolicy {

	@Override
	public Command getCommand(Request request) {
		if (request instanceof DropBMRequest)
			return getBindCommand((DropBMRequest) request);
		return super.getCommand(request);
	}

	protected Command getBindCommand(DropBMRequest request) {
		Container container = getContainer();
		Object data = request.getData();
//		String sectionType = container.getSectionType();
//		if(data instanceof List && ((List) data).size()>1){
//			if (!Container.SECTION_TYPE_QUERY.equals(sectionType)
//					&& !Container.SECTION_TYPE_RESULT.equals(sectionType)) {
//				Shell shell = Display.getDefault().getActiveShell();
//				MessageDialog.openInformation(shell, "Info", "无法添加！");
//				return null;
//			}
//		}

		if (!(data instanceof List)) {
			return null;
//			Shell shell = Display.getDefault().getActiveShell();
//			boolean openConfirm = MessageDialog.openConfirm(shell, Messages.BindDropModelEditPolicy_0,
//					Messages.BindDropModelEditPolicy_1);
//			if (openConfirm == false) {
//				return null;
//			}
		}

		BindDropModelCommand cmd = new BindDropModelCommand();
		cmd.setData(data);

		ViewDiagramPart diagramPart = this.getDiagramPart(getHost());
		cmd.setDiagram((ViewDiagram) diagramPart.getComponent());
		
		diagramPart.getEditorMode().isForDisplay();
		
		cmd.setContainer(container);
		return cmd;
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
		if (request instanceof DropBMRequest)
			return this.getHost();
		return super.getTargetEditPart(request);
	}

	@Override
	public boolean understandsRequest(Request req) {
		return super.understandsRequest(req);
	}

}
