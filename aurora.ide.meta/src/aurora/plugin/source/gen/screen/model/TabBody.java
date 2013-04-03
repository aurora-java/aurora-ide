package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;


public class TabBody extends Container {

	public static final String VISIBLE = "visible";
	public static String TAB_BODY="tabbody";
	@SuppressWarnings("rawtypes")
	private static Class[] unsupported = { Toolbar.class, Navbar.class,
			GridColumn.class, TabItem.class, TabBody.class };
//	private boolean visible = false;
	private TabItem tabItem;

	public TabBody(){
		this.setComponentType(TAB_BODY);
		this.setVisible(false);
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		Class cls = component.getClass();
		for (Class c : unsupported)
			if (c.equals(cls))
				return false;
		return super.isResponsibleChild(component);
	}

	public void setVisible(boolean v) {
//		if (visible == v) {
//			return;
//		}
//		boolean oldV = visible;
//		visible = v;
//		firePropertyChange(VISIBLE, oldV, v);
		this.setPropertyValue(ComponentInnerProperties.TABBODY_VISIBLE, v);
	}

	public boolean getVisible() {
//		return visible;
		return this.getBooleanPropertyValue(ComponentInnerProperties.TABBODY_VISIBLE);
	}


	public TabItem getTabItem() {
		return tabItem;
	}

	public void setTabItem(TabItem tabItem) {
		this.tabItem = tabItem;
	}

}
