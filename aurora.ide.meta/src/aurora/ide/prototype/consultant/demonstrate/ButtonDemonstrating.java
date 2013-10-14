package aurora.ide.prototype.consultant.demonstrate;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.DemonstrateData;

public class ButtonDemonstrating {
	private ComponentPart part;

	public ButtonDemonstrating(ComponentPart part) {
		this.part = part;
	}

	public void demonstrating(Shell shell) {
		DemonstrateData dd = (DemonstrateData) part.getComponent()
				.getPropertyValue(DemonstrateData.DEMONSTRATE_DATA);
		if (dd == null) {
			EditorMode editorMode = part.getEditorMode();
			if (editorMode instanceof DemonstrateEditorMode)
				;
			else
				showMessage("请配置演示数据");
		} else if (DemonstrateData.OPEN_MESSAGE.equals(dd.getOpenType())) {
			this.showMessage(dd.getOpenMessage());
		} else if (DemonstrateData.OPEN_TYPE_UIP.equals(dd.getOpenType())) {
			this.openUIP(dd.getOpenUIPPath());
		}
	}

	private void showMessage(String msg) {
		DialogUtil.showWarningMessageBox(msg);
	}

	private void openUIP(String path) {

		File file = new File(path);
		if (file.exists() == false) {
			showMessage("文件不存在");
			return;
		}
		IEditorInput input = createEditorInput(file);

		String editorId = getEditorId(file);
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			page.openEditor(input, editorId);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private String getEditorId(File file) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(file
				.getName());
		if (descriptor != null)
			return descriptor.getId();
		return "aurora.ide.meta.gef.editors.ConsultantVScreenEditor";
	}

	private IEditorInput createEditorInput(File file) {
		IPath location = new Path(file.getAbsolutePath());
		PathEditorInput input = new PathEditorInput(location);
		return input;
	}
}
