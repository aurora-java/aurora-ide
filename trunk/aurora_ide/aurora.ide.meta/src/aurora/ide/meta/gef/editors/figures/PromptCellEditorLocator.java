
package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

public class PromptCellEditorLocator implements CellEditorLocator {
	private InputField nodeFigure;

	public PromptCellEditorLocator(InputField nodeFigure) {
		this.nodeFigure = nodeFigure;
	}

	/**
	 * @see CellEditorLocator#relocate(org.eclipse.jface.viewers.CellEditor)
	 */
	public void relocate(CellEditor celleditor) {
		Text text = (Text) celleditor.getControl();
		Rectangle bounds = nodeFigure.getBounds().getCopy();
		nodeFigure.translateToAbsolute(bounds);
		int labelWidth = nodeFigure.getLabelWidth();
		text.setBounds(bounds.x - 1, bounds.y - 1, labelWidth + 1,
				bounds.height + 1);
//		Text text = (Text) celleditor.getControl();
//        Point pref = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//        Rectangle rect = this.nodeFigure.getTextBounds();
//        text.setBounds(rect.x - 1, rect.y - 1, pref.x + 1, pref.y + 1);
	}

}