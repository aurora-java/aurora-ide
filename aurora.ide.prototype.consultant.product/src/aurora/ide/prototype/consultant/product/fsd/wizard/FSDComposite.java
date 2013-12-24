package aurora.ide.prototype.consultant.product.fsd.wizard;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import aurora.ide.swt.util.PageModel;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.WidgetFactory;

public class FSDComposite {
	
	private PageModel model;
	
	public FSDComposite (PageModel model){
		this.model = model;
	}
	
	protected TextField createInputField(Composite parent, String label,
			String key) {
		TextField createTextField = WidgetFactory
				.createTextField(parent, label);
		createTextField.setText(getModel().getStringPropertyValue(key));
		createTextField.addModifyListener(new TextModifyListener(key,
				createTextField.getText()));
		return createTextField;
	}

	public PageModel getModel() {
		return model;
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
	public void updateModel(String key, String text) {
		getModel().setPropertyValue(key, text);
	}
}
