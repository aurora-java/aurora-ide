package aurora.ide.editor.textpage.js.validate;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import aurora.ide.editor.textpage.IReconcileListener;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.helpers.DialogUtil;

public class JSValidator implements IReconcileListener {

	private static final String SCRIPT = "<script( .*){0,1}>";

	private final static String C_DATA_BEGIN = "<![CDATA[";
	private final static String C_DATA_END = "]]>";
	private AnnotationReporter reporter;
	private IDocument document;
	private ISourceViewer mSourceViewer;

	public JSValidator(ISourceViewer sourceViewer) {
		this.mSourceViewer = sourceViewer;
		document = sourceViewer.getDocument();
	}

	public AnnotationReporter getReporter() {
		if (reporter == null) {
			reporter = new AnnotationReporter(
					mSourceViewer.getAnnotationModel());
		}
		return reporter;
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
		validator.validate("javascript", source);
	}

	public void reconcile() {
		document = mSourceViewer.getDocument();
		if (document == null)
			return;
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				StyledText textWidget = mSourceViewer.getTextWidget();
				if(textWidget==null)
					return;
				int offset = textWidget.getCaretOffset();
				if (offset <= 0)
					return;
				try {
					ITypedRegion partition = document.getPartition(offset);
					int offset2 = partition.getOffset() - 1;
					if (offset2 <= 0)
						return;
					ITypedRegion parentRegion = document.getPartition(offset2);
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
					DialogUtil.logErrorException(e);
				}
			}
		});
	}

}
