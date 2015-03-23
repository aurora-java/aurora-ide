package aurora.ide;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class AuroraMetaProjectNature  {

	public static final String ID = AuroraPlugin.PLUGIN_ID + ".meta.nature";

	public static boolean hasAuroraNature(IProject project)
			throws CoreException {
		return project.hasNature(ID);
	}
}
