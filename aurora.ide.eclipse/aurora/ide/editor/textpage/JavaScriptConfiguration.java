package aurora.ide.editor.textpage;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import aurora.ide.editor.textpage.scanners.JSEditorCodeScanner;

public class JavaScriptConfiguration extends SourceViewerConfiguration {

    private XMLDoubleClickStrategy doubleClickStrategy;
    private JSEditorCodeScanner    textScanner;
    private ColorManager           colorManager;

    public JavaScriptConfiguration(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    @Override
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        return new IAutoEditStrategy[] { new JavaScriptAutoIndentStrategy() };
    }

    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] { IDocument.DEFAULT_CONTENT_TYPE };
    }

    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        if (doubleClickStrategy == null)
            doubleClickStrategy = new XMLDoubleClickStrategy(sourceViewer, contentType);
        return doubleClickStrategy;
    }

    protected JSEditorCodeScanner getJSScanner() {
        if (textScanner == null) {
            textScanner = new JSEditorCodeScanner(colorManager);
            textScanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager
                    .getColor(IColorConstants.DEFAULT))));
        }
        return textScanner;
    }

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getJSScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        return reconciler;
    }
}
