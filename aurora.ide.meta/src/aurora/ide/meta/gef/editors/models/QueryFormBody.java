package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class QueryFormBody extends BOX {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2821328187635006644L;
	private IPropertyDescriptor[] pds = { PD_COL, PD_LABELWIDTH };

	public QueryFormBody() {
		super();
		setType("formBody");
		setCol(1);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		if (component instanceof QueryFormToolBar)
			return false;
		if (component instanceof QueryFormBody)
			return false;
		return true;
	}

}
