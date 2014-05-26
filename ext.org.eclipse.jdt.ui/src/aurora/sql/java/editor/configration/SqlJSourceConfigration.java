package aurora.sql.java.editor.configration;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

import aurora.sql.java.editor.content.assist.SQLJContentAssistProcessor;

import patch.org.eclipse.jdt.internal.ui.JavaPlugin;
import ext.org.eclipse.jdt.internal.ui.text.ContentAssistPreference;
import ext.org.eclipse.jdt.internal.ui.text.java.ContentAssistProcessor;
import ext.org.eclipse.jdt.internal.ui.text.java.JavaCompletionProcessor;
import ext.org.eclipse.jdt.internal.ui.text.javadoc.JavadocCompletionProcessor;

public class SqlJSourceConfigration {
	private SqlJSourceScanner sqljScanner;
	private IColorManager colorManager;
	
	private IEditorPart editor;

	public static final IToken SQLJ_TOKEN = new Token(
			SqlJSourceScanner.SQLJ_source);

	public SqlJSourceConfigration(IEditorPart editor,IColorManager colorManager) {
		super();
		this.editor = editor;
		this.colorManager = colorManager;
		initializeScanners();
	}

	private void initializeScanners() {
		sqljScanner = new SqlJSourceScanner(getColorManager());

	}

	private IColorManager getColorManager() {
		return colorManager;
	}

	public void connectPresentationReconciler(PresentationReconciler reconciler) {
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(sqljScanner);
		reconciler.setDamager(dr, SqlJSourceScanner.SQLJ_source);
		reconciler.setRepairer(dr, SqlJSourceScanner.SQLJ_source);
		

//		dr = new DefaultDamagerRepairer(sqljScanner);
//		reconciler.setDamager(dr, SqlJSourceScanner.SQLJ_VAR);
//		reconciler.setRepairer(dr, SqlJSourceScanner.SQLJ_VAR);
	}

	public String[] getConfiguredContentTypes(String[] exsitTypes,
			ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				IJavaPartitions.JAVA_DOC,
				IJavaPartitions.JAVA_MULTI_LINE_COMMENT,
				IJavaPartitions.JAVA_SINGLE_LINE_COMMENT,
				IJavaPartitions.JAVA_STRING, IJavaPartitions.JAVA_CHARACTER,
				SqlJSourceScanner.SQLJ_source, SqlJSourceScanner.SQLJ_VAR };
	}
	
	
	public void installContentAssistant(ContentAssistant assistant,ISourceViewer sourceViewer) {

		if (getEditor() != null) {

			
			SQLJContentAssistProcessor sss = new SQLJContentAssistProcessor(assistant,SqlJSourceScanner.SQLJ_source);
			assistant.setContentAssistProcessor(sss, SqlJSourceScanner.SQLJ_source);

//			ContentAssistProcessor multiLineProcessor = new JavaCompletionProcessor(
//					getEditor(), assistant,
//					IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
//			assistant.setContentAssistProcessor(multiLineProcessor,
//					IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

//			ContentAssistProcessor javadocProcessor = new JavadocCompletionProcessor(
//					getEditor(), assistant);
//			assistant.setContentAssistProcessor(javadocProcessor,
//					IJavaPartitions.JAVA_DOC);
//
//			ContentAssistPreference.configure(assistant, fPreferenceStore);
//
//			assistant
//					.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
//			assistant
//					.setInformationControlCreator(new IInformationControlCreator() {
//						public IInformationControl createInformationControl(
//								Shell parent) {
//							return new DefaultInformationControl(
//									parent,
//									JavaPlugin
//											.getAdditionalInfoAffordanceString());
//						}
//					});

		}

	}

	private IEditorPart getEditor() {
		return editor;
	}
}
