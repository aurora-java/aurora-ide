package aurora.ide.editor;

import java.io.File;
import java.net.MalformedURLException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.AuroraProjectNature;
import aurora.ide.editor.outline.BaseOutlinePage;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;

public abstract class BaseCompositeMapEditor extends FormEditor {

	protected CompositeMapPage mainViewerPage;
	private TextPage textPage = new TextPage(this);
	private boolean dirty = false;
	private File file;
	protected int mainViewerIndex;
	protected int textPageIndex;

	private BaseOutlinePage outline = new BaseOutlinePage();;

	public BaseCompositeMapEditor() {
		super();
		this.mainViewerPage = initMainViewerPage();
	}

	public abstract CompositeMapPage initMainViewerPage();

	protected void addPages() {
		try {
			mainViewerIndex = addPage(mainViewerPage);
			textPageIndex = addPage(getTextPage(), getEditorInput());
			setPageText(textPageIndex, TextPage.textPageTitle);
			// setActivePage(textPageIndex);
		} catch (PartInitException e) {
			DialogUtil.logErrorException(e);
		}
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		
		if (!(input instanceof IFileEditorInput)
				//&&(input instanceof FileStoreEditorInput == false)
				)
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput of FileStoreEditorInput");
		setSite(site);
		setInput(input);
		AuroraPlugin.getWorkspace().addResourceChangeListener(
				new InputFileListener(this));
		
		if(input instanceof IFileEditorInput){
			IFile ifile = ((IFileEditorInput) input).getFile();
			file = new File(AuroraResourceUtil.getIfileLocalPath(ifile));
		}
		if(input instanceof FileStoreEditorInput){
			try {
				file = new File(((FileStoreEditorInput) input).getURI().toURL().getFile());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		
		String fileName = file.getName();
		setPartName(fileName);
		// todo delete
		// autoAddAuroraNatue(ifile);

	}

	private void autoAddAuroraNatue(IFile file) {
		if (file.getName().toLowerCase()
				.endsWith("." + AuroraConstant.BMFileExtension)
				|| file.getName().toLowerCase()
						.endsWith("." + AuroraConstant.ScreenFileExtension)) {
			IProject project = file.getProject();
			try {
				if (!AuroraProjectNature.hasAuroraNature(project)) {
					AuroraProjectNature.addAuroraNature(project);
					AuroraProjectNature.autoSetProjectProperty(project);
				}
				
			} catch (CoreException e) {
				DialogUtil.logErrorException(e);
			}
		}
	}

	// protected int getCurrentPage() {
	// int currentPage = super.getCurrentPage();
	// if (currentPage == -1)
	// currentPage = textPageIndex;
	// return currentPage;
	// }

	public void doSave(IProgressMonitor monitor) {
		int currentPage = getCurrentPage();
		if (currentPage == textPageIndex) {
			try {
				// sycMainViewerPageWithTextPage();
				getTextPage().doSave(monitor);
			} catch (Throwable e) {
				DialogUtil.showExceptionMessageBox(e);
				return;
			}
		} else if (currentPage == mainViewerIndex) {
			// ifile.refreshLocal will cause textChanged event,so prevent it;
			getTextPage().setSyc(true);
			mainViewerPage.doSave(monitor);
		}
		setDirty(false);
	}

	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		super.editorDirtyStateChanged();
	}

	public boolean isDirty() {
		return dirty;
	}

	public File getFile() {
		return file;
	}

	public void editorDirtyStateChanged() {
		if (!dirty)
			setDirty(true);
	}

	protected void pageChange(int newPageIndex) {
//		IEditorInput editorInput = this.getEditorInput();
//		if (editorInput instanceof IFileEditorInput) {
//			AuroraBuilder.deleteMarkers(((IFileEditorInput) editorInput)
//					.getFile());
//		}
		super.pageChange(newPageIndex);
		if (newPageIndex == mainViewerIndex) {
			try {
				sycMainViewerPageWithTextPage();
			} catch (Exception e) {
				getTextPage().setIgnorceSycOnce(true);
				setActivePage(textPageIndex);
				String errorMessage = e.getCause() != null ? e.getCause()
						.getMessage() : e.getMessage();
				DialogUtil.showErrorMessageBox(errorMessage);
				return;
			}
		} else if (newPageIndex == textPageIndex) {
			// setActivePage will call pageChage(),we should prevent dead lock.
			if (getTextPage().isIgnorceSycOnce()) {
				getTextPage().setIgnorceSycOnce(false);
				return;
			}
			sycTextPageWithMainViewerPage();
		}
		outline.setActiveEditor(getEditor(newPageIndex));
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IContentOutlinePage.class) {
			return outline;
		}
		return super.getAdapter(adapter);
	}

	private boolean sycMainViewerPageWithTextPage() throws ApplicationException {
		CompositeMap data = getTextPage().toCompoisteMap();
		if (mainViewerPage.getData() == null) {
			mainViewerPage.setData(data);
		} else {
			if (getTextPage().isModify() && mainViewerPage != null)
				mainViewerPage.refreshFormContent(data);
		}
		getTextPage().setModify(false);
		return true;
	}

	private boolean sycTextPageWithMainViewerPage() {
		if (mainViewerPage.isModify()) {
			mainViewerPage.setModify(false);
			getTextPage().refresh(CompositeMapUtil.getFullContent(mainViewerPage
					.getData()));
			return true;
		}
		return true;
	}

	public TextPage getTextPage() {
		return textPage;
	}

}