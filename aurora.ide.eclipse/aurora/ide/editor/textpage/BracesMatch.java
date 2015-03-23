package aurora.ide.editor.textpage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;


public class BracesMatch {
	public static final String AnnotationType ="aurora.ide.braces.match";
	private ISourceViewer sourceViewer;
	private IAnnotationModel  annotationModel;
	public static Map braceList = new HashMap();
	private Annotation lastAnnotation ;
	static {
		braceList.put("{", new Brace("{","}",true));
		braceList.put("}", new Brace("}","{",false));
		braceList.put("(", new Brace("(",")",true));
		braceList.put(")", new Brace(")","(",false));
	}
	public BracesMatch(ISourceViewer sourceViewer) {
		this.sourceViewer = sourceViewer;
	}
	public static void addBrace(Brace brace){
		braceList.put(brace.getBrace(), brace);
	}
	public void compute(int cursorOffset) throws BadLocationException{
		annotationModel = getAnnotationModel();
		if(lastAnnotation != null){
			annotationModel.removeAnnotation(lastAnnotation);
		}
		IDocument document = sourceViewer.getDocument();
		ITypedRegion region = document.getPartition(cursorOffset);
		if (!XMLPartitionScanner.XML_CDATA.equals(region.getType()))
			return;
		String str = document.get(cursorOffset-1, 1);
		Object braceObject = braceList.get(str);
		if(braceObject == null)
			return ;
		Brace brace = (Brace)braceObject;
		String searchContent = null;
		lastAnnotation = new Annotation(AnnotationType,false,brace.getMatchBrace());
		if(brace.isForward()){
			searchContent = document.get(cursorOffset, region.getLength()+(region.getOffset()-cursorOffset));
			if(searchContent == null ||"".equals(searchContent))
				return;
			int fromIndex = 0;
			int braceCount = 1;
			while(fromIndex<searchContent.length()){
				if(brace.getMatchBrace().equals(searchContent.substring(fromIndex, fromIndex+1))){
					if(braceCount == 1){
						Position   pos   =   new   Position(cursorOffset+fromIndex,1);
						annotationModel.addAnnotation(lastAnnotation, pos);
						return;
					}else{
						braceCount--;
					}
				}else if(brace.getBrace().equals(searchContent.substring(fromIndex, fromIndex+1))){
					braceCount++;
				}
				fromIndex++;
			}
		}else{
			searchContent = document.get(region.getOffset(), cursorOffset-region.getOffset()-1);
			if(searchContent == null ||"".equals(searchContent))
				return;
			int index = searchContent.length();
			int braceCount = 1;
			while(index>0){
				String sunStr = searchContent.substring(index-1, index);
				if(brace.getMatchBrace().equals(sunStr)){
					if(braceCount == 1){
						Position   pos   =   new   Position(region.getOffset()+index-1,1);
						annotationModel.addAnnotation(lastAnnotation, pos);
						return;
					}else{
						braceCount--;
					}
				}else if(brace.getBrace().equals(sunStr)){
					braceCount++;
				}
				index--;
			}
		}
		
	}
	private IAnnotationModel getAnnotationModel() {
		if(annotationModel != null)
			return annotationModel;
		IAnnotationModel annotationModel= sourceViewer.getAnnotationModel();
		if(annotationModel == null){
			annotationModel = new AnnotationModel();
			annotationModel.connect(sourceViewer.getDocument());
		}
		return annotationModel;
	}
}


class Brace {
	private String brace;
	private String matchBrace;
	private boolean forward = true;
	/**
	 * @param brace
	 * @param matchBrace
	 * @param forward
	 */
	public Brace(String brace, String matchBrace, boolean forward) {
		super();
		this.brace = brace;
		this.matchBrace = matchBrace;
		this.forward = forward;
	}
	public String getBrace() {
		return brace;
	}
	public void setBrace(String brace) {
		this.brace = brace;
	}
	public String getMatchBrace() {
		return matchBrace;
	}
	public void setMatchBrace(String matchBrace) {
		this.matchBrace = matchBrace;
	}
	public boolean isForward() {
		return forward;
	}
	public void setForward(boolean forward) {
		this.forward = forward;
	}
}