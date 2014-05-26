package aurora.sql.java.editor.content.assist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;

import aurora.sql.java.editor.configration.SqlJSourceScanner;

public class SQLJContentAssistProvider {

	public static final int KEY_WORD = 2;

	public static final int COMUMN = 2 * 2;
	public static final int TABLE = 2 * 4;

	private SqlContentAssistInvocationContext context;

	public SQLJContentAssistProvider(SqlContentAssistInvocationContext context) {
		this.context = context;
	}

	public List<CompletionProposal> getCompletionProposal() {
		// getSqlPositionType();
		List<CompletionProposal> result = new ArrayList<CompletionProposal>();
		try {
			int pos = context.getInvocationOffset()
					- context.getPartition().getOffset() - 2;
			int sqlPositionType = getSqlPositionType(context.getSql(), pos);
			if ((sqlPositionType & KEY_WORD) != 0) {
				result.addAll(new SQLKeyWordProposal(context)
						.getCompletionProposal());
			}
			if ((sqlPositionType & COMUMN) != 0) {
				result.addAll(new SQLColumnProposal(context)
						.getCompletionProposal());
			}
			if ((sqlPositionType & TABLE) != 0) {
				result.addAll(new SQLTableProposal(context)
						.getCompletionProposal());
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return result;
	}

	public int getSqlPositionType(String sql, int position) {
		int type = COMUMN;

		StringTokenizer strToken = new StringTokenizer(sql);

		String lastStr = "";
		int lastPosition = 0;
		String str = "";
		while (strToken.hasMoreElements()) {
			String nextToken = strToken.nextToken();
			int currentPosition = strToken.getCurrentPosition();

			if (currentPosition >= position) {

				if (position + nextToken.length() >= currentPosition) {
					str = nextToken;
				} else {
					str = "";
				}
				break;
			}

			if (isKeyWord(nextToken)) {
				if ("from".equalsIgnoreCase(nextToken)) {
					type = TABLE;
				} else {
					type = COMUMN;
				}
			}

			lastPosition = currentPosition;
			lastStr = nextToken;

		}

		return KEY_WORD | type;
	}

	private boolean isKeyWord(String nextToken) {
		String[] sqlKeywords = SqlJSourceScanner.getSQLKeywords();
		return Arrays.binarySearch(sqlKeywords, nextToken.toUpperCase()) >= 0;
	}

	static public String computeIdentifierPrefix(IDocument document, int end) {

		String fPrefix = "";
		try {
			if (document == null)
				return null;
			int start = end;
			while (--start >= 0) {
				if (!Character.isJavaIdentifierPart(document.getChar(start)))
					break;
			}
			start++;
			fPrefix = document.get(start, end - start);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return fPrefix;
	}

}
