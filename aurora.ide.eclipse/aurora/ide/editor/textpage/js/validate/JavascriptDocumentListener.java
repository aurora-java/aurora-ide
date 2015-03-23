package aurora.ide.editor.textpage.js.validate;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.TextEditor;

import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;

public class JavascriptDocumentListener implements IDocumentListener {

	private static final String SCRIPT = "<script( .*){0,1}>";
	private TextEditor sourceEditor;
	private final static String C_DATA_BEGIN = "<![CDATA[";
	private final static String C_DATA_END = "]]>";
	private AnnotationReporter reporter;

	public TextEditor getSourceEditor() {
		return sourceEditor;
	}

	public AnnotationReporter getReporter() {
		if (reporter == null) {
			reporter = new AnnotationReporter(
					(IAnnotationModel) this.sourceEditor
							.getAdapter(IAnnotationModel.class));
		}
		return reporter;
	}

	public JavascriptDocumentListener(TextEditor sourceEditor) {
		Assert.isNotNull(sourceEditor);
		this.sourceEditor = sourceEditor;

	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		// donothing
	}

	public void documentChanged(DocumentEvent event) {
		int offset = event.getOffset();
		IDocument document = event.getDocument();
		try {
			ITypedRegion partition = document.getPartition(offset);
			ITypedRegion parentRegion = document.getPartition(partition
					.getOffset() - 1);
			String parentNode = document.get(parentRegion.getOffset(),
					parentRegion.getLength());
			if (parentNode == null)
				return;
			String type = partition.getType();
			if (XMLPartitionScanner.XML_CDATA.equals(type)
					&& parentNode.matches(SCRIPT)) {
				validate(document, partition);
			}
		} catch (BadLocationException e) {
			// donothoing;just log it.
			// e.printStackTrace();
		}
	}

	private void validate(IDocument document, ITypedRegion partition)
			throws BadLocationException {
		int beginOffset = partition.getOffset() + C_DATA_BEGIN.length();
		int length = partition.getLength() - C_DATA_BEGIN.length()
				- C_DATA_END.length();
		int beginLine = document.getLineOfOffset(beginOffset);
		String source = document.get(beginOffset, length);
		AnnotationReporter reporter = getReporter();
		reporter.reset(document, beginLine, beginOffset);
		JavascriptValidator validator = new JavascriptValidator(reporter);
		validator.validate(sourceEditor.getPartName(), source);
	}

}
