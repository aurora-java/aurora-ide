package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.DialogPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.RendererEditDialog;
import aurora.ide.meta.gef.editors.source.gen.DataSetFieldUtil;

public class GridColumn extends RowCol implements IDatasetFieldDelegate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3032139528088861361L;
	private static final String[] editors = { "", Input.TEXT, Input.NUMBER,
			Input.Combo, Input.LOV, CheckBox.CHECKBOX, Input.CAL,
			Input.DATETIMEPICKER };
	public static final String EDITOR = "editor";
	public static final String RENDERER = "renderer";

	private List<GridColumn> cols = new ArrayList<GridColumn>();
	// default row height 25
	private int rowHight = 25;
	private String editor = editors[0];
	private Renderer renderer = new Renderer();
	private DatasetField dsField = new DatasetField();

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT,
			PD_WIDTH,
			PD_NAME,
			new ComboPropertyDescriptor(EDITOR, "editor", editors),
			new DialogPropertyDescriptor(RENDERER, "renderer",
					RendererEditDialog.class),
			new BooleanPropertyDescriptor(READONLY, "*readOnly"),
			new BooleanPropertyDescriptor(REQUIRED, "*required") };

	public int getRowHight() {
		return rowHight;
	}

	public void setRowHight(int rowHight) {
		this.rowHight = rowHight;
	}

	public List<GridColumn> getCols() {
		return cols;
	}

	public GridColumn() {
		super();
		this.row = 1;
		this.col = 999;
		this.headHight = 25;
		this.setSize(new Dimension(100, rowHight * 2 + 10));
		this.setType("column");
		setPrompt("prompt");
		renderer.setColumn(this);
	}

	public void addCol(GridColumn col) {
		cols.add(col);
		this.addChild(col);
	}

	public boolean isResponsibleChild(AuroraComponent child) {
		// only allow add GridColumn
		return child.getClass().equals(GridColumn.class);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (EDITOR.equals(propName))
			return Arrays.asList(editors).indexOf(getEditor());
		else if (RENDERER.equals(propName))
			return getRenderer();
		else if (READONLY.equals(propName)) {
			return getDatasetField().isReadOnly();
		} else if (REQUIRED.equals(propName))
			return getDatasetField().isRequired();
		if (DatasetField.DISPLAY_FIELD.equals(propName)
				|| DatasetField.VALUE_FIELD.equals(propName)
				|| DatasetField.RETURN_FIELD.equals(propName)
				|| DatasetField.OPTIONS.equals(propName)
				|| DatasetField.LOV_GRID_HEIGHT.equals(propName)
				|| DatasetField.LOV_HEIGHT.equals(propName)
				|| DatasetField.LOV_SERVICE.equals(propName)
				|| DatasetField.LOV_URL.equals(propName)
				|| DatasetField.LOV_WIDTH.equals(propName)
				|| DatasetField.TITLE.equals(propName))
			return dsField.getPropertyValue(propName);
		if (DatasetField.UNCHECKED_VALUE.equals(propName)
				|| DatasetField.CHECKED_VALUE.equals(propName)
				|| DatasetField.DEFAULT_VALUE.equals(propName)) {
			return getDatasetField().getPropertyValue(propName);
		}
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (EDITOR.equals(propName))
			setEditor(editors[(Integer) val]);
		else if (RENDERER.equals(propName))
			setRenderer((Renderer) val);
		else if (READONLY.equals(propName)) {
			setReadOnly((Boolean) val);
		} else if (REQUIRED.equals(propName)) {
			setRequired((Boolean) val);
		} else if (DatasetField.DISPLAY_FIELD.equals(propName)
				|| DatasetField.VALUE_FIELD.equals(propName)
				|| DatasetField.RETURN_FIELD.equals(propName)
				|| DatasetField.OPTIONS.equals(propName)
				|| DatasetField.LOV_GRID_HEIGHT.equals(propName)
				|| DatasetField.LOV_HEIGHT.equals(propName)
				|| DatasetField.LOV_SERVICE.equals(propName)
				|| DatasetField.LOV_URL.equals(propName)
				|| DatasetField.LOV_WIDTH.equals(propName)
				|| DatasetField.TITLE.equals(propName))
			dsField.setPropertyValue(propName, val);
		else if (DatasetField.UNCHECKED_VALUE.equals(propName)
				|| DatasetField.CHECKED_VALUE.equals(propName)
				|| DatasetField.DEFAULT_VALUE.equals(propName)) {
			getDatasetField().setPropertyValue(propName, val);
		} else
			super.setPropertyValue(propName, val);
	}

	public void setRenderer(Renderer r) {
		this.renderer = r;
		r.setColumn(this);
		firePropertyChange(RENDERER, null, r);
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		if (eq(this.editor, editor))
			return;
		String oldV = this.editor;
		this.editor = editor;
		firePropertyChange(EDITOR, oldV, editor);
	}

	public void setHeadHight(int h) {
		this.headHight = h;
	}

	public DatasetField getDatasetField() {
		return dsField;
	}

	public void setReadOnly(boolean readOnly) {
		if (dsField.isReadOnly() == readOnly)
			return;
		dsField.setReadOnly(readOnly);
		firePropertyChange(READONLY, !readOnly, readOnly);
	}

	public void setRequired(boolean required) {
		if (dsField.isRequired() == required)
			return;
		dsField.setRequired(required);
		firePropertyChange(REQUIRED, !required, required);
	}

	public void setParent(Container part) {
		super.setParent(part);
		if (dsField != null)
			dsField.setDataset(DataSetFieldUtil.findDataset(getParent()));
	}

	public void setDatasetField(DatasetField field) {
		dsField = field;
		dsField.setName(getName());
		dsField.setDataset(DataSetFieldUtil.findDataset(getParent()));
	}
}
