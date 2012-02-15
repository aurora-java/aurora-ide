package aurora.ide.meta.gef.editors.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.IPropertySource2;
import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class AuroraComponent implements Cloneable, Serializable, IProperties,
		IPropertySource2 {

	/**
	 * 此id仅用于存储xml时标记引用关系,以便读取时根据引用关系重新建立引用关系
	 */
	public transient String markid = Integer.toHexString(hashCode());
	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);
	static final long serialVersionUID = 1;

	private Rectangle bounds = new Rectangle();

	private String name = "";

	private String type = "";

	private String prompt = "prompt";

	// private String bindTarget = "";

	// private Dataset bindTarget;

	private Container parent;

	protected static final IPropertyDescriptor PD_PROMPT = new StringPropertyDescriptor(
			PROMPT, "Prompt");
	protected static final IPropertyDescriptor PD_WIDTH = new IntegerPropertyDescriptor(
			WIDTH, "Width");
	protected static final IPropertyDescriptor PD_HEIGHT = new IntegerPropertyDescriptor(
			HEIGHT, "Height");
	protected static final IPropertyDescriptor PD_NAME = new StringPropertyDescriptor(
			NAME, "Name");
	private static final IPropertyDescriptor[] pds = { PD_PROMPT };
	protected static final IPropertyDescriptor[] NONE_PROPS = new IPropertyDescriptor[0];

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	protected void fireStructureChange(String prop, Object child) {
		listeners.firePropertyChange(prop, null, child);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		listeners = new PropertyChangeSupport(this);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}

	public void setLocation(Point p) {
		Point oldLoc = getLocation();
		if (eq(oldLoc, p)) {
			return;
		}
		bounds.setLocation(p);
		firePropertyChange(LOCATION, oldLoc, p);
	}

	public Point getLocation() {
		return bounds.getLocation();
	}

	public Dimension getSize() {
		return bounds.getSize();
	}

	public void setSize(Dimension size) {
		Dimension oldSize = getSize();
		if (eq(oldSize, size)) {
			return;
		}
		this.bounds.setSize(size);
		firePropertyChange(SIZE, oldSize, size);
	}

	public Rectangle getBounds() {
		return bounds.getCopy();
	}

	public void setBounds(Rectangle bounds) {
		if (eq(this.bounds, bounds)) {
			return;
		}
		Rectangle old = this.bounds;
		this.bounds = bounds;
		firePropertyChange(BOUNDS, old, bounds);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (eq(this.name, name)) {
			return;
		}
		String old = this.name;
		this.name = name;
		firePropertyChange(NAME, old, name);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (eq(this.type, type)) {
			return;
		}
		String old = this.type;
		this.type = type;
		firePropertyChange(TYPE, old, type);
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		if (eq(this.prompt, prompt)) {
			return;
		}
		String old = this.prompt;
		this.prompt = prompt;
		firePropertyChange(PROMPT, old, prompt);
	}

	// public Dataset getBindTarget() {
	// return bindTarget;
	// }
	//
	// public void setBindTarget(Dataset bindTarget) {
	// this.bindTarget = bindTarget;
	// bindTarget.addBind(this);
	// }
	//
	// public void removeBindTarget() {
	// bindTarget.removeBind(this);
	// this.bindTarget = null;
	// }

	protected boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	public Object getPropertyValue(Object propName) {
		if (PROMPT.equals(propName))
			return this.getPrompt();
		else if (WIDTH.equals(propName))
			return getSize().width;
		else if (HEIGHT.equals(propName))
			return getSize().height;
		else if (NAME.equals(propName))
			return getName();
		return null;
	}

	public boolean isPropertySet(Object propName) {
		if (PROMPT.equals(propName))
			return true;
		else if (WIDTH.equals(propName))
			return true;
		else if (HEIGHT.equals(propName))
			return true;
		else if (NAME.equals(propName))
			return true;
		return false;
	}

	public void resetPropertyValue(Object propName) {
	}

	public void setPropertyValue(Object propName, Object val) {

		if (PROMPT.equals(propName))
			this.setPrompt((String) val);
		else if (WIDTH.equals(propName))
			setSize(new Dimension((Integer) val, getSize().height));
		else if (HEIGHT.equals(propName))
			setSize(new Dimension(getSize().width, (Integer) val));
		else if (NAME.equals(propName))
			setName((String) val);
	}

	public Container getParent() {
		return parent;
	}

	public void setParent(Container parent) {
		this.parent = parent;
	}

	// public void setIBounds(Rectangle layout) {
	// this.bounds = layout;
	//
	// }
	//
	// public void setILocation(Point location) {
	// this.location = location;
	// }
	public IPropertyDescriptor[] mergePropertyDescriptor(
			IPropertyDescriptor[] pd1, IPropertyDescriptor[] pd2) {
		IPropertyDescriptor[] descs = new IPropertyDescriptor[pd1.length
				+ pd2.length];
		System.arraycopy(pd1, 0, descs, 0, pd1.length);
		System.arraycopy(pd2, 0, descs, pd1.length, pd2.length);
		return descs;
	}

}
