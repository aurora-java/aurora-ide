/*
 * Created on 2009-7-21
 */
package aurora.ide.editor.widgets;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import aurora.ide.AuroraPlugin;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.editor.widgets.core.IGridLabelProvider;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;

import uncertain.composite.CompositeMap;

public class PropertyGridLabelProvider extends BaseLabelProvider implements
		IGridLabelProvider, ITableColorProvider {

	/**
	 * @param attribArray
	 */
	int nodeIndex;
	int nodeCount;
	String[] gridPropties;
	private GridViewer viewer;

	public PropertyGridLabelProvider(String[] gridPropties, GridViewer viewer) {
		super();
		this.gridPropties = gridPropties;
		this.viewer = viewer;
	}

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return element.toString();
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public Image getColumnImage(Object element, int columnIndex) {

		if (columnIndex == 0) {
			return null;
		}
		if (gridPropties == null || gridPropties.length == 0)
			return null;

		ICellEditor cellEditor = viewer
				.getCellEditor(gridPropties[columnIndex - 1]);

		if (cellEditor != null && cellEditor instanceof CheckboxCellEditor) {
			CompositeMap data = (CompositeMap) element;
			String returnValue = data.getString(gridPropties[columnIndex - 1]);
			if (returnValue != null && returnValue.equals("true")) {

				// return
				// AuroraPlugin.getImageDescriptor(LocaleMessage.getString("checked.icon")).createImage();
				return ImagesUtils.getImage("checked.gif");
			}
			// return
			// AuroraPlugin.getImageDescriptor(LocaleMessage.getString("unchecked.icon")).createImage();
			return ImagesUtils.getImage("unchecked.gif");

		}

		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		CompositeMap data = (CompositeMap) element;
		int nowCount = data.getParent().getChildsNotNull().size();
		// if nodes has changed,reset the nodeIndex;
		if (nowCount != nodeCount) {
			nodeCount = nowCount;
			nodeIndex = 0;
		}
		// the first column is sequence.
		if (columnIndex == 0) {
			if ((viewer.getGridStyle() & IGridViewer.NoSeqColumn) != 0) {
				return "";
			}
			return String.valueOf(++nodeIndex);
		}

		if (gridPropties == null || gridPropties.length == 0)
			return null;
		String attrName = gridPropties[columnIndex - 1];

		ICellEditor cellEditor = viewer.getCellEditor(attrName);
		String returnValue = data.getString(attrName);

		if (cellEditor != null) {
			if (returnValue != null)
				cellEditor.SetSelection(returnValue);
		}
		return returnValue;
	}

	public void refresh() {
		nodeIndex = 0;
	}

	public Color getBackground(Object element, int columnIndex) {
		if (columnIndex == 0)
			if (rowNum % 2 == 0)
				rowNum++;
			else
				rowNum--;
		return (rowNum == 0) ? COLOR_EVEN : COLOR_ODD;
	}

	private int rowNum = 0;
	private Color COLOR_ODD = new Color(null, 245, 255, 255);
	private Color COLOR_EVEN = new Color(null, 255, 255, 255);

	public Color getForeground(Object element, int columnIndex) {
		return null;
	}
}
