package aurora.ide.editor.textpage.hover;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import aurora.ide.editor.textpage.quickfix.QuickFixInformationControl;

public class HoverInformationControlCreator implements
		IInformationControlCreator {
	public MarkerAnnotation ma = null;

	public HoverInformationControlCreator() {

	}

	public HoverInformationControlCreator(MarkerAnnotation ma) {
		this.ma = ma;
	}

	public IInformationControl createInformationControl(Shell parent) {
		if (ma != null) {
			return new QuickFixInformationControl(parent, ma, "Click for focus");
		}
		HoverInformationControl bic = new HoverInformationControl(parent,
				"sans-serif", "Click for focus");
//		Point computeSizeConstraints = bic.computeSizeConstraints(150, 100);
//		bic.setSize(computeSizeConstraints.x, computeSizeConstraints.y);
		return bic;
	}
}
