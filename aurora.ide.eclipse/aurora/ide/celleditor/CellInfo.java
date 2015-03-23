/**
 * 
 */
package aurora.ide.celleditor;



import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.editor.core.ITableViewer;
import aurora.ide.helpers.LoadSchemaManager;


import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Enumeration;
import uncertain.schema.IType;
import uncertain.schema.Restriction;
import uncertain.schema.SimpleType;

/**
 * @author linjinxiao
 *
 */
public class CellInfo {
	
	protected String[] items;
	protected ITableViewer tableViewer;
	protected boolean required;
	protected String columnName;
	protected CompositeMap record;
	protected TableItem tableItem;
	protected QualifiedName typeQname;
	public QualifiedName getTypeQname() {
		return typeQname;
	}
	public void setTypeQname(QualifiedName typeQname) {
		this.typeQname = typeQname;
	}
	//for schema grid
	public CellInfo(ITableViewer tableViewer,Attribute attribute){
		this.tableViewer = tableViewer;
		parseAttribute(attribute);
	}
	//for schema form
	public CellInfo(ITableViewer tableViewer,Attribute attribute,CompositeMap record,TableItem tableItem){
		this(tableViewer, attribute);
		this.record = record;
		this.tableItem = tableItem;
			
	}
	//for grid
	public  CellInfo(ITableViewer tableViewer,String  columnName, boolean required){
		this.tableViewer = tableViewer;
		this.columnName = columnName;
		this.required = required;
	}
	//for form
	public  CellInfo(ITableViewer tableViewer,String  columnName, boolean required,CompositeMap record,TableItem tableItem){
		this(tableViewer,columnName,required);
		this.record = record;
		this.tableItem = tableItem;
	}
	public String getColumnName() {
		return columnName;
	}
	public String[] getItems() {
		return items;
	}
	public CompositeMap getRecord() {
		return record;
	}
	public TableItem getTableItem() {
		return tableItem;
	}
	public ITableViewer getTableViewer() {
		return tableViewer;
	}
	public Table getTable(){
		return tableViewer.getViewer().getTable();
	}
	public boolean isRequired() {
		return required;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public void setItems(String[] items) {
		this.items = items;
	}
	public void setRecord(CompositeMap record) {
		this.record = record;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public void setTableItem(TableItem tableItem) {
		this.tableItem = tableItem;
	}
	
	public void setTableViewer(ITableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}
	
	private void iniItems(Attribute attribute) {
		QualifiedName typeQname = attribute.getTypeQName();
		if (typeQname == null)
			return;
		IType type = LoadSchemaManager.getSchemaManager().getType(typeQname);
		if (type == null || !(type instanceof SimpleType)) {
			return;
		}
		SimpleType simpleType = (SimpleType) type;
		Restriction rest = simpleType.getRestriction();
		if (rest != null) {
			Enumeration[] emus = rest.getEnumerations();
			if (emus != null) {
				if (required) {
					items = new String[emus.length];
					for (int i = 0; i < emus.length; i++) {
						items[i] = emus[i].getValue();
					}
				} else {
					items = new String[emus.length + 1];
					items[0] = "";
					for (int i = 0; i < emus.length; i++) {
						items[i + 1] = emus[i].getValue();
					}
				}
			}
		}
	}
	private void parseAttribute(Attribute attribute){
		Assert.isNotNull(attribute,"Attribute can not be null");
		columnName = attribute.getLocalName();
		if("required".equals(attribute.getUse()))
				required = true;
		typeQname = attribute.getTypeQName();
		iniItems(attribute);
	}
	
}
