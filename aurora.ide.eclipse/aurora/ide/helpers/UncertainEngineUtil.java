package aurora.ide.helpers;


public class UncertainEngineUtil {
//	private static HashMap<IProject, UncertainEngine> project_engine = new HashMap<IProject, UncertainEngine>();

//	public static UncertainEngine initUncertainProject(IProject project)
//			throws ApplicationException {
////		Object obj = project_engine.get(project);
////		if (obj != null)
////			return (UncertainEngine) obj;
//		String webHome = ProjectUtil.getWebHomeLocalPath(project);
//		UncertainEngine ue = initUncertainProject(webHome);
//		project_engine.put(project, ue);
//		return ue;
//	}

//	static UncertainEngine initUncertainProject(String webHome)
//			throws ApplicationException {
//		try {
//			if (webHome == null)
//				return null;
//			File home_path = new File(webHome);
//			File config_path = new File(home_path, "WEB-INF");
//			DBEngineInitiator ei = new DBEngineInitiator(home_path, config_path);
//			ei.init();
//			UncertainEngine uncertainEngine = ei.getUncertainEngine();
//			return uncertainEngine;
//		} catch (Throwable e) {
//			throw new ApplicationException("启用EngineInitiator失败!", e);
//		}
//	}
//
//	/**
//	 * 
//	 * @deprecated
//	 * */
//	public static UncertainEngine getUncertainEngine() {
//		return new UncertainEngineIDE();
//	}
//
//	public static UncertainEngine getUncertainEngine(String excludePackage) {
//		return new UncertainEngineIDE(excludePackage);
//	}
//
//	public static UncertainEngine getUncertainEngine(File config_dir) {
//		return new UncertainEngineIDE(config_dir);
//	}
	// public static UncertainEngine getUncertainEngine(File config_dir,String
	// excludePackage){
	// return new UncertainEngineIDE(config_dir,excludePackage);
	// }
}
