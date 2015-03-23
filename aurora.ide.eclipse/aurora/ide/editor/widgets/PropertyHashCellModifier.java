/*
 * Created on 2009-7-21
 */
package aurora.ide.editor.widgets;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.editor.core.ICategoryViewer;
import aurora.ide.editor.widgets.core.CategoryLabel;


import uncertain.schema.editor.AttributeValue;

public class PropertyHashCellModifier implements ICellModifier {

	public static final String[] PROPERTY_TO_UPDATE = { PropertyHashViewer.COLUMN_VALUE };
	
	ICategoryViewer viewer;
	CellEditor cellEditor;
	public PropertyHashCellModifier(ICategoryViewer viewer) {
		super();
		this.viewer = viewer;
	}



	public boolean canModify(Object element, String property) {
		if (element instanceof CategoryLabel) {
			return false;
		}
		return PropertyHashViewer.COLUMN_VALUE.equals(property);
	}

	public Object getValue(Object element, String property) {
		AttributeValue av = (AttributeValue) element;
		if (PropertyHashViewer.COLUMN_VALUE.equals(property))
			return av.getValueString();
		else {
			return av.getAttribute().getLocalName();
		}
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem) element;
		AttributeValue av = (AttributeValue) item.getData();
		if (av instanceof CategoryLabel) {
			return ;
		}
		String attributeName = av.getAttribute().getLocalName();
		Object oldValue = av.getContainer().get(av.getAttribute().getLocalName());
		
		if((oldValue==null ||oldValue.equals(""))&&(value==null ||value.equals(""))){
			return;
		}
		
		if (oldValue == null ||(oldValue != null && !oldValue.equals(value))) {
			av.getContainer().put(attributeName, value);
			viewer.refresh(true);
		}
		if(value == null ||value.equals("")){
			av.getContainer().remove(attributeName);
		}
	}
}
