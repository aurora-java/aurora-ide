package aurora.ide.meta.gef.designer.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.AuroraPlugin;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.ide.meta.gef.editors.property.DialogCellEditor;
import aurora.ide.meta.gef.editors.property.ResourceSelector;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Util;

public class OptionsCellEditor extends DialogCellEditor {
	public static final int BM = 0;
	public static final int CODE = 1;
	private int mode = BM;

	public OptionsCellEditor(Composite parent) {
		super(parent, SWT.NONE);
	}

	public void setEnable(boolean enabled) {
		getButton().setEnabled(enabled);
	}

	public void setSelectionMode(int mode) {
		this.mode = mode;
		getLabel().setForeground(mode == BM ? ColorConstants.black : ColorConstants.gray);
	}

	@Override
	protected Object doGetValue() {
		String str = getLabel().getText();
		return str == null ? "" : str;
	}

	@Override
	protected void doSetFocus() {
	}

	@Override
	protected void doSetValue(Object value) {
		updateLabelText(value);
	}

	private void updateLabelText(Object o) {
		CLabel l = getLabel();
		if (o == null) {
			l.setText("");
			l.setToolTipText(null);
		} else {
			l.setText(o.toString());
			l.setToolTipText(o.toString());
		}
	}

	protected void showDialog() {
		if (mode == CODE) {
			LookupCodeDialog d = new LookupCodeDialog(getLabel().getShell());
			d.setValue(getLabel().getText());
			d.setBlockOnOpen(true);
			if (d.open() == Dialog.OK) {
				updateLabelText(d.getValue());
				fireApplyEditorValue();
			}
			return;
		}
		IFile file = AuroraPlugin.getActiveIFile();
		if (file != null) {
			AuroraMetaProject amp = new AuroraMetaProject(file.getProject());
			IProject proj;
			try {
				proj = amp.getAuroraProject();
				IFolder folder = ResourceUtil.getBMHomeFolder(proj);
				ResourceSelector rs = new ResourceSelector(getLabel()
						.getShell());
				rs.setExtFilter(new String[] { "bm" });
				rs.setInput(folder);
				IResource res = rs.getSelection();
				if (res instanceof IFile) {
					String newValue = Util.toPKG(res.getFullPath()
							.removeFileExtension());
					updateLabelText(newValue);
					fireApplyEditorValue();
				}
			} catch (ResourceNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}
}
