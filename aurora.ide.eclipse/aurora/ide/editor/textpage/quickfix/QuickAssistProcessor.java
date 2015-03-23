package aurora.ide.editor.textpage.quickfix;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;

public class QuickAssistProcessor implements IQuickAssistProcessor {
	private static String[] fixableMarkerType = {
			AuroraBuilder.UNDEFINED_ATTRIBUTE, AuroraBuilder.UNDEFINED_DATASET };

	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		return true;
	}

	public boolean canFix(Annotation annotation) {
		if (!(annotation instanceof MarkerAnnotation))
			return false;
		MarkerAnnotation ma = (MarkerAnnotation) annotation;
		for (String s : fixableMarkerType) {
			try {
				IMarker marker = ma.getMarker();
				if (marker == null)
					return false;
				if (s.equals(marker.getType()))
					return true;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public ICompletionProposal[] computeQuickAssistProposals(
			IQuickAssistInvocationContext invocationContext) {
		ISourceViewer viewer = invocationContext.getSourceViewer();
		IAnnotationModel am = viewer.getAnnotationModel();
		@SuppressWarnings("unchecked")
		Iterator<Annotation> itr = am.getAnnotationIterator();
		MarkerAnnotation anno = null;
		while (itr.hasNext()) {
			Annotation a = itr.next();
			if (!(a instanceof MarkerAnnotation))
				continue;
			Position p = am.getPosition(a);
			if (p.includes(invocationContext.getOffset())) {
				anno = (MarkerAnnotation) a;
				break;
			}
		}
		if (anno == null || !canFix(anno))
			return null;
		IDocument doc = viewer.getDocument();
		CompositeMap rootMap = null;
		try {
			rootMap = CompositeMapUtil.loaderFromString(doc.get());
		} catch (ApplicationException e3) {
			e3.printStackTrace();
			return null;
		}
		IMarker marker = anno.getMarker();
		return new CompletionProposalCreator(doc, rootMap, marker)
				.getCompletionProposal();
	}

	public String getErrorMessage() {
		return "未找到帮助信息";
	}
}
