package aurora.ide.prototype.consultant.view.property.page;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;

import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;

public class ResourceTester extends PropertyTester {

	public ResourceTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if ((receiver instanceof Node) == false) {
			return false;
		}
		Node n = (Node) receiver;
		File file = n.getFile();
		if ("isProject".equals(property)) {
			return expectedValue.equals(ResourceUtil.isProject(file));
		}
		if ("isFunction".equals(property)) {
			return expectedValue.equals(ResourceUtil.isFunction(file));
		}
		if ("isModule".equals(property)) {
			return expectedValue.equals(ResourceUtil.isModule(file));
		}
		if ("isUIP".equals(property)) {
			return expectedValue.equals(ResourceUtil.isUIP(file));
		}
		return false;
	}

}
