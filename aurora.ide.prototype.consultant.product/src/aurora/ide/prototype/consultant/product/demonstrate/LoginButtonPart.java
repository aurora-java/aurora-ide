package aurora.ide.prototype.consultant.product.demonstrate;

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.LocationRequest;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.parts.ButtonPart;
import aurora.ide.meta.gef.editors.wizard.dialog.DemonstratingDialog;
import aurora.ide.prototype.consultant.demonstrate.DemonstrateEditorMode;

public class LoginButtonPart extends ButtonPart {
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())
				&& req instanceof LocationRequest) {
			if (MetaPlugin.isDemonstrate) {
				openDemonstrateMainDialog();
				return;
			}
		}
	}

	private void openDemonstrateMainDialog() {
		DemonstratingMainPageDialog dd = new DemonstratingMainPageDialog(this.getViewer().getControl().getShell());
		if (this.getEditorMode() instanceof DemonstrateEditorMode) {
			DemonstratingDialog demonstratingDialog = ((DemonstrateEditorMode) getEditorMode()).getDemonstratingDialog();
			dd.setLoginPage(demonstratingDialog); 
			dd.setProject(demonstratingDialog.getProject());
		}
		dd.open();
	}
}
