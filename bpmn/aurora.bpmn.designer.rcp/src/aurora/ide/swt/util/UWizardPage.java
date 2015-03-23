package aurora.ide.swt.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

abstract public class UWizardPage extends WizardPage {

	private PageModel model = new PageModel();
	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			modelChanged(evt.getPropertyName(), evt.getNewValue());
		}
	};

	public UWizardPage(String pageName) {
		super(pageName);
		init();
	}

	abstract protected String[] getModelPropertyKeys();

	protected void modelChanged(String key, Object value) {
		modelChanged();
	}

	protected void modelChanged() {
		String[] keys = getModelPropertyKeys();
		if (keys != null) {
			for (String k : keys) {
				Object val = model.getPropertyValue(k);
				String errMSG = verifyModelProperty(k, val);
				if (verifyPage(errMSG) == false) {
					break;
				}
			}
		}
	}

	protected abstract String verifyModelProperty(String key, Object val);

	public boolean verifyPage(String message) {
		this.setErrorMessage(message);
		this.setPageComplete(message == null);
		return message == null;
	}

	protected void updateModel(String key, Object value) {
		this.model.setPropertyValue(key, value);
	}

	protected void init() {
		model.addPropertyChangeListener(propertyChangeListener);
	}

	public void createControl(Composite parent) {
		Composite c = createPageControl(parent);
		this.setControl(c);
		modelChanged();
	}

	abstract protected Composite createPageControl(Composite control);

	public PageModel getModel() {
		return model;
	}

	public void setModel(PageModel model) {
		this.model = model;
	}

	protected class TextModifyListener implements ModifyListener {

		private String key;
		private Text text;

		public TextModifyListener(String key, Text text) {
			this.key = key;
			this.text = text;
		}

		public void modifyText(ModifyEvent e) {
			updateModel(key, text.getText());
		}
	}

	protected TextField createInputField(Composite parent, String label,
			String key) {
		TextField createTextField = WidgetFactory
				.createTextField(parent, label);
		createTextField.addModifyListener(new TextModifyListener(key,
				createTextField.getText()));
		return createTextField;
	}
}
