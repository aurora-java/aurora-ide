package aurora.ide.meta.gef.editors.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.ContainerHolderEditDialog;
import aurora.ide.meta.gef.editors.property.DialogPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class QueryForm extends BOX implements PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3990396088428828805L;
	public static String DEFAULT_QUERY_FIELD_KEY = "defaultQueryField";
	public static String DEFAULT_QUERY_HINT_KEY = "defaultQueryHint";
	public static String RESULT_TARGET_CONTAINER_KEY = "resultTargetContainer";
	public static String QUERY_HOOK_KEY = "queryHook";
	private QueryFormToolBar toolBar = new QueryFormToolBar();
	// private QueryFormBody body = new QueryFormBody();
	private String defaultQueryField = "";
	private String defaultQueryHint = "";
	private String queryHook = "";
	private ContainerHolder resultTargetContainer = null;
	protected static IPropertyDescriptor PD_QUERY_FIELD = new StringPropertyDescriptor(
			DEFAULT_QUERY_FIELD_KEY, DEFAULT_QUERY_FIELD_KEY);
	protected static IPropertyDescriptor PD_QUERY_HINT = new StringPropertyDescriptor(
			DEFAULT_QUERY_HINT_KEY, DEFAULT_QUERY_HINT_KEY);
	protected static IPropertyDescriptor PD_RESULT_TARGET = new DialogPropertyDescriptor(
			RESULT_TARGET_CONTAINER_KEY, "resultTarget",
			ContainerHolderEditDialog.class);
	private IPropertyDescriptor[] pds = new IPropertyDescriptor[] { PD_PROMPT,
			PD_QUERY_FIELD, PD_QUERY_HINT, PD_RESULT_TARGET, PD_LABELWIDTH };

	public QueryForm() {
		this.setType("queryForm");
		this.setDataset(new QueryDataSet());
		this.setSectionType(BOX.SECTION_TYPE_QUERY);
		setCol(1);
		toolBar.setDataset(getDataset());
		addChild(toolBar);
		// addChild(body);
		resultTargetContainer = new ContainerHolder();
		resultTargetContainer.setOwner(this);
		resultTargetContainer.setContainerType(BOX.SECTION_TYPE_RESULT);
		setSize(new Dimension(600, 400));
		addPropertyChangeListener(this);
	}

	public int getHeadHight() {
		return 0;
	}

	public QueryFormToolBar getToolBar() {
		for (AuroraComponent ac : getChildren()) {
			if (ac instanceof QueryFormToolBar)
				return (QueryFormToolBar) ac;
		}
		return null;
	}

	public QueryFormBody getBody() {
		for (AuroraComponent ac : getChildren()) {
			if (ac instanceof QueryFormBody)
				return (QueryFormBody) ac;
		}
		return null;
	}

	public void setDataset(Dataset ds) {
		super.setDataset(ds);
		if (toolBar != null)
			toolBar.setDataset(ds);
		QueryFormBody body = getBody();
		if (body != null)
			body.setDataset(ds);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (DEFAULT_QUERY_FIELD_KEY.equals(propName))
			return getDefaultQueryField();
		else if (DEFAULT_QUERY_HINT_KEY.equals(propName))
			return getDefaultQueryHint();
		else if (RESULT_TARGET_CONTAINER_KEY.equals(propName))
			return getResultTargetContainer();
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (DEFAULT_QUERY_FIELD_KEY.equals(propName))
			setDefaultQueryField((String) val);
		else if (DEFAULT_QUERY_HINT_KEY.equals(propName))
			setDefaultQueryHint((String) val);
		else if (RESULT_TARGET_CONTAINER_KEY.equals(propName))
			setResultTargetContainer((ContainerHolder) val);
		else
			super.setPropertyValue(propName, val);
	}

	public String getDefaultQueryField() {
		return defaultQueryField;
	}

	public void setDefaultQueryField(String defaultQueryField) {
		this.defaultQueryField = defaultQueryField;
	}

	public String getDefaultQueryHint() {
		return defaultQueryHint;
	}

	public void setDefaultQueryHint(String defaultQueryHint) {
		String old = this.defaultQueryField;
		this.defaultQueryHint = defaultQueryHint;
		toolBar.firePropertyChange(DEFAULT_QUERY_HINT_KEY, old,
				defaultQueryHint);
	}

	public ContainerHolder getResultTargetContainer() {
		return resultTargetContainer;
	}

	public void setResultTargetContainer(ContainerHolder resultTargetContainer) {
		this.resultTargetContainer = resultTargetContainer;
		resultTargetContainer.setOwner(this);
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		if (component instanceof QueryFormToolBar)
			return getToolBar() == null;
		if (component instanceof QueryFormBody)
			return getBody() == null;
		return false;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(CHILDREN)) {
			Object newVal = evt.getNewValue();
			if (newVal instanceof QueryFormBody) {
				toolBar.setHasMore(getChildren().contains(newVal));
			}
		}
	}
}
