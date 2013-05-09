package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

public class GridColumnCellEditorLocator implements CellEditorLocator {
	private GridColumnFigure nodeFigure;
	private int idx = 0;

	public GridColumnCellEditorLocator(GridColumnFigure nodeFigure, int idx) {
		this.nodeFigure = nodeFigure;
		this.idx = idx;
	}

	/**
	 * @see CellEditorLocator#relocate(org.eclipse.jface.viewers.CellEditor)
	 */
	public void relocate(CellEditor celleditor) {
		Text text = (Text) celleditor.getControl();
		Rectangle bounds = nodeFigure.getBounds().getCopy();
		nodeFigure.translateToAbsolute(bounds);
		int columnHight = nodeFigure.getColumnHight();
		text.setBounds(bounds.x - 1, bounds.y + columnHight * idx - 1,
				bounds.width + 1, columnHight + 1);
	}

}