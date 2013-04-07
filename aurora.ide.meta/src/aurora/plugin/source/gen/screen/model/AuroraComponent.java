package aurora.plugin.source.gen.screen.model;

import java.util.HashMap;
import java.util.Map;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;
import aurora.plugin.source.gen.screen.model.properties.PropertyEditSupport;

abstract public class AuroraComponent extends PropertyEditSupport implements
		Cloneable {

	

	// private String componentType = "";

	private Map<Object, Object> simpleProperties = new HashMap<Object, Object>();

	protected Map<Object, Object> getSimpleProperties() {
		return simpleProperties;
	}

	protected void setSimpleProperties(Map<Object, Object> simpleProperties) {
		this.simpleProperties = simpleProperties;
	}

	private Container parent;

	private Rectangle bounds = Rectangle.NONE();

	{
		this.setPropertyValue(ComponentInnerProperties.COMPONENT_MARKER_ID,
				markid);
	}

	protected boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	public int getIntegerPropertyValue(String propId) {
		Object x = this.getPropertyValue(propId);
		return x instanceof Integer ? (Integer) x : -1;
	}

	public Boolean getBooleanPropertyValue(String propId) {
		Object x = this.getPropertyValue(propId);
		return x instanceof Boolean ? (Boolean) x : false;
	}

	public String getStringPropertyValue(String propId) {
		Object x = this.getPropertyValue(propId);
		return x instanceof String ? x.toString() : "";
	}

	public AuroraComponent getAuroraComponentPropertyValue(String propId) {
		Object x = this.getPropertyValue(propId);
		return x instanceof AuroraComponent ? (AuroraComponent) x : null;
	}

	public Object getPropertyValue(String propId) {
		if (ComponentInnerProperties.LOCATION_X.equals(propId)) {
			return bounds.x;
		}
		if (ComponentInnerProperties.LOCATION_Y.equals(propId)) {
			return bounds.y;
		}
		if (ComponentProperties.width.equals(propId)) {
			return bounds.width;
		}
		if (ComponentProperties.height.equals(propId)) {
			return bounds.height;
		}

		return simpleProperties.get(propId);
	}

	public void setPropertyValue(String propId, Object val) {
		if (ComponentInnerProperties.LOCATION_X.equals(propId)
				&& val instanceof Integer) {
			bounds.x = (Integer) val;
			this.setBounds(bounds);
			return;
		}
		if (ComponentInnerProperties.LOCATION_Y.equals(propId)
				&& val instanceof Integer) {
			bounds.y = (Integer) val;
			this.setBounds(bounds);
			return;
		}
		if (ComponentProperties.width.equals(propId) && val instanceof Integer) {
//			bounds.width = (Integer) val;
//			this.setBounds(bounds);
			this.setSize((Integer) val, bounds.height);
			return;
		}
		if (ComponentProperties.height.equals(propId) && val instanceof Integer) {
//			bounds.height = (Integer) val;
//			this.setBounds(bounds);
			this.setSize(bounds.width,(Integer) val);
			return;
		}
		// ComponentInnerProperties.BOUNDS

		Object oldVal = simpleProperties.get(propId);
		if (eq(oldVal, val)) {
			return;
		}
		simpleProperties.put(propId, val);
		firePropertyChange(propId, oldVal, val);
	}

	public Container getParent() {
		return parent;
	}

	public void setParent(Container parent) {
		this.parent = parent;
	}

	public String getComponentType() {
		// return componentType;
		return this
				.getStringPropertyValue(ComponentInnerProperties.COMPONENT_TYPE);
	}

	public void setComponentType(String componentType) {
		// if (eq(this.componentType, componentType)) {
		// return;
		// }
		// String old = this.componentType;
		// this.componentType = componentType;
		// firePropertyChange(ComponentInnerProperties.COMPONENT_TYPE, old,
		// componentType);
		this.setPropertyValue(ComponentInnerProperties.COMPONENT_TYPE,
				componentType);
	}

	public void setPrompt(String prompt) {
		this.setPropertyValue(ComponentProperties.prompt, prompt);
	}

	public void setSize(int width, int height) {
		// this.setPropertyValue(ComponentProperties.width, width);
		// this.setPropertyValue(ComponentProperties.height, height);
		Point old = new Point(bounds.width, bounds.height);
		bounds.width = width;
		bounds.height = height;
		this.firePropertyChange(ComponentInnerProperties.SIZE, old,
				bounds.getSize());
	}

	public Point getSize() {
		// Object x = this.getPropertyValue(ComponentProperties.width);
		// Object y = this.getPropertyValue(ComponentProperties.height);
		// return new Point(x instanceof Integer ? (Integer) x : -1,
		// y instanceof Integer ? (Integer) y : -1);
		return bounds.getSize();
	}

	public void setLocation(Point p) {
		// this.setPropertyValue(ComponentInnerProperties.LOCATION, p);
		bounds.setLocation(p);
		this.firePropertyChange(ComponentInnerProperties.LOCATION, p,
				bounds.getLocation());
	}

	public Point getLocation() {
		// Object l = this.getPropertyValue(ComponentInnerProperties.LOCATION);
		// if (l instanceof Point) {
		// return new Point((Point) l);
		// } else
		// return Point.NONE;
		return bounds.getLocation();
	}

	public String getName() {
		return "" + this.getPropertyValue(ComponentProperties.name);
	}

	public String getPrompt() {
		return "" + this.getPropertyValue(ComponentProperties.prompt);
	}

	public void setName(String name) {
		this.setPropertyValue(ComponentProperties.name, name);
	}

	public void applyToModel(Rectangle bounds) {
		this.bounds = bounds.getCopy();
	}

	public Rectangle getBoundsCopy() {
		return bounds.getCopy();
	}

	public void setBounds(aurora.plugin.source.gen.screen.model.Rectangle aurora) {
		bounds = aurora.getCopy();
		this.firePropertyChange(ComponentInnerProperties.BOUNDS, aurora, bounds);
	}

}
