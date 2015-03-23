package aurora.ide.project.wizard;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.editor.widgets.core.IUpdateMessageDialog;
import aurora.ide.helpers.LocaleMessage;



public class ProjectWizardPage extends WizardPage implements IUpdateMessageDialog{

	private static String WZ_TITLE = LocaleMessage.getString("aurora.project");
	private static String WZ_DESCRIPTION = LocaleMessage.getString("create.a.new.aurora.project");
	public ProjectWizardPage() {
		super("aurora.wizard.Page");
		setTitle(WZ_TITLE);
		setDescription(WZ_DESCRIPTION);
	}
	public void createControl(Composite parent) {
		Composite composite =  new Composite(parent,SWT.NONE);
        setControl(composite);
       
   }
	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}
