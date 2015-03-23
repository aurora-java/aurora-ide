package aurora.ide.editor.textpage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

import aurora.ide.editor.textpage.js.validate.JSValidator;
import aurora.ide.helpers.DialogUtil;

public class XmlReconcilingStrategy implements IReconcilingStrategy,
		IReconcilingStrategyExtension {

	private ProjectionViewer mSourceViewer;
	private List<IReconcileListener> listeners = new ArrayList<IReconcileListener>();

	public XmlReconcilingStrategy(ISourceViewer sourceViewer) {
		this.mSourceViewer = (ProjectionViewer) sourceViewer;
	}

	public boolean addListener(IReconcileListener l) {
		return listeners.add(l);
	}

	public void reconcile(IRegion partition) {
		noticeListener();
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		noticeListener();
	}

	public void setDocument(IDocument document) {
		noticeListener();

	}

	private void noticeListener() {
		if (!listeners.isEmpty()) {
			for (Iterator it = listeners.listIterator(); it.hasNext();) {
				try {
					((IReconcileListener) it.next()).reconcile();
				} catch (Throwable e) {
					DialogUtil.showExceptionMessageBox(e);
				}
			}
		}
	}

	public void initialReconcile() {
		noticeListener();
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
	}
}
