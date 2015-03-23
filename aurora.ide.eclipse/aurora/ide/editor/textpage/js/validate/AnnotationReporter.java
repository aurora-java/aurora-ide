package aurora.ide.editor.textpage.js.validate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class AnnotationReporter implements ErrorReporter {
	public int character;

	public String evidence;

	public int line;

	public String reason;

	private List annotationList = new LinkedList();

	private IAnnotationModel annotationModel;

	private int beginLine;

	private int beginOffset;

	private IDocument document;

	public AnnotationReporter(IAnnotationModel annotationModel) {
		Assert.isNotNull(annotationModel);
		this.annotationModel = annotationModel;
	}

	public void warning(String message, String sourceName, int line,
			String lineSource, int lineOffset) {
		addAnnotation("org.eclipse.ui.workbench.texteditor.warning", message,
				sourceName, line, lineSource, lineOffset);
	}

	private void addAnnotation(String type, String message, String sourceName,
			int line, String lineSource, int lineOffset) {
		try {
			int i = this.beginLine + line;
			int offset = line == 0 ? beginOffset : document.getLineOffset(i);
			int length = lineOffset;
			Position pos = new Position(offset, length);
			Annotation annotation = new Annotation(type, false, message);
			annotationModel.addAnnotation(annotation, pos);
			annotationList.add(annotation);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	public void error(String message, String sourceName, int line,
			String lineSource, int lineOffset) {
		addAnnotation("org.eclipse.ui.workbench.texteditor.error", message,
				sourceName, line, lineSource, lineOffset);
	}

	public EvaluatorException runtimeError(String message, String sourceName,
			int line, String lineSource, int lineOffset) {
		return new EvaluatorException(message, sourceName, line);
	}

	public void clear() {
		for (Iterator it = annotationList.iterator(); it.hasNext();) {
			annotationModel.removeAnnotation((Annotation) it.next());
		}
		annotationList.clear();
	}

	public void reset(IDocument document, int beginLine, int beginOffset) {
		this.document = document;
		this.beginLine = beginLine;
		this.beginOffset = beginOffset;
		clear();
	}
}
