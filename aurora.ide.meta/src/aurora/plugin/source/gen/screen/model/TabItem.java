package aurora.plugin.source.gen.screen.model;

import java.util.List;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;

//import org.eclipse.draw2d.geometry.Dimension;
//import org.eclipse.ui.views.properties.IPropertyDescriptor;
//
//import aurora.ide.meta.gef.editors.models.link.DeadTabRef;
//import aurora.ide.meta.gef.editors.models.link.Parameter;
//import aurora.ide.meta.gef.editors.models.link.TabRef;
//import aurora.ide.meta.gef.editors.property.TabRefPropertyDescriptor;

public class TabItem extends Container {
	public static final String TAB = "tab";
	public static final String SCREEN_REF = "ref";
	// private static IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
	// PD_PROMPT, PD_WIDTH,
	// new TabRefPropertyDescriptor(SCREEN_REF, "ref") };
	public static final String CURRENT = "current";
	public static final int HEIGHT = 25;
	private TabBody body = new TabBody();
	static int idx = 0;
	boolean current = false;

	// private TabRef ref;

	public TabItem() {
		setWidth(65);
		this.setComponentType(TAB);
		setPrompt("tabItem" + idx++);
		body.setTabItem(this);
	}

	// public void setSize(Dimension dim) {
	// dim.height = HEIGHT;
	// super.setSize(dim);
	// }

	public void setWidth(int width) {
		super.setSize(width, HEIGHT);
	}

	public int getWidth() {
		// return super.getSize().width;
		return this.getIntegerPropertyValue(ComponentProperties.width);
	}

	public TabBody getBody() {
		return body;
	}

	@Override
	public void addChild(AuroraComponent child) {
		body.addChild(child);
	}

	@Override
	public void addChild(AuroraComponent child, int index) {
		body.addChild(child, index);
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean b) {
		if (current == b)
			return;
		if (b) {
			TabFolder tf = (TabFolder) getParent();
			if (tf != null)
				tf.disSelectAll();
		}
		getBody().setVisible(b);
		boolean oldV = current;
		current = b;
		firePropertyChange(CURRENT, oldV, b);
	}


	// public Object getPropertyValue(Object propName) {
	// if (SCREEN_REF.equals(propName)) {
	// TabRef tabRef = new TabRef();
	// tabRef.setTabItem(this);
	// if (ref != null) {
	// tabRef.setOpenPath(ref.getOpenPath());
	// for (Parameter p : ref.getParameters()) {
	// tabRef.addParameter(p.clone());
	// }
	// tabRef.setModelQuery(ref.getModelQuery());
	// }
	// return tabRef;
	// }
	// return super.getPropertyValue(propName);
	// }

	// public void setPropertyValue(Object propName, Object val) {
	//
	// if (SCREEN_REF.equals(propName) && val instanceof TabRef) {
	// if (val instanceof DeadTabRef) {
	// ref = null;
	// } else {
	// ref = (TabRef) val;
	// }
	// firePropertyChange(SCREEN_REF, null, val);
	// body.firePropertyChange(SCREEN_REF, null, val);
	// }
	// super.setPropertyValue(propName, val);
	// }

	// public TabRef getTabRef() {
	// return ref;
	// }

	// public void setTabRef(TabRef tr) {
	// this.ref = tr;
	// body.firePropertyChange(SCREEN_REF, null, tr);
	// }

	// / make tabitem a proxy of tabbody
	@Override
	public List<AuroraComponent> getChildren() {
		return body.getChildren();
	}

	@Override
	public void removeChild(AuroraComponent child) {
		body.removeChild(child);
	}

	@Override
	public void removeChild(int idx) {
		body.removeChild(idx);
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		return false;
	}

	@Override
	public Object getPropertyValue(String propId) {
		if (ComponentInnerProperties.TAB_ITEM_CURRENT.equals(propId)) {
			return this.isCurrent();
		}
		return super.getPropertyValue(propId);
	}

	@Override
	public void setPropertyValue(String propId, Object val) {
		if (ComponentInnerProperties.TAB_ITEM_CURRENT.equals(propId)
				&& val instanceof Boolean) {
			this.setCurrent((Boolean) val);
			return;
		}
		super.setPropertyValue(propId, val);
	}

}
