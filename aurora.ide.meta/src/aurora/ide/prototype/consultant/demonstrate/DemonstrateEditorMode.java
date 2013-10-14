package aurora.ide.prototype.consultant.demonstrate;

import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.wizard.dialog.SysLovDialog;

public class DemonstrateEditorMode extends EditorMode {
	private SysLovDialog sysLovDialog;

	public DemonstrateEditorMode(SysLovDialog sysLovDialog) {
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

	public SysLovDialog getSysLovDialog() {
		return sysLovDialog;
	}

	public void setSysLovDialog(SysLovDialog sysLovDialog) {
		this.sysLovDialog = sysLovDialog;
	}

}
