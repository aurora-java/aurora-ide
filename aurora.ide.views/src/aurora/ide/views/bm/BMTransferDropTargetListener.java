package aurora.ide.views.bm;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

import uncertain.composite.CompositeMap;
import aurora.ide.create.component.wizard.CreateComponentWizard;
import aurora.ide.editor.textpage.TextPage;

public class BMTransferDropTargetListener extends DropTargetAdapter {
	private Transfer transfer = BMTransfer.getInstance();
	private TextPage textPage;

	public BMTransferDropTargetListener(TextPage textPage) {
		this.textPage = textPage;
	}

	public Transfer getTransfer() {
		return transfer;
	}

	public void setTransfer(Transfer transfer) {
		this.transfer = transfer;
	}

	public Transfer[] getTransfers() {
		return new Transfer[] { transfer };
	}

	public void dragOver(DropTargetEvent event) {
		boolean supportedType = transfer.isSupportedType(event.currentDataType);
		if (supportedType == true) {
			event.feedback |= DND.FEEDBACK_SCROLL;
			event.feedback |= DND.FEEDBACK_SELECT;
			return;
		}
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
//		boolean supportedType = transfer.isSupportedType(event.currentDataType);
//		if (supportedType == true) {
//			return;
//		}
		super.dragEnter(event);
	}

	public void drop(DropTargetEvent event) {
		boolean supportedType = transfer.isSupportedType(event.currentDataType);
		if (supportedType == false) {
			return;
		}
		IProject project = getTextPage().getFile().getProject();
		Object a = event.data;
		if (a instanceof List<?>) {
			@SuppressWarnings("unchecked")
			CreateComponentWizard ccw = new CreateComponentWizard(
					(List<CompositeMap>) event.data, project, getTextPage());
			WizardDialog wd = new WizardDialog(textPage.getSite().getShell(), ccw);
			wd.addPageChangedListener(ccw);
			wd.setHelpAvailable(false);
			wd.setMinimumPageSize(800,400);
			int open = wd.open();
		}
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		super.dropAccept(event);
	}

	public TextPage getTextPage() {
		return textPage;
	}

	public void setTextPage(TextPage textPage) {
		this.textPage = textPage;
	}

}
