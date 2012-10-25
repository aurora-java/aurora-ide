package aurora.ide.meta.gef.editors.models;

import org.eclipse.swt.graphics.Image;

import aurora.ide.api.javascript.JavascriptRhino;
import aurora.ide.meta.gef.editors.property.DialogEditableObject;

public class FootRenderer extends AuroraComponent implements
		DialogEditableObject {

	private static final long serialVersionUID = -4223243831892151659L;
	public static final String DISABLE = "disable";
	public static final String PLAIN_TEXT = "text";
	public static final String COLUMNS_SUM = "sum";
	public static final String USER = "user";
	public static final String[] FOOTRENDERER_TYPES = { DISABLE, PLAIN_TEXT,
			COLUMNS_SUM, USER };

	public static String[] function_models = {
			"",
			"function footRenderer(datas,name) {\n\treturn 'text';\n}",
			"function footRenderer(datas,name) {\n\tvar sum = 0;\n\tfor (var i = 0;i < datas.length;i++) {\n\t\tvar d = datas[i].get(name);\n\t\tvar n = parseFloat(d);\n\t\tif (!isNaN(n)) {\n\t\t\tsum += n;\n\t\t}\n\t}\n\treturn Aurora.formatNumber(sum, 2);\n}",
			"function footRenderer(datas,name) {\n\t//TODO\n\treturn someValue;\n}" };
	private String type = FOOTRENDERER_TYPES[0];
	private String function = function_models[0];
	private GridColumn column;

	public FootRenderer() {

	}

	public void setColumn(GridColumn col) {
		column = col;
	}

	public GridColumn getColumn() {
		return column;
	}

	public boolean isEnabled() {
		return !DISABLE.equals(type);
	}

	public String getDescripition() {
		if (!DISABLE.equals(type)) {
			JavascriptRhino js = new JavascriptRhino(function);
			return js.getFirstFunctionName();
		}
		return "";
	}

	public String getFunction() {
		return function;
	}

	public Image getDisplayImage() {
		return null;
	}

	public Object getContextInfo() {
		return column;
	}

	public FootRenderer clone() {
		FootRenderer renderer = new FootRenderer();
		renderer.type = type;
		renderer.function = function;
		renderer.column = column;
		return renderer;
	}

	public void setRendererType(String rendererType) {
		type = rendererType;
	}

	public void setFunction(String tmpFunction) {
		function = tmpFunction;
	}

	public String getRendererType() {
		return type;
	}
}
