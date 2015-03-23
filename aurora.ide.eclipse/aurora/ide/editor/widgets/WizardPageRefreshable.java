package aurora.ide.editor.widgets;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

public abstract class WizardPageRefreshable extends WizardPage {

	protected WizardPageRefreshable(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	protected WizardPageRefreshable(String pageName) {
		super(pageName);
	}
		
	public void refreshPage() {
		if(!isInit()){
			return;
		}
	}
	
	public abstract void checkPageValues();
	
	public void initPageValues(){
		
	}
	
	public void updatePageStatus(String errorMessage){
		setErrorMessage(errorMessage);
		setPageComplete(errorMessage == null);
	}
	public boolean isInit(){
		return getControl()!= null;
	}

}
