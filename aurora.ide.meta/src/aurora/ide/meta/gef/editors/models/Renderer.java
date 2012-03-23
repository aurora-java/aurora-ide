package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import aurora.ide.api.javascript.JavascriptRhino;
import aurora.ide.meta.gef.editors.models.link.Parameter;
import aurora.ide.meta.gef.editors.property.DialogEditableObject;

public class Renderer extends AuroraComponent implements DialogEditableObject {

	private static final long serialVersionUID = -3218999047690358423L;
	public static final String NONE_RENDERER = "NONE_RENDERER"; //$NON-NLS-1$
	public static final String PAGE_REDIRECT = "PAGE_REDIRECT"; //$NON-NLS-1$
	public static final String INNER_FUNCTION = "INNER_FUNCTION"; //$NON-NLS-1$
	public static final String USER_FUNCTION = "USER_FUNCTION"; //$NON-NLS-1$
	public static final String[] RENDERER_TYPES = { NONE_RENDERER,
			PAGE_REDIRECT, INNER_FUNCTION, USER_FUNCTION };

	// PAGE_REDIRECT
	private String openPath = ""; //$NON-NLS-1$
	private String labelText = ""; //$NON-NLS-1$
	private List<Parameter> paras = new ArrayList<Parameter>();

	/**
	 */
	private String functionName = ""; //$NON-NLS-1$

	/**
	 */
	public static final String[] INNER_FUNCTIONS = { "Aurora.formatDate", //$NON-NLS-1$
			"Aurora.formatDateTime", "Aurora.formatNumber" }; //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 */
	public static final String[] INNER_RENDERER_DESC = { Messages.Renderer_10,
			Messages.Renderer_11, Messages.Renderer_12 };

	/**
	 */
	private String function = ""; //$NON-NLS-1$

	/**
	 */
	public static final String FUNCTION_MODEL = "function myRenderer(value,record,name){\n\treturn 'rendererText';\n}"; //$NON-NLS-1$

	private GridColumn column;
	private String rendererType = NONE_RENDERER;

	public Renderer() {

	}

	public void setColumn(GridColumn col) {
		column = col;
	}

	public GridColumn getColumn() {
		return column;
	}

	public String getDescripition() {
		if (PAGE_REDIRECT.equals(rendererType))
			return labelText;
		else if (INNER_FUNCTION.equals(rendererType))
			return functionName;
		else if (USER_FUNCTION.equals(rendererType)) {
			JavascriptRhino js = new JavascriptRhino(function);
			return "[ " + js.getFirstFunctionName() + " ]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return ""; //$NON-NLS-1$
	}

	public Object getContextInfo() {
		return column;
	}

	public Renderer clone() {
		Renderer r = new Renderer();
		r.openPath = openPath;
		r.paras = new ArrayList<Parameter>();
		for (Parameter p : paras) {
			r.paras.add(p.clone());
		}
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

	@Override
	public Object getEditableValue() {
		return null;
	}

	public List<Parameter> getParameters() {
		return paras;
	}

	public void addParameter(Parameter para) {
		paras.add(para);
	}

}
