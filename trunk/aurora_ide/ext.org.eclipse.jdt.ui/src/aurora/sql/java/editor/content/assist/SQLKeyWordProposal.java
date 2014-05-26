package aurora.sql.java.editor.content.assist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.CompletionProposal;

import aurora.sql.java.editor.configration.SqlJSourceScanner;

public class SQLKeyWordProposal extends AbctractSQLProposal {
	private SqlContentAssistInvocationContext context;

	public SQLKeyWordProposal(SqlContentAssistInvocationContext context) {
		super(context);
		this.context = context;
	}

	@Override
	public List<CompletionProposal> getCompletionProposal()
			throws BadLocationException {

		List<CompletionProposal> cps = new ArrayList<CompletionProposal>();

		List<String> sqlAllWords = SqlJSourceScanner.getSQLAllWords();

		CharSequence computeIdentifierPrefix = context
				.computeIdentifierPrefix();
		for (String string : sqlAllWords) {
			if (checkStartWith(computeIdentifierPrefix, string)) {
				continue;
			}
			CompletionProposal cp = createCompletionProposal(
					computeIdentifierPrefix, string);
			cps.add(cp);
		}

		return cps;

	}
}
