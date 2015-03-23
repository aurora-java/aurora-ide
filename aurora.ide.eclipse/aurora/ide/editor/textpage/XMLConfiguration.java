package aurora.ide.editor.textpage;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import aurora.ide.builder.IntimeBuilder;
import aurora.ide.editor.textpage.contentassist.TagContentAssistProcessor;
import aurora.ide.editor.textpage.format.DefaultFormattingStrategy;
import aurora.ide.editor.textpage.format.DocTypeFormattingStrategy;
import aurora.ide.editor.textpage.format.PIFormattingStrategy;
import aurora.ide.editor.textpage.format.TextFormattingStrategy;
import aurora.ide.editor.textpage.format.XMLFormattingStrategy;
import aurora.ide.editor.textpage.hover.HoverInformationControlCreator;
import aurora.ide.editor.textpage.hover.TextHover;
import aurora.ide.editor.textpage.hyperlinks.FileHyperlinkDetector;
import aurora.ide.editor.textpage.js.validate.JSValidator;
import aurora.ide.editor.textpage.quickfix.QuickAssistProcessor;
import aurora.ide.editor.textpage.scanners.JSEditorCodeScanner;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.editor.textpage.scanners.XMLScanner;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;

public class XMLConfiguration extends SourceViewerConfiguration {

	private XMLDoubleClickStrategy doubleClickStrategy;

	private XMLTagScanner tagScanner;

	private XMLScanner scanner;

	private JSEditorCodeScanner cdataScanner;

	private ColorManager colorManager;

	public XMLConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				XMLPartitionScanner.XML_COMMENT, XMLPartitionScanner.XML_PI,
				XMLPartitionScanner.XML_DOCTYPE,
				XMLPartitionScanner.XML_START_TAG,
				XMLPartitionScanner.XML_END_TAG, XMLPartitionScanner.XML_CDATA };
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new XMLDoubleClickStrategy(sourceViewer,
					contentType);
		return doubleClickStrategy;
	}

	protected XMLScanner getXMLScanner() {
		if (scanner == null) {
			scanner = new XMLScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(IColorConstants.DEFAULT))));
		}
		return scanner;
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		if (contentType.equals(XMLPartitionScanner.XML_CDATA))
			return new IAutoEditStrategy[] { new JavaScriptAutoIndentStrategy() };
		IAutoEditStrategy[] aes = super.getAutoEditStrategies(sourceViewer,
				contentType);
		IAutoEditStrategy[] aes2 = new IAutoEditStrategy[aes.length + 1];
		System.arraycopy(aes, 0, aes2, 0, aes.length);
		aes2[aes.length] = new XMLAutoEditStrategy();
		return aes2;
	}

	protected JSEditorCodeScanner getCDataScanner() {
		if (cdataScanner == null) {
			cdataScanner = new JSEditorCodeScanner(colorManager);
			cdataScanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(IColorConstants.CDATA_TEXT))));
		}
		return cdataScanner;
	}

	protected XMLTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new XMLTagScanner(colorManager);
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(IColorConstants.TAG))));
		}
		return tagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				getXMLTagScanner());
		reconciler.setDamager(dr, XMLPartitionScanner.XML_START_TAG);
		reconciler.setRepairer(dr, XMLPartitionScanner.XML_START_TAG);

		dr = new DefaultDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, XMLPartitionScanner.XML_END_TAG);
		reconciler.setRepairer(dr, XMLPartitionScanner.XML_END_TAG);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, XMLPartitionScanner.XML_DOCTYPE);
		reconciler.setRepairer(dr, XMLPartitionScanner.XML_DOCTYPE);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, XMLPartitionScanner.XML_PI);
		reconciler.setRepairer(dr, XMLPartitionScanner.XML_PI);

		dr = new DefaultDamagerRepairer(getCDataScanner());
		reconciler.setDamager(dr, XMLPartitionScanner.XML_CDATA);
		reconciler.setRepairer(dr, XMLPartitionScanner.XML_CDATA);

		TextAttribute textAttribute = new TextAttribute(
				colorManager.getColor(IColorConstants.XML_COMMENT));
		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
				textAttribute);
		reconciler.setDamager(ndr, XMLPartitionScanner.XML_COMMENT);
		reconciler.setRepairer(ndr, XMLPartitionScanner.XML_COMMENT);

		return reconciler;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		ContentAssistant assistant = new ContentAssistant();

		assistant.setContentAssistProcessor(new TagContentAssistProcessor(
				getXMLTagScanner()), XMLPartitionScanner.XML_START_TAG);
		assistant.setContentAssistProcessor(new TagContentAssistProcessor(
				getXMLTagScanner()), IDocument.DEFAULT_CONTENT_TYPE);
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(200);
		assistant
				.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant
				.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant
				.setInformationControlCreator(new HoverInformationControlCreator());
		return assistant;

	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		ContentFormatter formatter = new ContentFormatter();
		XMLFormattingStrategy formattingStrategy = new XMLFormattingStrategy();
		DefaultFormattingStrategy defaultStrategy = new DefaultFormattingStrategy();
		TextFormattingStrategy textStrategy = new TextFormattingStrategy();
		DocTypeFormattingStrategy doctypeStrategy = new DocTypeFormattingStrategy();
		PIFormattingStrategy piStrategy = new PIFormattingStrategy();
		formatter.setFormattingStrategy(defaultStrategy,
				IDocument.DEFAULT_CONTENT_TYPE);
		formatter.setFormattingStrategy(doctypeStrategy,
				XMLPartitionScanner.XML_DOCTYPE);
		formatter.setFormattingStrategy(piStrategy, XMLPartitionScanner.XML_PI);
		formatter.setFormattingStrategy(textStrategy,
				XMLPartitionScanner.XML_CDATA);
		formatter.setFormattingStrategy(formattingStrategy,
				XMLPartitionScanner.XML_START_TAG);
		formatter.setFormattingStrategy(formattingStrategy,
				XMLPartitionScanner.XML_END_TAG);

		return formatter;
	}

	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		return new IHyperlinkDetector[] { new FileHyperlinkDetector() };
	}

	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new DefaultAnnotationHover();
	}

	public static RGB getTokenType(IToken token) {
		if (token == null || !(token instanceof TextAttribute))
			return null;
		TextAttribute textAttribute = (TextAttribute) token.getData();
		return textAttribute.getForeground().getRGB();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler
	 * (org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		XmlReconcilingStrategy strategy = new XmlReconcilingStrategy(
				sourceViewer);
		strategy.addListener(new XmlErrorReconcile(sourceViewer));
		strategy.addListener(new ProjectionReconcile((ProjectionViewer) sourceViewer));
		strategy.addListener(new IntimeBuilder(sourceViewer));
		strategy.addListener(new JSValidator(sourceViewer));
		MonoReconciler reconciler = new MonoReconciler(strategy, false);
		return reconciler;
	}

	public IUndoManager getUndoManager(ISourceViewer sourceViewer) {
		IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
		return new TextViewerUndoManager(
				generalTextStore
						.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_UNDO_HISTORY_SIZE));
		// return new TextViewerUndoManager(200);

	}

	@Override
	public IQuickAssistAssistant getQuickAssistAssistant(
			ISourceViewer sourceViewer) {
		QuickAssistAssistant qaa = new QuickAssistAssistant();
		qaa.setInformationControlCreator(new HoverInformationControlCreator());
		IQuickAssistProcessor qap = new QuickAssistProcessor();
		qaa.setQuickAssistProcessor(qap);
		return qaa;
	}

	@Override
	public ITextHover getTextHover(final ISourceViewer sourceViewer,
			String contentType) {
		ITextHover th = new TextHover(sourceViewer);
		return th;
	}
}
