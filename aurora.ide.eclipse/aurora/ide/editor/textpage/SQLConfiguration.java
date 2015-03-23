package aurora.ide.editor.textpage;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import aurora.ide.editor.textpage.scanners.SQLCodeScanner;



public class SQLConfiguration extends SourceViewerConfiguration {

	private XMLDoubleClickStrategy doubleClickStrategy;
	private SQLCodeScanner textScanner;
	private ColorManager colorManager;

	public SQLConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE};
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new XMLDoubleClickStrategy(sourceViewer,contentType);
		return doubleClickStrategy;
	}
	
	protected SQLCodeScanner getSQLScanner() {
		if (textScanner == null) {
			textScanner = new SQLCodeScanner(colorManager);
			textScanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(IColorConstants.DEFAULT))));
		}
		return textScanner;
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				getSQLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		return reconciler;
	}
}