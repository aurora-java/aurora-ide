package aurora.lwap.ide.service;

import org.eclipse.jface.viewers.ISelection;

import aurora.ide.helpers.LocaleMessage;
import aurora.ide.screen.wizard.ServiceNewWizardPage;

public class NewServiceWizardPage extends ServiceNewWizardPage {

	public NewServiceWizardPage(ISelection selection) {
		super(selection);
		this.setTitle("Service");
		this.setMessage("New Service File");
	}

	public String getFileName() {
		String fileName = getFileTextText();
		if (fileName.indexOf(".") == -1) {
			fileName = fileName + "." + "service";
		}
		return fileName;
	}
	protected boolean checkExt(String fileName, int dotLoc) {
		String ext = fileName.substring(dotLoc + 1);
		if (ext.equalsIgnoreCase("service") == false) {
			updateStatus(LocaleMessage.getString("file.extension.must.be.service"));
			return true;
		}
		return false;
	}
}
