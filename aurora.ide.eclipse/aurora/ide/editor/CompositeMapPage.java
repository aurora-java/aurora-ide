/**
 * 
 */
package aurora.ide.editor;


import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;


import uncertain.composite.CompositeMap;

/**
 * @author linjinxiao
 * 
 */
public abstract class CompositeMapPage extends FormPage implements IViewer {
	private boolean modify = false;
	public CompositeMapPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	public abstract void setData(CompositeMap data);
	public abstract CompositeMap getData();
	public abstract void refreshFormContent(CompositeMap data);
	protected File getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput()).getFile();
		String fileName = AuroraResourceUtil.getIfileLocalPath(ifile);
		return new File(fileName);
	}
	public void doSave(IProgressMonitor monitor) {
		IFile ifile = ((IFileEditorInput) getEditorInput()).getFile();
		try {
			ifile.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
	}
	public boolean isFormContendCreated(){
		return true;
	}
	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}
	public void refresh(boolean dirty) {
		if(dirty){
			modify = true;
			getEditor().editorDirtyStateChanged();
		}
	}
}
