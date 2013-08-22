package aurora.ide.meta.gef.editors.consultant.property;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class FSDPropertyPage extends WizardPage {


	private Map<IPropertyDescriptor, String> values = new HashMap<IPropertyDescriptor, String>();

	protected FSDPropertyPage(String pageName, String title,
			ImageDescriptor titleImage,  Map<IPropertyDescriptor, String> values) {
		super(pageName, title, titleImage);
		this.setValues(values);
	}

	public void createControl(Composite root) {
		Composite parent = new Composite(root, SWT.NONE);
		parent.setLayout(new GridLayout());
		Set<IPropertyDescriptor> pds = getValues().keySet();
		for (IPropertyDescriptor pd : pds) {
			CellEditor ce = pd.createPropertyEditor(parent);
			ce.create(parent);
			Control control = ce.getControl();
			if (control instanceof Text) {
				((Text) control).setText(getValues().get(pd));
			}
			TypedListener typedListener = new TypedListener(new Listener(pd));
			control.addListener(SWT.Modify, typedListener);
		}

		this.setControl(parent);
	}

	public Map<IPropertyDescriptor, String> getValues() {
		return values;
	}

	public void setValues(Map<IPropertyDescriptor, String> values) {
		this.values = values;
	}

	private class Listener implements ModifyListener {

		private IPropertyDescriptor pd;

		private Listener(IPropertyDescriptor pd) {
			this.pd = pd;
		}

		public void modifyText(ModifyEvent e) {
			Object source = e.getSource();
			if (source instanceof Text) {
				String text = ((Text) source).getText();
				getValues().put(pd, text);
			}
		}
	}

}
