package aurora.ide.navigator.action;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import uncertain.pkg.PackageManager;
import uncertain.schema.SchemaManager;
import aurora.ide.fake.uncertain.engine.InstanceFactory;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;

public class SxsdPackageValidAction implements IObjectActionDelegate {

	ISelection selection;
	private static final String CONFIG_PATH = "config";

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		if (!(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection structured = (IStructuredSelection) selection;
		IResource type = (IResource) structured.getFirstElement();
		File selectedFile = type.getLocation().toFile();
		PackageManager pkgManager;
		String fileList = "";
		try {
			pkgManager = InstanceFactory.getPackageManager();
			// UncertainEngineUtil.getUncertainEngine(selectedFile.getName()).getPackageManager();
			pkgManager.loadPackgeDirectory(selectedFile.getAbsolutePath());
			File[] files = selectedFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()
						&& PackageManager.isPackageDirectory(file)) {
					File mConfigPathFile = new File(file.getPath(), CONFIG_PATH);
					if (mConfigPathFile == null)
						return;
					String extension = "." + SchemaManager.DEFAULT_EXTENSION;
					File[] sxsdFiles = mConfigPathFile.listFiles();
					if (sxsdFiles == null)
						return;
					for (int k = 0; k < sxsdFiles.length; k++) {
						String projectName = type.getProject().getName();
						String sxsdFile = sxsdFiles[k].getName().toLowerCase();
						String projectBaseName = projectName + "/"
								+ type.getProjectRelativePath().toString()
								+ file.getName() + "/"
								+ mConfigPathFile.getName() + "/"
								+ sxsdFiles[k].getName();
						if (sxsdFile.endsWith(extension)) {
							fileList += projectBaseName + "\r\n";
						}
					}
				}
			}
			if (!fileList.equals("")) {
				fileList = LocaleMessage
						.getString("valid.sxsd.file.window.title")
						+ "\r\n"
						+ fileList;
			} else {
				fileList = LocaleMessage.getString("no.file.is.valid");
			}
			DialogUtil.showMessageBox(SWT.ICON_INFORMATION, "Result", fileList);
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

	}
}
