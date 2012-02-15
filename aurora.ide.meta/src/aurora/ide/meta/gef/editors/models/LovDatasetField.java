package aurora.ide.meta.gef.editors.models;

import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class LovDatasetField extends DatasetField {
	/**
	 * 
	 */
	// check box
	// checkedValue="Y" defaultValue="Y"
	// lov
	// mapping = lov service:=
	private static final long serialVersionUID = -4619018857153616914L;

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new IntegerPropertyDescriptor(LOV_GRID_HEIGHT, "lovGridHeight"),
			new IntegerPropertyDescriptor(LOV_HEIGHT, "lovHeight"),
			new StringPropertyDescriptor(LOV_SERVICE, "lovService"),
			new StringPropertyDescriptor(LOV_URL, "lovUrl"),
			new IntegerPropertyDescriptor(LOV_WIDTH, "lovWidth"),
			new StringPropertyDescriptor(TITLE, "title") };

	private int lovGridHeight;
	private int lovHeight;
	private String lovService = "";
	private String lovUrl = "";
	private int lovWidth;
	private String title = "";

	public LovDatasetField() {
		this.setType("field");
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// IPropertyDescriptor[] propertyDescriptors = super
		// .getPropertyDescriptors();
		// return this.mergePropertyDescriptor(propertyDescriptors, pds);
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (LOV_GRID_HEIGHT.equals(propName)) {
			return this.getLovGridHeight();
		}
		if (LOV_HEIGHT.equals(propName)) {
			return this.getLovHeight();
		}
		if (LOV_SERVICE.equals(propName)) {
			return this.getLovService();
		}
		if (LOV_URL.equals(propName)) {
			return this.getLovUrl();
		}
		if (LOV_WIDTH.equals(propName)) {
			return this.getLovWidth();
		}
		if (TITLE.equals(propName)) {
			return this.getTitle();
		}

		return super.getPropertyValue(propName);
	}

	public void setPropertyValue(Object propName, Object val) {
		if (LOV_GRID_HEIGHT.equals(propName)) {
			setLovGridHeight((Integer) val);
		}
		if (LOV_HEIGHT.equals(propName)) {
			setLovHeight((Integer) val);
		}
		if (LOV_SERVICE.equals(propName)) {
			setLovService((String) val);
		}
		if (LOV_URL.equals(propName)) {
			setLovUrl((String) val);
		}
		if (LOV_WIDTH.equals(propName)) {
			setLovWidth((Integer) val);
		}
		if (TITLE.equals(propName)) {
			setTitle((String) val);
		}
		super.setPropertyValue(propName, val);
	}

	public int getLovGridHeight() {
		return lovGridHeight;
	}

	public void setLovGridHeight(int lovGridHeight) {
		this.lovGridHeight = lovGridHeight;
	}

	public int getLovHeight() {
		return lovHeight;
	}

	public void setLovHeight(int lovHeight) {
		this.lovHeight = lovHeight;
	}

	public String getLovService() {
		return lovService;
	}

	public void setLovService(String lovService) {
		this.lovService = lovService;
	}

	public String getLovUrl() {
		return lovUrl;
	}

	public void setLovUrl(String lovUrl) {
		this.lovUrl = lovUrl;
	}

	public int getLovWidth() {
		return lovWidth;
	}

	public void setLovWidth(int lovWidth) {
		this.lovWidth = lovWidth;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
