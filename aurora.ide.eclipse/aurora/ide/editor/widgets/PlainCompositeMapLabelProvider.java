package aurora.ide.editor.widgets;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import aurora.ide.editor.widgets.core.IGridLabelProvider;


import uncertain.composite.CompositeMap;

public class PlainCompositeMapLabelProvider extends LabelProvider implements IGridLabelProvider,ITableColorProvider  {

	int nodeIndex;
	public String[] columnProperties;

	public PlainCompositeMapLabelProvider(String[] columnProperties) {
		this.columnProperties = columnProperties;
	}

	public String getColumnText(Object element, int columnIndex) {
		CompositeMap record = (CompositeMap) element;
		
    	//the first column is sequence.
    	if(columnIndex == 0){
    		int returnInt = ++nodeIndex;
    		return String.valueOf(returnInt);
    	}
		String propertyName = columnProperties[columnIndex-1];
		return record.getString(propertyName);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
	public void refresh(){
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
	private Color COLOR_ODD = new Color(null,245,255,255);
	private Color COLOR_EVEN = new Color(null, 255,255,255);

	public Color getForeground(Object element, int columnIndex) {
		return null;
	}
}