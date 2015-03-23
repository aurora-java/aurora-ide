/**
 * 
 */
package aurora.ide.celleditor;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.helpers.LocaleMessage;

import uncertain.composite.QualifiedName;
import uncertain.datatype.ConvertionException;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;

/**
 * @author linjinxiao
 * 
 */
public class NumberTextCellEditor extends AbstractTextCellEditor {

	public NumberTextCellEditor(CellInfo cellProperties) {
		super(cellProperties);
	}
	/**
	 * 
	 */
	public boolean validValue(String value) {
		String errorMessage = "";
		QualifiedName typeQname = cellProperties.getTypeQname();
		String typeLocalName = typeQname.getLocalName();
		DataType dt = DataTypeRegistry.getInstance().getDataType(typeLocalName);
		try {
			if (dt != null && value != null)
				dt.convert(value);
		} catch (ConvertionException e) {
			errorMessage = LocaleMessage.getString("this.value")+"'" + value + "' can not for this field <"
					+ cellProperties.getColumnName() + "> !  "
					+ e.getLocalizedMessage();
			setErrorMessage(errorMessage);
			getCellControl().setFocus();
			return false;
		}
		return super.validValue(value);
	}

	public void createCellEditor(Composite parent) {
		this.setStyle(SWT.RIGHT_TO_LEFT);
		super.create(parent);

	}
}
