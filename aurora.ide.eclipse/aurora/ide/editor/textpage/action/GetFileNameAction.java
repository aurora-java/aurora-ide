package aurora.ide.editor.textpage.action;


import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;



public class GetFileNameAction  extends Action  implements IEditorActionDelegate {

	IEditorPart activeEditor;
	public GetFileNameAction() {
		this.setActionDefinitionId("aurora.ide.text.editor.copy.fileName");
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}
	public void run() {
		run(null);
	}

	public void run(IAction action) {
		if (activeEditor == null || !(activeEditor instanceof TextPage)) {
			DialogUtil.showErrorMessageBox("这个类不是" + TextPage.class.getName());
			return;
		}
//		TextPage tp = (TextPage) activeEditor;
		Clipboard cb = new Clipboard(Display.getCurrent());
		IFile ifile = ((IFileEditorInput) activeEditor.getEditorInput()).getFile();
		String textData = "";
		try {
			textData = AuroraResourceUtil.getRegisterPath(ifile);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		if(textData.length()<=0){
			textData = "  ";
		}
//		String textData = tp.getEditorInput().getName();;
		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[]{textData}, new Transfer[]{textTransfer});

	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
