package aurora.ide.editor.textpage;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;

import aurora.ide.helpers.DialogUtil;

public class XMLDoubleClickStrategy implements ITextDoubleClickStrategy {
	protected ITextViewer textView;
	private ISourceViewer sourceViewer;
	public static final String AnnotationType ="aurora.ide.word.highlight";
	private List annotatioList = new LinkedList();
	private List offsetList = new LinkedList();
	int offset;
	private String keyWord;
	private IAnnotationModel  annotationModel;
	private BracesMatch braceMatch;
	public XMLDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		this.sourceViewer = sourceViewer;
		braceMatch = new BracesMatch(sourceViewer);
		addListner(sourceViewer);
	}
	private void addListner(ISourceViewer sourceViewer){
		sourceViewer.getTextWidget().addCaretListener(new CaretListener() {
			public void caretMoved(CaretEvent event) {
				try {
					braceMatch.compute(event.caretOffset);
				} catch (BadLocationException e) {
					DialogUtil.logErrorException(e);
				}
				if(keyWord == null||"".equals(keyWord))
					return;
				int caretOffset = event.caretOffset-keyWord.length();
				if(offsetList.contains(new Integer(caretOffset)))
					return;
				clearHistory();
			}
		});
	}
	public void doubleClicked(final ITextViewer part) {
		annotationModel = getAnnotationModel();
		clearHistory();
		offset = part.getSelectedRange().x;
		if (offset < 0)
			return;
		textView = part;
//		if (!selectComment(pos)) {
			selectWord(offset);
//		}
	}
	private IAnnotationModel getAnnotationModel() {
		if(annotationModel != null)
			return annotationModel;
		annotationModel= sourceViewer.getAnnotationModel();
		if(annotationModel == null){
			annotationModel = new AnnotationModel();
			annotationModel.connect(sourceViewer.getDocument());
		}
		return annotationModel;
	}
	protected boolean selectComment(int caretPos) {
		IDocument doc = textView.getDocument();
		int startPos, endPos;
		try {
			int pos = caretPos;
			char c = ' ';
			while (pos >= 0) {
				c = doc.getChar(pos);
				if (c == '\\') {
					pos -= 2;
					continue;
				}
				if (c == Character.LINE_SEPARATOR || c == '\"')
					break;
				--pos;
			}
			if (c != '\"')
				return false;
			startPos = pos;
			pos = caretPos;
			int length = doc.getLength();
			c = ' ';
			while (pos < length) {
				c = doc.getChar(pos);
				if (c == Character.LINE_SEPARATOR || c == '\"')
					break;
				++pos;
			}
			if (c != '\"')
				return false;
			endPos = pos;
			int offset = startPos + 1;
			int len = endPos - offset;
			textView.setSelectedRange(offset, len);
			return true;
		} catch (BadLocationException x) {
		}

		return false;
	}
	protected boolean selectWord(int caretPos) {
		IDocument doc = textView.getDocument();
		int startPos, endPos;
		try {
			int pos = caretPos;
			char c;
			while (pos >= 0) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				--pos;
			}
			startPos = pos;
			pos = caretPos;
			int length = doc.getLength();
			while (pos < length) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				++pos;
			}
			endPos = pos;
			selectRange(startPos, endPos);
			int offset = startPos + 1;
			int wordLength = endPos - offset;
			keyWord = textView.getDocument().get(offset, wordLength);
			setHighLight(keyWord);
			return true;
		} catch (BadLocationException x) {
		}

		return false;
	}
	private void setHighLight(String keyword){
		String line = textView.getTextWidget().getText();
		int cursor = -1;
		while ((cursor = line.indexOf(keyword, cursor + 1)) >= 0) {
			createAnnotation(cursor);
		}
	}
	protected void selectRange(int startPos, int stopPos) {
		int offset = startPos + 1;
		int length = stopPos - offset;
		textView.setSelectedRange(offset, length);
	}
	private void createAnnotation(int offset){
		Position   pos   =   new   Position(offset,keyWord.length());
		Annotation annotation = new Annotation(AnnotationType,false,keyWord);
		annotationModel.addAnnotation(annotation,pos);
		annotatioList.add(annotation);
		offsetList.add(new Integer(offset));
	}
	private void clearHistory(){
		if(offsetList.size() == 0)
			return;
		for(Iterator it = annotatioList.iterator();it.hasNext();){
			annotationModel.removeAnnotation((Annotation)it.next());
		}
		annotatioList.clear();
		offsetList.clear();
	}
}