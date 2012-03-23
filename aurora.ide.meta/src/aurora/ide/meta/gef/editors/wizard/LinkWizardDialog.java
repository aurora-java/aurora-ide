package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class LinkWizardDialog extends WizardDialog {

	private CreateEditLinkWizard wizard;

	public LinkWizardDialog(Shell parentShell, CreateEditLinkWizard wizard) {
		super(parentShell, wizard);
		this.wizard = wizard;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if(wizard.isNeedDelete()){
			((GridLayout) parent.getLayout()).numColumns++;
			Button del = new Button(parent,SWT.NONE);
			del.setText("   Delete   ");
			del.addSelectionListener(new SelectionListener(){

				public void widgetSelected(SelectionEvent e) {
					wizard.delButtonClicked();
					close();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		super.createButtonsForButtonBar(parent);
	}

}
