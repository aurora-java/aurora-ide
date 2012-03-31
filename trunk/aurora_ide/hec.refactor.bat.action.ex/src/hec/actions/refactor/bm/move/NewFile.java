package hec.actions.refactor.bm.move;

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
		// For hr
		// try{
		// String a = segments[1];
		// }catch(Exception e){
		// return null;
		// }

		String s = segments[0] +
		// "." + /* TODO delete for hr */segments[1] +
				"." + p + "." + segments[segments.length - 1];
		return s;
	}

}
