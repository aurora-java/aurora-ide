package aurora.ide.meta.gef.designer.editor;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

public class BMModelLabelProvider extends BaseLabelProvider implements
		ITableLabelProvider, ITableColorProvider, ILabelProvider {
	private static Color COLOR_ODD = new Color(null, 245, 255, 255);
	private static Color COLOR_EVEN = new Color(null, 255, 255, 255);
	private int type = BMModel.RECORD;
	private String[] column_properties;

	public BMModelLabelProvider(int type) {
		super();
		this.type = type;
	}

	public BMModelLabelProvider(int type, String[] properties) {
		this.type = type;
		this.column_properties = properties;
	}

	private String getProperty(int idx) {
		if (column_properties == null || idx >= column_properties.length)
			return null;
		return column_properties[idx];
	}

	public Color getForeground(Object element, int columnIndex) {
		if (IDesignerConst.COLUMN_NUM.equals(getProperty(columnIndex)))
			return ColorConstants.gray;
		return null;
	}

	public Color getBackground(Object element, int columnIndex) {
		int rowNum = ((Record) element).getNum();
		return (rowNum % 2 == 0) ? COLOR_EVEN : COLOR_ODD;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Record r = (Record) element;
		// if (columnIndex == columnNumIndx)
		// return "" + r.getNum();
		// if (type == BMModel.RELATION)
		// return
		// r.getStringNotNull(RelationViewer.COLUMN_PROPERTIES[columnIndex]);
		// return "";
		return r.getStringNotNull(getProperty(columnIndex));
	}

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		Record r = (Record) element;
		return r.getPrompt();
	}
}
