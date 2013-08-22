package aurora.ide.meta.gef.editors.consultant.property;

import java.util.Map;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class FSDPropertyWizard extends Wizard {

	private FSDPropertyPage page1;
	private Shell shell;
	private Map<IPropertyDescriptor, String> values;
	
	public FSDPropertyWizard(Shell shell, Map<IPropertyDescriptor, String> values) {
		this.shell = shell;
		this.values = values;
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page1 = new FSDPropertyPage("FunctionDescPage", "FSD文档属性", null, values);
		addPage(page1);
	}

	public Map<IPropertyDescriptor, String> getValues(){
		return page1.getValues();
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}

	public int open() {
		WizardDialog wd = new WizardDialog(shell, this);
		return wd.open();
	}
}
