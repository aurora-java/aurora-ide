package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class TabItem extends AuroraComponent {
	private static final long serialVersionUID = -6198220551287976461L;
	private static IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT, PD_WIDTH };
	public static final String CURRENT = "current";
	public static final int HEIGHT = 25;
	private TabBody body = new TabBody();
	static int idx = 0;
	boolean current = false;
	private String templateRef = "";

	public TabItem() {
		setWidth(65);
		this.setType("tab");
		setPrompt("tabItem" + idx++);
		body.setTabItem(this);
	}

	public void setSize(Dimension dim) {
		dim.height = HEIGHT;
		super.setSize(dim);
	}

	public void setWidth(int width) {
		super.setSize(new Dimension(width, HEIGHT));
	}

	public int getWidth() {
		return super.getSize().width;
	}

	public TabBody getBody() {
		return body;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean b) {
		if (current == b)
			return;
		getBody().setVisible(b);
		boolean oldV = current;
		current = b;
		firePropertyChange(CURRENT, oldV, b);
	}

	@Override
	public Object getEditableValue() {
		return super.getEditableValue();
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		return super.getPropertyValue(propName);
	}

	@Override
	public boolean isPropertySet(Object propName) {
		return true;
	}

	@Override
	public void resetPropertyValue(Object propName) {
		super.resetPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		super.setPropertyValue(propName, val);
	}

	public String getTemplateRef() {
		return templateRef;
	}

	public void setTemplateRef(String templateRef) {
		this.templateRef = templateRef;
		// TODO 显示模板预览图
	}

}
