package aurora.ide.meta.gef.editors.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.AuroraPlugin;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.property.MutilInputResourceSelector;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Util;

public class ScreenUIPBrowseButton {

	private List<IPathChangeListener> listeners;

	private String openPath;

	private IProject auroraProject;

	public IProject getAuroraProject() {
		return auroraProject;
	}

	public void setAuroraProject(IProject auroraProject) {
		this.auroraProject = auroraProject;
	}

	private Button button;

	public ScreenUIPBrowseButton(Composite parent, int style) {
		button = new Button(parent, style);
		addSelectionListener(parent);
	}

	public void addListener(IPathChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<IPathChangeListener>();
		}
		listeners.add(listener);
	}

	private void addSelectionListener(final Composite parent) {
		button.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				MutilInputResourceSelector fss = new MutilInputResourceSelector(
						parent.getShell());
				fss.setExtFilter(new String[] { "screen", "uip" });
				IContainer uipFolder = getUIPFolder();
				if(auroraProject == null){
					fss.setInput(uipFolder);
				}else{
					String webHome = ResourceUtil.getWebHome(auroraProject);
					IResource res = ResourcesPlugin.getWorkspace().getRoot()
							.findMember(webHome);
					fss.setInputs(new IContainer[] { (IContainer) res, uipFolder });
				}
				Object obj = fss.getSelection();
				if (obj instanceof IFile) {
					IFile file = (IFile) obj;
					String fileExtension = file.getFileExtension();
					if ("uip".equalsIgnoreCase(fileExtension)) {
						IPath path = file.getFullPath();
						if (uipFolder != null) {
							IContainer web = uipFolder.getParent();
							path = path.makeRelativeTo(web.getFullPath());
						}
						setOpenPath(path.toString());
						return;
					}
					if ("screen".equalsIgnoreCase(fileExtension)) {
						IPath path = file.getFullPath();
						IContainer findWebInf = Util.findWebInf(file);
						if (findWebInf != null) {
							IContainer web = findWebInf.getParent();
							path = path.makeRelativeTo(web.getFullPath());
						}
						setOpenPath(path.toString());
						return;
					}
				}
				setOpenPath(null);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});
	}

	public IContainer getUIPFolder() {
		IFile activeIFile = AuroraPlugin.getActiveIFile();
		IProject proj = activeIFile.getProject();
		AuroraMetaProject mProj = new AuroraMetaProject(proj);
		try {
			return mProj.getScreenFolder();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
		return mProj.getProject();
	}

	public String getOpenPath() {
		return openPath;
	}

	public void setOpenPath(String openPath) {
		this.openPath = openPath;
		if (listeners != null) {
			for (IPathChangeListener l : listeners) {
				l.pathChanged(openPath);
			}
		}
	}

	public void setText(String string) {
		button.setText(string);
	}

	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}

}
