package aurora.ide.refactoring.ui.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.view.IPromptsViewer;

public class ShowPromptsViewAction extends Action {

	private TextPage textPage;

	public ShowPromptsViewAction() {
		this.setText("在Prompts Viewer中显示");
		this.setToolTipText("在Prompts Viewer中显示");
		this.setId("aurora.ide.refactoring.ui.action.ShowPromptsViewAction");
	}

	public ShowPromptsViewAction(TextPage textPage) {
		this();
		this.textPage = textPage;
	}

	@Override
	public boolean isEnabled() {
		IFile file = textPage.getFile();
		if (file == null)
			return false;
		return AuroraPlugin.getActivePage().getActiveEditor().isDirty() == false;
	}

	public void run() {
//		AuroraPlugin.getActivePage().findView(viewId)(viewId);
		try {
			IViewPart showView = AuroraPlugin.getActivePage().showView("aurora.ide.views.prompts.view.PromptsView");
			if(showView instanceof IPromptsViewer){
				((IPromptsViewer) showView).linkFile(textPage.getFile());
			}
			//			showView
		} catch (PartInitException e) {
			e.printStackTrace();
		}
 	}

}
