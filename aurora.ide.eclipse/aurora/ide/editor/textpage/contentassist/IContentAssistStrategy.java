/**
 * 
 */
package aurora.ide.editor.textpage.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author linjinxiao
 * 
 */
public interface IContentAssistStrategy {

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) throws BadLocationException;
}
