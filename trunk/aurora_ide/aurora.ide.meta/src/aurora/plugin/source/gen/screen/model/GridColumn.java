package aurora.plugin.source.gen.screen.model;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;

public class GridColumn extends RowCol implements IDatasetFieldDelegate {

	public static final String GRIDCOLUMN = "gridcolumn";
	/**
	 * 
	 */
	private static final String[] editors = { "", Input.TEXT, Input.NUMBER,
			Input.Combo, Input.LOV, CheckBox.CHECKBOX, Input.DATE_PICKER,
			Input.DATETIMEPICKER };
	public static final String EDITOR = "editor";
	public static final String RENDERER = "renderer";
	public static final String FOOTRENDERER = "footRenderer";

	// private List<GridColumn> cols = new ArrayList<GridColumn>();
	// default row height 25
	private int rowHight = 25;
	// private String editor = editors[0];
	private Renderer renderer = new Renderer();
	private FootRenderer footRenderer = new FootRenderer();
	private DatasetField dsField = new DatasetField();

	// private static final IPropertyDescriptor[] pds = new
	// IPropertyDescriptor[] {
	// PD_PROMPT,
	// PD_WIDTH,
	// PD_NAME,
	// new ComboPropertyDescriptor(EDITOR, "editor", editors),
	// new DialogPropertyDescriptor(RENDERER, "renderer",
	// RendererEditDialog.class),
	// new DialogPropertyDescriptor(FOOTRENDERER, "footRenderer",
	// FootRendererEditDialog.class),
	// new BooleanPropertyDescriptor(READONLY, "*readOnly"),
	// new BooleanPropertyDescriptor(REQUIRED, "*required") };

	public int getRowHight() {
		return rowHight;
		// return
		// this.getIntegerPropertyValue(ComponentInnerProperties.GRIDCOLUMN_ROWHIGHT);
	}

	public void setRowHight(int rowHight) {
		this.rowHight = rowHight;
		// this.setPropertyValue(ComponentInnerProperties.GRIDCOLUMN_ROWHIGHT,
		// rowHight);
	}

	// public List<GridColumn> getCols() {
	// return cols;
	// }

	public GridColumn() {
		super();
		// this.row = 1;
		// this.col = 999;
		this.setCol(999);
		this.setRow(1);
		this.headHight = 25;
		this.setSize(100, rowHight * 2 + 10);
		this.setComponentType(GRIDCOLUMN);
		setPrompt("prompt");
		this.setEditor(editors[0]);
		Renderer r = new Renderer();

		this.setRenderer(r);
		FootRenderer fr = new FootRenderer();

		this.setFootRenderer(fr);
	}

	public void addCol(GridColumn col) {
		// cols.add(col);
		this.addChild(col);
	}

	public boolean isResponsibleChild(AuroraComponent child) {
		// only allow add GridColumn
		return child.getClass().equals(GridColumn.class);
	}

	// public Object getPropertyValue(Object propName) {
	// if (EDITOR.equals(propName))
	// return Arrays.asList(editors).indexOf(getEditor());
	// else if (RENDERER.equals(propName))
	// return getRenderer();
	// else if (FOOTRENDERER.equals(propName))
	// return getFootRenderer();
	// else if (READONLY.equals(propName)) {
	// return getDatasetField().isReadOnly();
	// } else if (REQUIRED.equals(propName))
	// return getDatasetField().isRequired();
	// if (DatasetField.DISPLAY_FIELD.equals(propName)
	// || DatasetField.VALUE_FIELD.equals(propName)
	// || DatasetField.RETURN_FIELD.equals(propName)
	// || DatasetField.OPTIONS.equals(propName)
	// || DatasetField.LOV_GRID_HEIGHT.equals(propName)
	// || DatasetField.LOV_HEIGHT.equals(propName)
	// || DatasetField.LOV_SERVICE.equals(propName)
	// || DatasetField.LOV_URL.equals(propName)
	// || DatasetField.LOV_WIDTH.equals(propName)
	// || DatasetField.TITLE.equals(propName))
	// return dsField.getPropertyValue(propName);
	// if (DatasetField.UNCHECKED_VALUE.equals(propName)
	// || DatasetField.CHECKED_VALUE.equals(propName)
	// || DatasetField.DEFAULT_VALUE.equals(propName)) {
	// return getDatasetField().getPropertyValue(propName);
	// }
	// return super.getPropertyValue(propName);
	// }

	// public void setPropertyValue(Object propName, Object val) {
	// if (EDITOR.equals(propName))
	// setEditor(editors[(Integer) val]);
	// else if (RENDERER.equals(propName))
	// setRenderer((Renderer) val);
	// else if (FOOTRENDERER.equals(propName))
	// setFootRenderer((FootRenderer) val);
	// else if (READONLY.equals(propName)) {
	// setReadOnly((Boolean) val);
	// } else if (REQUIRED.equals(propName)) {
	// setRequired((Boolean) val);
	// } else if (DatasetField.DISPLAY_FIELD.equals(propName)
	// || DatasetField.VALUE_FIELD.equals(propName)
	// || DatasetField.RETURN_FIELD.equals(propName)
	// || DatasetField.OPTIONS.equals(propName)
	// || DatasetField.LOV_GRID_HEIGHT.equals(propName)
	// || DatasetField.LOV_HEIGHT.equals(propName)
	// || DatasetField.LOV_SERVICE.equals(propName)
	// || DatasetField.LOV_URL.equals(propName)
	// || DatasetField.LOV_WIDTH.equals(propName)
	// || DatasetField.TITLE.equals(propName))
	// dsField.setPropertyValue(propName, val);
	// else if (DatasetField.UNCHECKED_VALUE.equals(propName)
	// || DatasetField.CHECKED_VALUE.equals(propName)
	// || DatasetField.DEFAULT_VALUE.equals(propName)) {
	// getDatasetField().setPropertyValue(propName, val);
	// } else
	// super.setPropertyValue(propName, val);
	// }

	public void setRenderer(Renderer r) {
		this.renderer = r;
		r.setColumn(this);
		firePropertyChange(ComponentProperties.renderer, null, r);
		// r.setColumn(this);
		// this.setPropertyValue(ComponentInnerProperties.GRID_COLUMN_RENDERER,
		// r);
	}

	public Renderer getRenderer() {
//		Object o = this
//				.getPropertyValue(ComponentInnerProperties.GRID_COLUMN_RENDERER);
		 return renderer;
//		if (o instanceof Renderer)
//			return (Renderer) o;
//		return null;
	}

	public void setFootRenderer(FootRenderer r) {
		this.footRenderer = r;
		r.setColumn(this);
		firePropertyChange(ComponentProperties.footerRenderer, null, r);
		r.setColumn(this);
		// this.setPropertyValue(
		// ComponentInnerProperties.GRID_COLUMN_FOOTRENDERER, r);
	}

	public FootRenderer getFootRenderer() {
		return footRenderer;
		// Object o = this
		// .getPropertyValue(ComponentInnerProperties.GRID_COLUMN_FOOTRENDERER);
		// if (o instanceof FootRenderer)
		// return (FootRenderer) o;
		// return null;
	}

	public String getEditor() {
		// return editor;
		return this.getStringPropertyValue(ComponentProperties.editor);
	}

	public void setEditor(String editor) {
		// if (eq(this.editor, editor))
		// return;
		// String oldV = this.editor;
		// this.editor = editor;
		// firePropertyChange(EDITOR, oldV, editor);
		this.setPropertyValue(ComponentProperties.editor, editor);
	}

	public void setHeadHight(int h) {
		this.headHight = h;
	}

	public DatasetField getDatasetField() {
		return dsField;
	}

	public void setReadOnly(boolean readOnly) {
		// if (dsField.isReadOnly() == readOnly)
		// return;
		dsField.setReadOnly(readOnly);
		// firePropertyChange(READONLY, !readOnly, readOnly);
	}

	public void setRequired(boolean required) {
		// if (dsField.isRequired() == required)
		// return;
		dsField.setRequired(required);
		// firePropertyChange(REQUIRED, !required, required);
	}

	public void setParent(Container part) {
		super.setParent(part);
		// if (dsField != null)
		// dsField.setDataset(DataSetFieldUtil.findDataset(getParent()));
	}

	public void setDatasetField(DatasetField field) {
		dsField = field;
		dsField.setName(getName());
		// dsField.setDataset(DataSetFieldUtil.findDataset(getParent()));
	}

	@Override
	public Object getPropertyValue(String propId) {
		if (ComponentProperties.footerRenderer.equals(propId)) {
			return this.getFootRenderer();
		}
		if (ComponentProperties.renderer.equals(propId)) {
			return this.getRenderer();
		}
		if (ComponentInnerProperties.DATASET_FIELD_DELEGATE.equals(propId)) {
			return dsField;
		}
		return super.getPropertyValue(propId);
	}

	@Override
	public void setPropertyValue(String propId, Object val) {
		if (ComponentProperties.renderer.equals(propId)
				&& val instanceof Renderer) {
			this.setRenderer((Renderer) val);
			return;
		}
		if (ComponentProperties.footerRenderer.equals(propId)
				&& val instanceof FootRenderer) {
			this.setFootRenderer((FootRenderer) val);
			return;
		}
		if (ComponentInnerProperties.DATASET_FIELD_DELEGATE.equals(propId)
				&& val instanceof DatasetField) {
			this.setDatasetField((DatasetField) val);
			return;
		}
		super.setPropertyValue(propId, val);
	}
	public void addPropertyChangeListener(PropertyChangeListener l) {
		super.addPropertyChangeListener(l);
		if (dsField != null) {
			dsField.addPropertyChangeListener(l);
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		super.removePropertyChangeListener(l);
		if (dsField != null) {
			dsField.removePropertyChangeListener(l);
		}
	}
}
