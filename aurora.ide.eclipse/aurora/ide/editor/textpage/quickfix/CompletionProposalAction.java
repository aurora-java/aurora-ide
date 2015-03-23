package aurora.ide.editor.textpage.quickfix;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class CompletionProposalAction implements ICompletionProposal {

	private String fDisplayString;
	private String fReplacementString;
	private int fReplacementOffset;
	private int fReplacementLength;
	private int fCursorPosition;
	private Image fImage;
	private IContextInformation fContextInformation;
	private String fAdditionalProposalInfo;

	//
	private IAction action;
	private boolean ignoreReplace = false;

	public CompletionProposalAction(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition) {
		this(replacementString, replacementOffset, replacementLength,
				cursorPosition, null, null, null, null);
	}

	public CompletionProposalAction(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation,
			String additionalProposalInfo) {
		Assert.isNotNull(replacementString);
		Assert.isTrue(replacementOffset >= 0);
		Assert.isTrue(replacementLength >= 0);
		Assert.isTrue(cursorPosition >= 0);

		fReplacementString = replacementString;
		fReplacementOffset = replacementOffset;
		fReplacementLength = replacementLength;
		fCursorPosition = cursorPosition;
		fImage = image;
		fDisplayString = displayString;
		fContextInformation = contextInformation;
		fAdditionalProposalInfo = additionalProposalInfo;
	}

	public void apply(IDocument document) {
		try {
			if (!ignoreReplace)
				document.replace(fReplacementOffset, fReplacementLength,
						fReplacementString);
			//
			action.run();
		} catch (BadLocationException x) {
			// ignore
		}
	}

	public Point getSelection(IDocument document) {
		return new Point(fReplacementOffset + fCursorPosition, 0);
	}

	public IContextInformation getContextInformation() {
		return fContextInformation;
	}

	public Image getImage() {
		return fImage;
	}

	public String getDisplayString() {
		if (fDisplayString != null)
			return fDisplayString;
		return fReplacementString;
	}

	public String getAdditionalProposalInfo() {
		return fAdditionalProposalInfo;
	}

	public IAction getAction() {
		return action;
	}

	public void setAction(IAction action) {
		this.action = action;
	}

	public boolean isIgnoreReplace() {
		return ignoreReplace;
	}

	public void setIgnoreReplace(boolean ignoreReplace) {
		this.ignoreReplace = ignoreReplace;
	}
}
