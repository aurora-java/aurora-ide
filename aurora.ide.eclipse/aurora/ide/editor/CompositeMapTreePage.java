package aurora.ide.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;

public class CompositeMapTreePage extends CompositeMapPage {

	private CompositeMap data;

	protected BaseCompositeMapViewer baseCompositeMapPage;

	Composite shell;

	public CompositeMapTreePage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		shell = form.getBody();
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);
		if (data == null) {
			try {
				CompositeLoader loader = AuroraResourceUtil.getCompsiteLoader();
				data = loader.loadByFile(getFile().getAbsolutePath());

			} catch (IOException e) {
				DialogUtil.logErrorException(e);
			} catch (SAXException e) {
				String emptyExcption = "Premature end of file";
				if (e.getMessage() != null && e.getMessage().indexOf(emptyExcption) != -1) {
					data = ScreenUtil.createScreenTopNode();
					((CommentCompositeMap)data).setComment("本文件为空,现在内容为系统自动创建,请修改并保存");
				} else {
//					DialogUtil.showExceptionMessageBox(e);
					return;
				}
			} 
		}
		try {
			createContent(shell);
		} catch (ApplicationException e) {
			DialogUtil.logErrorException(e);
		}
	}

	protected File getFile() {
		IFile ifile = ((IFileEditorInput) getEditor().getEditorInput()).getFile();
		String fileName = AuroraResourceUtil.getIfileLocalPath(ifile);
		return new File(fileName);
	}

	protected void createContent(Composite shell) throws ApplicationException {
		baseCompositeMapPage = new BaseCompositeMapViewer(this, data);
		baseCompositeMapPage.createFormContent(shell);
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			File file = getFile();
//			XMLOutputter.saveToFile(file, data);
			CommentXMLOutputter.saveToFile(file, data);
			
			super.doSave(monitor);
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	public void refresh(boolean dirty) {
		baseCompositeMapPage.refresh(false);
		super.refresh(dirty);
	}

	public CompositeMap getData() {
		return data;
	}

	public TreeViewer getTreeViewer() {
		return baseCompositeMapPage.getTreeViewer();
	}

	public CompositeMap getSelection() {
		return baseCompositeMapPage.getSelection();
	}

	public void setData(CompositeMap content) {
		this.data = content;

	}

	public boolean isFormContendCreated() {
		return baseCompositeMapPage != null;
	}

	@Override
	public void refreshFormContent(CompositeMap data) {
		this.data = data;
		baseCompositeMapPage.refresh(data);
	}
}