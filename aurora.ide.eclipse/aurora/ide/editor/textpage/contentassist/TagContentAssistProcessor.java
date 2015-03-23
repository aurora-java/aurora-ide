package aurora.ide.editor.textpage.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;
import aurora.ide.helpers.DialogUtil;

public class TagContentAssistProcessor implements IContentAssistProcessor {

	private XMLTagScanner scanner;

	public TagContentAssistProcessor(XMLTagScanner scanner) {
		super();
		this.scanner = scanner;

	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {

		IDocument document = viewer.getDocument();
		IContentAssistStrategy strategy = null;
		try {
			strategy = getAssistStrategy(document, offset);
			if (strategy == null)
				return null;
			return strategy.computeCompletionProposals(viewer, offset);
		} catch (BadLocationException e) {
			DialogUtil.logErrorException(e);
		}
		return null;
	}

	private IContentAssistStrategy getAssistStrategy(IDocument document,
			int documentOffset) throws BadLocationException {
		ITypedRegion region = document.getPartition(documentOffset);
		if (!XMLPartitionScanner.XML_START_TAG.equals(region.getType()))
			return null;
		scanner.setRange(document, region.getOffset(), region.getLength());
		scanner.nextToken();
		scanner.nextToken();
		int tokenEndOffset = scanner.getTokenOffset()
				+ scanner.getTokenLength();
		if (tokenEndOffset >= documentOffset) {
			return new ChildStrategy(scanner);
		}
		return new AttributeStrategy(scanner);
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '<', ' ' };
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}