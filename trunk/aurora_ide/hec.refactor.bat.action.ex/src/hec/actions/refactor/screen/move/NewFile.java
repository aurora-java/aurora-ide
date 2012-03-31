package hec.actions.refactor.screen.move;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class NewFile {
	// ACP1135:modules/acp/acp_sys_acp_req_types.screen
	public String moduleName;
	public String functionName;
	public boolean isPublic = false;
	// ACP1135:modules/acp/acp_sys_acp_req_types.screen
	public String oldPath;

	// :modules/acp/acp1135/acp_sys_acp_req_types.screen
	// public String newPath;
	public String getNewPath() {
		Path path = new Path(oldPath);
		String p = functionName;
		if (isPublic) {
			p = "public";
		}
//		String[] segments = path.segments();

		IPath np = new Path("");
		
		np = np.append(path.segment(0));
		String segment = path.segment(1);
		if(segment == null){
			System.out.println();
		}
		np = np.append(segment);
		//TODO delete for hr      
//		np = np.append(path.segment(2));
		
		np = np.append(p);
		np = np.append(path.segment(path.segmentCount()-1));

		// int i = 0;
		// for (String string : segments) {
		// np.append(string);
		// if (i == 1) {
		// np.append(p);
		// }
		// i++;
		// }
		return np.toString();
	}
}
