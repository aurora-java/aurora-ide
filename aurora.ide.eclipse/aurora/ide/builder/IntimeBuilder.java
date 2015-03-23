package aurora.ide.builder;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;

import aurora.ide.AuroraPlugin;
import aurora.ide.AuroraProjectNature;
import aurora.ide.builder.validator.IntimeValidator;
import aurora.ide.editor.textpage.IReconcileListener;
import aurora.ide.preferencepages.CustomSettingPreferencePage;

public class IntimeBuilder implements IReconcileListener {
	private ISourceViewer sourceViewer;
	private IFile file;

	/**
	 * @param sourceViewer
	 */
	public IntimeBuilder(ISourceViewer sourceViewer) {
		super();
		this.sourceViewer = sourceViewer;
	}

	public void reconcile() {
		
		if (!CustomSettingPreferencePage.getIntimeBuildEnable()) {
			return;
		}
		IAnnotationModel am = sourceViewer.getAnnotationModel();
		@SuppressWarnings("unchecked")
		Iterator<Annotation> itr = am.getAnnotationIterator();
		while (itr.hasNext()) {
			Annotation anno = itr.next();
			if (anno.getText() == null)
				continue;
			// if any xml error exists , do nothing
			if (anno.getClass().equals(
					org.eclipse.jface.text.source.Annotation.class)) {
				return;
			}
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					file = AuroraPlugin.getActiveIFile();
					if (file == null)
						return;
					if (!AuroraProjectNature.hasAuroraNature(file.getProject()))
						return;
					IDocument doc = sourceViewer.getDocument();
					new IntimeValidator(file, doc).validate();
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		});
	}
}
