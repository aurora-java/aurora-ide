package aurora.ide.meta.gef.editors.models;

import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.property.DialogEditableObject;

public class Renderer extends AuroraComponent implements DialogEditableObject {

	private static final long serialVersionUID = -3218999047690358423L;
	public static final String PAGE_REDIRECT = "PAGE_REDIRECT";
	public static final String INNER_FUNCTION = "INNER_FUNCTION";
	public static final String USER_FUNCTION = "USER_FUNCTION";
	public static final String[] RENDERER_TYPES = { PAGE_REDIRECT,
			INNER_FUNCTION, USER_FUNCTION };

	// PAGE_REDIRECT
	private String openPath = "";
	private String labelText = "";

	/**
	 * 内置函数名
	 */
	private String functionName = "";

	/**
	 * 所有预定义renderer
	 */
	public static final String[] INNER_FUNCTIONS = { "Aurora.formatDate",
			"Aurora.formatDateTime", "Aurora.formatNumber" };
	/**
	 * 预定义renderer对应的描述
	 */
	public static final String[] INNER_RENDERER_DESC = {
			"将日期转换成默认格式的字符串，默认格式是根据Aurora.defaultDateFormat来定义的.如果没有特殊指定,默认格式为yyyy-mm-dd",
			"将日期转换成yyyy-mm-dd HH:MM:ss格式的字符串", "将数值根据精度转换成带有千分位的字符串" };

	/**
	 * 自定义函数
	 */
	private String function = "";

	/**
	 * 自定义函数模板(范例)
	 */
	public static final String FUNCTION_MODEL = "function(value,record,name){\n\treturn 'rendererText';\n}";

	private GridColumn column;
	private String rendererType = PAGE_REDIRECT;

	public Renderer() {

	}

	public void setColumn(GridColumn col) {
		column = col;
	}

	public GridColumn getColumn() {
		return column;
	}

	public String getDescripition() {
		return labelText;
	}

	public Object getContextInfo() {
		return column;
	}

	public Renderer clone() {
		Renderer r = new Renderer();
		r.openPath = openPath;
		r.column = column;
		r.labelText = labelText;
		r.function = function;
		r.functionName = functionName;
		r.rendererType = rendererType;
		return r;
	}

	public String getOpenPath() {
		return openPath;
	}

	public void setOpenPath(String openPath) {
		this.openPath = openPath;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String tmpLabelText) {
		this.labelText = tmpLabelText;
	}

	public Image getDisplayImage() {
		return null;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getRendererType() {
		return rendererType;
	}

	public void setRendererType(String rendererType) {
		this.rendererType = rendererType;
	}

}
