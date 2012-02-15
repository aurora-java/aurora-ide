package aurora.ide.meta.gef.editors.models;

import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.DialogPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.RendererEditDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class GridColumn extends RowCol {

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
	// 界面默认的行高 25
	private int rowHight = 25;
	private String editor = editors[0];
	private Renderer renderer = new Renderer();

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT,
			PD_WIDTH,
			PD_NAME,
			new ComboPropertyDescriptor(EDITOR, "editor", editors),
			new DialogPropertyDescriptor(RENDERER, "renderer",
					RendererEditDialog.class) };

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
		renderer.setColumn(this);
	}

	public void addCol(GridColumn col) {
		cols.add(col);
		this.addChild(col);
	}

	/**
	 * 
	 * 仅允许增加 GridColumn
	 * */
	public boolean isResponsibleChild(AuroraComponent child) {
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
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (EDITOR.equals(propName))
			setEditor(editors[(Integer) val]);
		else if (RENDERER.equals(propName))
			setRenderer((Renderer) val);
		super.setPropertyValue(propName, val);
	}

	public void setRenderer(Renderer r) {
		this.renderer = r;
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

}
