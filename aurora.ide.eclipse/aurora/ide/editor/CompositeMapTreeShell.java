package aurora.ide.editor;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DialogUtil;

public class CompositeMapTreeShell implements IViewer {

	protected IViewer mParentViewer;
	BaseCompositeMapViewer baseCompositeMapPage;
	private CompositeMap data;
	private Shell shell;

	public CompositeMapTreeShell(IViewer parent, CompositeMap data) {
		mParentViewer = parent;
		this.data = data;
	}

	public void createFormContent(Shell shell) {

		try {
			this.shell = shell;
			createContent(shell);

		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	protected void createContent(Composite shell) throws ApplicationException {

		baseCompositeMapPage = new BaseCompositeMapViewer(this, data);
		baseCompositeMapPage.createFormContent(shell);
	}

	public void refresh(boolean dirty) {
		String text = shell.getText();
		if (text.indexOf("*") == -1)
			text = "*" + text;
		shell.setText(text);
		if (dirty) {
			mParentViewer.refresh(true);
		}
		baseCompositeMapPage.refresh(false);

	}
}