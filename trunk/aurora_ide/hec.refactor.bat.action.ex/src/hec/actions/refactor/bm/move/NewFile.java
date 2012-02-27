package hec.actions.refactor.bm.move;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class NewFile {
	// ACP1135:acp.acp_company_acp_req_types

	public String functionName;
	public boolean isPublic = false;
	// ACP1135:acp.acp_company_acp_req_types
	public String oldPath;

	public String getModuleName() {
		String[] segments = oldPath.split("\\.");
		return segments[0];
	}

	public String getNewPath() {

		String p = functionName.toUpperCase();
		if (isPublic || "db".equals(this.getModuleName())) {
			return null;
		}

		String[] segments = oldPath.split("\\.");
		String s = segments[0] + "." + p + "." + segments[segments.length - 1];
		return s;
	}

}
