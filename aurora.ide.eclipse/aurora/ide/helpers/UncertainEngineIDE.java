package aurora.ide.helpers;

import java.io.File;
import java.io.IOException;

import uncertain.core.UncertainEngine;
import uncertain.pkg.PackageManager;

public class UncertainEngineIDE extends UncertainEngine {

	private String excludePackage;

	public UncertainEngineIDE() {
		super();
		loadBuiltinPackage();
	}

	public UncertainEngineIDE(String excludePackage) {
		super();
		this.excludePackage = excludePackage;
		loadBuiltinPackage();
	}

	public UncertainEngineIDE(File config_dir) {
		this(config_dir, null);
	}

	public UncertainEngineIDE(File config_dir, String excludePackage) {
		super();
		setConfigDirectory(config_dir);
		this.excludePackage = excludePackage;
		loadBuiltinPackage();
	}

	protected void bootstrap() {
		try {
			super.bootstrap();
		} catch (Exception e) {
//			e.printStackTrace();
			// LogUtil.getInstance().logWarning(AuroraPlugin.PLUGIN_ID, e);
		}
//		ISourceFileManager instanceOfType = (ISourceFileManager)this.getObjectRegistry().getInstanceOfType(ISourceFileManager.class);
//		if(instanceOfType  instanceof ILifeCycle){
//			((ILifeCycle) instanceOfType).shutdown();	
//		}
	}

	protected void loadBuiltinPackage() {
		try {
			PackageManager packageManager = getPackageManager();
			if ("uncertain_builtin_package".equals(excludePackage))
				return;
			else if ("aurora_builtin_package".equals(excludePackage)) {
				packageManager
						.loadPackgeDirectory(
								AuroraResourceUtil.getClassPathFile(
										"uncertain_builtin_package")
										.getCanonicalPath());
				return;
			}
			packageManager.loadPackgeDirectory(
					AuroraResourceUtil.getClassPathFile(
							"uncertain_builtin_package").getCanonicalPath());
			packageManager.loadPackgeDirectory(
					AuroraResourceUtil.getClassPathFile(
							"aurora_builtin_package").getCanonicalPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
