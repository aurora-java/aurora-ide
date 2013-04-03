package aurora.plugin.source.gen.screen.model;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.property.IDialogEditableObject;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;


public class Renderer extends AuroraComponent implements IDialogEditableObject { 

	public static final String NONE_RENDERER = "NONE_RENDERER"; //$NON-NLS-1$
	public static final String PAGE_REDIRECT = "PAGE_REDIRECT"; //$NON-NLS-1$
	public static final String INNER_FUNCTION = "INNER_FUNCTION"; //$NON-NLS-1$
	public static final String USER_FUNCTION = "USER_FUNCTION"; //$NON-NLS-1$
	public static final String[] RENDERER_TYPES = { NONE_RENDERER,
			PAGE_REDIRECT, INNER_FUNCTION, USER_FUNCTION };

	// PAGE_REDIRECT
//	private String openPath = ""; //$NON-NLS-1$
//	private String labelText = ""; //$NON-NLS-1$
	private List<Parameter> paras = new ArrayList<Parameter>();

	/**
	 */
//	private String functionName = ""; //$NON-NLS-1$

	/**
	 */
	public static final String[] INNER_FUNCTIONS = { "Aurora.formatDate", //$NON-NLS-1$
			"Aurora.formatDateTime", "Aurora.formatNumber" }; //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 */
//	public static final String[] INNER_RENDERER_DESC = { Messages.Renderer_10,
//			Messages.Renderer_11, Messages.Renderer_12 };

	/**
	 */
//	private String function = ""; //$NON-NLS-1$

	/**
	 */
	public static final String FUNCTION_MODEL = "function myRenderer(value,record,name){\n\treturn 'rendererText';\n}"; //$NON-NLS-1$

	private GridColumn column;
//	private String rendererType = NONE_RENDERER;

	public Renderer() {
		this.setComponentType("renderer");
		this.setRendererType(NONE_RENDERER);
	}

	public void setColumn(GridColumn col) {
		column = col;
	}

	public GridColumn getColumn() {
		return column;
	}

//	public String getDescripition() {
//		if (PAGE_REDIRECT.equals(rendererType))
//			return labelText;
//		else if (INNER_FUNCTION.equals(rendererType))
//			return functionName;
//		else if (USER_FUNCTION.equals(rendererType)) {
//			JavascriptRhino js = new JavascriptRhino(function);
//			return "[ " + js.getFirstFunctionName() + " ]"; //$NON-NLS-1$ //$NON-NLS-2$
//		}
//		return ""; //$NON-NLS-1$
//	}

	public Object getContextInfo() {
		return column;
	}

	public Renderer clone() {
		Renderer r = new Renderer();
//		r.openPath = openPath;
		r.setOpenPath(this.getOpenPath());
		r.paras = new ArrayList<Parameter>();
		for (Parameter p : paras) {
			r.paras.add(p.clone());
		}
		r.column = column;
//		r.labelText = labelText;
		r.setLabelText(this.getLabelText());
//		r.function = function;
		r.setFunction(this.getFunction());
//		r.functionName = functionName;
		r.setFunctionName(this.getFunctionName());
//		r.rendererType = rendererType;
		r.setRendererType(this.getRendererType());
		return r;
	}

	public String getOpenPath() {
//		return openPath;
		return this.getStringPropertyValue(ComponentInnerProperties.RENDERER_OPEN_PATH);
	}

	public void setOpenPath(String openPath) {
//		this.openPath = openPath;
		this.setPropertyValue(ComponentInnerProperties.RENDERER_OPEN_PATH, openPath);
	}

	public String getLabelText() {
//		return labelText;
		return this.getStringPropertyValue(ComponentInnerProperties.RENDERER_LABELTEXT);
	}

	public void setLabelText(String tmpLabelText) {
//		this.labelText = tmpLabelText;
		this.setPropertyValue(ComponentInnerProperties.RENDERER_LABELTEXT, tmpLabelText);
	}

//	public Image getDisplayImage() {
//		return null;
//	}

	public String getFunctionName() {
//		return functionName;
		return this.getStringPropertyValue(ComponentInnerProperties.RENDERER_FUNCTION_NAME);
		
	}

	public void setFunctionName(String functionName) {
//		this.functionName = functionName;
		this.setPropertyValue(ComponentInnerProperties.RENDERER_FUNCTION_NAME, functionName);
	}

	public String getFunction() {
//		return function;
		return this.getStringPropertyValue(ComponentInnerProperties.RENDERER_FUNCTION);
	}

	public void setFunction(String function) {
//		this.function = function;
		this.setPropertyValue(ComponentInnerProperties.RENDERER_FUNCTION, function);
	}

	public String getRendererType() {
//		return rendererType;
		return this.getStringPropertyValue(ComponentInnerProperties.RENDERER_TYPE);
	}

	public void setRendererType(String rendererType) {
//		this.rendererType = rendererType;
		this.setPropertyValue(ComponentInnerProperties.RENDERER_TYPE, rendererType);
	}


	public List<Parameter> getParameters() {
		return paras;
	}

	public void addParameter(Parameter para) {
		paras.add(para);
	}

	@Override
	public Object getPropertyValue(String propId) {
		if (ComponentInnerProperties.RENDERER_PARAMETERS.equals(propId)) {
			return paras; 
		}
		Object propertyValue = super.getPropertyValue(propId);
		return propertyValue;
	}

	public void setPropertyValue(String propId, Object val) {
		if (ComponentInnerProperties.RENDERER_PARAMETERS.equals(propId)
				&& val instanceof List) {
			paras = (List<Parameter>) val;
			return;
		}
		super.setPropertyValue(propId, val);
	}

	public String getDescripition() {
		//TODO
		return "description";
	}
}
