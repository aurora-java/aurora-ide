package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Control;

/**
 * 
 */
public class SimpleDataCellEditorLocator implements CellEditorLocator {
	private InputField nodeFigure;

	/**
	 * 
	 * @param nodeFigure
	 *            the Label
	 */
	public SimpleDataCellEditorLocator(InputField nodeFigure) {
		this.nodeFigure = nodeFigure;
	}

	/**
	 * @see CellEditorLocator#relocate(org.eclipse.jface.viewers.CellEditor)
	 */
	public void relocate(CellEditor celleditor) {
		Control text =  celleditor.getControl();
		int labelWidth = nodeFigure.getLabelWidth();
		Rectangle bounds = nodeFigure.getBounds().getCopy();
		nodeFigure.translateToAbsolute(bounds);
		text.setBounds(bounds.x + labelWidth - 1, bounds.y - 1, bounds.width
				- labelWidth + 1, bounds.height + 1);
	}

}