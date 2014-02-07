package aurora.ide.prototype.consultant.view.property.page;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.prototype.consultant.view.wizard.CreateFunctionWizard;

public class FunctionFSDPropertyPage extends AbstractFSDPropertyPage {
	protected CompositeMap loadProperties(File file) {
		CompositeMap pp = ResourceUtil.loadFunctionProperties(file);
		ResourceUtil.copyProjectProperties(file, pp);
		return pp;

	}

	protected void saveProperties(CompositeMap map) throws IOException {
		IAdaptable element = this.getElement();
		if (element instanceof Node) {
			File file = ((Node) element).getFile();
			ResourceUtil.createFile(file,
					CreateFunctionWizard.QUICK_UI_FUNCTION, map);
		}
	}

	@Override
	protected IPath getBasePath() {
		Node element = (Node) getElement();
		return element.getPath();
	}
}
