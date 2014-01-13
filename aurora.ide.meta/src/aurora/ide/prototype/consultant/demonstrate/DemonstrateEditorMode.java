package aurora.ide.prototype.consultant.demonstrate;

import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.wizard.dialog.DemonstratingDialog;

public class DemonstrateEditorMode extends EditorMode {
	private DemonstratingDialog sysLovDialog;

	public DemonstrateEditorMode(DemonstratingDialog sysLovDialog) {
		this.setSysLovDialog(sysLovDialog);
	}

	public String getMode() {
		return None;
	}

	public boolean isForDisplay() {
		return false;
	}

	public boolean isForCreate() {
		return false;
	}

	public boolean isForUpdate() {
		return false;
	}

	public boolean isForSearch() {
		return false;
	}

	public DemonstratingDialog getDemonstratingDialog() {
		return sysLovDialog;
	}

	public void setSysLovDialog(DemonstratingDialog sysLovDialog) {
		this.sysLovDialog = sysLovDialog;
	}

}
