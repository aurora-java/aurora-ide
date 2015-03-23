package aurora.plugin.sap.sync.idoc;

import aurora.application.admin.ServerAdmin;

public class IDocTest {

	public static void main(String[] args) {
		String idocDir = "E:/workspace/worksheet/idoc";
		ServerAdmin admin = new ServerAdmin(800, idocDir);
		admin.doStartup();
	}
}
