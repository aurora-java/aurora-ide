package aurora.ide.prototype.consultant.view.property.page;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IAdaptable;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.prototype.consultant.view.wizard.CreateModuleWizard;

public class ModuleFSDPropertyPage extends ProjectFSDPropertyPage {
	protected CompositeMap loadProperties(File file) {
		CompositeMap pp = ResourceUtil.loadModuleProperties(file);
		ResourceUtil.copyProjectProperties(file, pp);
		return pp;

	}

	protected void saveProperties(CompositeMap map) throws IOException {
		IAdaptable element = this.getElement();
		if (element instanceof Node) {
			File file = ((Node) element).getFile();
			ResourceUtil.createFile(file, CreateModuleWizard.QUICK_UI_MODULE,
					map);
		}
	}

}
