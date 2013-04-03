package aurora.plugin.source.gen.screen.model;

import aurora.ide.meta.gef.editors.property.IDialogEditableObject;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;


public class FootRenderer extends AuroraComponent implements IDialogEditableObject
		 {

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
//	private String type = FOOTRENDERER_TYPES[0];
//	private String function = function_models[0];
	private GridColumn column;

	public FootRenderer() {
		this.setRendererType(FOOTRENDERER_TYPES[0]);
		this.setFunction(function_models[0]);
		setComponentType("footrenderer");
	}

	public void setColumn(GridColumn col) {
		column = col;
	}

	public GridColumn getColumn() {
		return column;
	}

	public boolean isEnabled() {
//		return !DISABLE.equals(type);
		return !DISABLE.equals(this.getRendererType());
	}

	public String getDescripition() {
//		if (!DISABLE.equals(type)) {
//			JavascriptRhino js = new JavascriptRhino(function);
//			return js.getFirstFunctionName();
//		}
		return "Descripition";
	}

	public String getFunction() {
//		return function;
		return this.getStringPropertyValue(ComponentInnerProperties.FOOT_RENDERER_FUNCTION);
	}

//	public Image getDisplayImage() {
//		return null;
//	}

	public Object getContextInfo() {
		return column;
	}

	public FootRenderer clone() {
		FootRenderer r = new FootRenderer();
//		renderer.type = type;
		r.setRendererType(this.getRendererType());
//		renderer.function = function;
		r.setFunction(this.getFunction());
		r.column = column;
		return r;
	}

	public void setRendererType(String rendererType) {
//		type = rendererType;
		this.setPropertyValue(ComponentInnerProperties.FOOT_RENDERER_TYPE, rendererType);
	}

	public void setFunction(String tmpFunction) {
//		function = tmpFunction;
		
		this.setPropertyValue(ComponentInnerProperties.FOOT_RENDERER_FUNCTION, tmpFunction);
	}

	public String getRendererType() {
//		return type;
		return this.getStringPropertyValue(ComponentInnerProperties.FOOT_RENDERER_TYPE);
	}

}
