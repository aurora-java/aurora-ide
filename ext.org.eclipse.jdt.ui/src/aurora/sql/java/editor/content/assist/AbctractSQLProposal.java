package aurora.sql.java.editor.content.assist;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.CompletionProposal;

public abstract class AbctractSQLProposal {
	private SqlContentAssistInvocationContext context;

	public AbctractSQLProposal(SqlContentAssistInvocationContext context) {
		this.context = context;
	}

	abstract public List<CompletionProposal> getCompletionProposal()
			throws BadLocationException;

	protected boolean checkStartWith(CharSequence computeIdentifierPrefix,
			String string) {
		return string.toLowerCase().startsWith(
				computeIdentifierPrefix.toString().toLowerCase()) == false;
	}

	protected CompletionProposal createCompletionProposal(
			CharSequence computeIdentifierPrefix, String string) {
		CompletionProposal cp = new CompletionProposal(string,
				context.getInvocationOffset()
						- computeIdentifierPrefix.length(),
				computeIdentifierPrefix.length(), context.getInvocationOffset());
		return cp;
	}

}
