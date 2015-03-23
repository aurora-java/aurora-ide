package aurora.ide.navigator.action;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import aurora.ide.AuroraMetaProjectNature;
import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.DialogUtil;

public class ProjectNatureTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if (!(receiver instanceof IProject))
			return false;
		IProject project = (IProject) receiver;
		if (!project.isOpen())
			return false;
		if (args == null || args.length < 1)
			return false;
		Object arg = args[0];
		try {
			if (AuroraMetaProjectNature.hasAuroraNature(project)) {
				return false;
			}
			boolean hasAuroraNature = AuroraProjectNature
					.hasAuroraNature(project);
			return arg.equals(new Boolean(hasAuroraNature));
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
			return false;
		}
	}
}
