package aurora.ide.meta.gef.designer.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.AuroraPlugin;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.property.DialogCellEditor;
import aurora.ide.meta.gef.editors.property.ResourceSelector;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Util;

public class OptionsCellEditor extends DialogCellEditor {

	public OptionsCellEditor(Composite parent) {
		super(parent, SWT.NONE);
	}

	public void setEnable(boolean enabled) {
		getButton().setEnabled(enabled);
		if (enabled)
			getButton().forceFocus();
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
		if (value != null)
			getLabel().setText(value.toString());
	}

	protected void showDialog() {
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
					getLabel().setText(newValue);
					fireApplyEditorValue();
				}
			} catch (ResourceNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}
}
