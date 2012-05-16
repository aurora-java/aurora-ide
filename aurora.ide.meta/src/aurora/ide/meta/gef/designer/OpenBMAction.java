package aurora.ide.meta.gef.designer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.model.ModelMerger;
import aurora.ide.meta.gef.editors.ImagesUtils;

public class OpenBMAction extends Action {

	private IFile bmqFile;
	private IWorkbenchPart part;
	private IFile baseBMFile;

	public OpenBMAction(IFile bmqFile, IWorkbenchPart part) {
		super("Open BM", Action.AS_DROP_DOWN_MENU);
		setImageDescriptor(ImagesUtils.getImageDescriptor("folder.png"));
		this.bmqFile = bmqFile;
		this.part = part;

	}

	@Override
	public IMenuCreator getMenuCreator() {
		return new OpenBmMenuCreator();
	}

	@Override
	public void run() {
		try {
			openFile(getFile(""));
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void openFile(IFile bmFile) {
		if (bmFile == null || !bmFile.exists()) {
			DialogUtil.showWarningMessageBox("Related file does not exists.");
			return;
		}
		try {
			IWorkbenchPage page = part.getSite().getPage();
			IDE.openEditor(page, bmFile, "aurora.ide.BusinessModelEditor", true);
		} catch (PartInitException e) {
			// error will show in another way.
		}
	}

	private IFile getFile(String sufix) throws ResourceNotFoundException {
		ModelMerger merger = new ModelMerger(bmqFile);
		baseBMFile = merger.getBMFile();
		IPath fullpath = baseBMFile.getFullPath();
		IPath newPath = new Path(fullpath.removeFileExtension().toString()
				+ sufix + ".bm");
		IFile f = baseBMFile.getProject().getParent().getFile(newPath);
		return f;
	}

	class OpenBmMenuCreator implements IMenuCreator, SelectionListener {

		public void dispose() {

		}

		public Menu getMenu(Control parent) {
			Menu menu = new Menu(parent);
			for (String s : IDesignerConst.AE_TYPES) {
				MenuItem mi = new MenuItem(menu, SWT.NONE);
				mi.setText("Open for " + s + " BM");
				mi.setData(s);
				mi.addSelectionListener(this);
			}
			return menu;
		}

		public Menu getMenu(Menu parent) {
			return null;
		}

		public void widgetSelected(SelectionEvent e) {
			MenuItem mi = (MenuItem) e.getSource();
			String s = (String) mi.getData();
			try {
				openFile(getFile("_for_" + s));
			} catch (ResourceNotFoundException e1) {
				e1.printStackTrace();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {

		}
	}

}
