package aurora.ide.views.bm;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;

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
		event.feedback |= DND.FEEDBACK_SCROLL;
		// event.feedback |= DND.FEEDBACK_EXPAND;
		// event.feedback = DND.FEEDBACK_NONE;
		event.feedback |= DND.FEEDBACK_SELECT;
		// event.feedback = DND.FEEDBACK_INSERT_BEFORE;
		// event.feedback = DND.FEEDBACK_INSERT_AFTER;
		// event.feedback = DND.FEEDBACK_SCROLL;
		// event.feedback = DND.FEEDBACK_EXPAND;
	}

	public void drop(DropTargetEvent event) {
		boolean supportedType = transfer.isSupportedType(event.currentDataType);
		if (supportedType == false) {
			return;
		}

		StyledText st = (StyledText) this.getTextPage().getAdapter(
				StyledText.class);

		int caretOffset = st.getCaretOffset();

		IDocument document = (IDocument) getTextPage().getAdapter(
				IDocument.class);

		try {
			ITypedRegion partition = document.getPartition(caretOffset);
			int length = partition.getLength();
			int offset = partition.getOffset();
			partition.getType();
			String string = document.get(offset, length);
			System.out.println(partition.getType());
			System.out.println(string);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		CreateComponentWizard ccw = new CreateComponentWizard();
		WizardDialog wd = new WizardDialog(new Shell(), ccw);
		wd.open();

		IRewriteTarget target = (IRewriteTarget) this.getTextPage().getAdapter(
				IRewriteTarget.class);
		if (target != null)
			target.beginCompoundChange();

		// Point newSelection= st.getSelection();
		// try {
		// int modelOffset= widgetOffset2ModelOffset(viewer, newSelection.x);
		try {
			document.replace(caretOffset, 0, "lalalall");
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		// } catch (BadLocationException e) {
		// return;
		// }
		st.setSelectionRange(caretOffset, "lalalall".length());

		// event
		System.out.println("drop");
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
