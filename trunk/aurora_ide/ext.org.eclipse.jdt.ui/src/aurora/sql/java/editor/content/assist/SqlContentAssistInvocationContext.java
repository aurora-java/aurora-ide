package aurora.sql.java.editor.content.assist;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;

import patch.org.eclipse.jdt.internal.ui.JavaPlugin;
import patch.org.eclipse.jdt.ui.text.JavaTextTools;
import patch.org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;

public class SqlContentAssistInvocationContext extends
		ContentAssistInvocationContext {

	private String sql;
	private ITypedRegion partition;

	public SqlContentAssistInvocationContext() {
		super();
	}

	public SqlContentAssistInvocationContext(IDocument document, int offset) {
		super(document, offset);
	}

	public SqlContentAssistInvocationContext(ITextViewer viewer, int offset) {
		super(viewer, offset);
	}

	public SqlContentAssistInvocationContext(ITextViewer viewer) {
		super(viewer);
	}

	public String getSql() {
		if (sql == null)
			try {
				IDocument document = this.getDocument();
				ITypedRegion partition = getPartition();
				sql = document.get(partition.getOffset() + 2,
						partition.getLength() - 3);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		return sql;
	}

	public ITypedRegion getPartition() throws BadLocationException {
		if (partition == null) {
			IDocument document = getDocument();
			JavaTextTools textTools = JavaPlugin.getDefault()
					.getJavaTextTools();
			textTools.setupJavaDocumentPartitioner(document);
			partition = document.getPartition(this.getInvocationOffset());

		}
		return partition;
	}

}
