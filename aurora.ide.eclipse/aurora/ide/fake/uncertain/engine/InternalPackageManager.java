package aurora.ide.fake.uncertain.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import uncertain.composite.CompositeLoader;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.ClassRegistryMBean;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.pkg.ComponentPackage;
import uncertain.pkg.IInstanceCreationListener;
import uncertain.pkg.InstanceConfig;
import uncertain.pkg.PackageManager;
import uncertain.pkg.PackagePath;
import uncertain.schema.SchemaManager;
import uncertain.util.FileUtil;

public class InternalPackageManager extends PackageManager {

	public static final String FILE_PACKAGE_XML = "package.xml";
	public static final String KEY_CONFIG = "config";
	CompositeLoader mCompositeLoader;
	OCManager mOCManager;
	// package name -> loaded component package
	HashMap<String, ComponentPackage> mPackageNameMap = new HashMap<String, ComponentPackage>();
	ClassRegistry mClassRegistry;
	SchemaManager mSchemaManager;
	// path -> loaded component package
	HashMap<String, ComponentPackage> mLoadedPackagePaths = new HashMap<String, ComponentPackage>();

	String mTempPath;

	public static boolean isPackageDirectory(File dir) {
		if (!dir.isDirectory())
			return false;
		File config_dir = new File(dir, KEY_CONFIG);
		if (!config_dir.exists() || !config_dir.isDirectory())
			return false;
		File pkg_xml = new File(config_dir, FILE_PACKAGE_XML);
		if (!pkg_xml.exists())
			return false;
		return true;
	}

	public InternalPackageManager() {
		mCompositeLoader = CompositeLoader.createInstanceForOCM(null);
		mOCManager = OCManager.getInstance();
		mClassRegistry = mOCManager.getClassRegistry();
		mSchemaManager = new SchemaManager();
	}

	public InternalPackageManager(CompositeLoader loader, OCManager oc_manager,
			SchemaManager schema_manager) {
		mCompositeLoader = loader;
		mOCManager = oc_manager;
		mClassRegistry = mOCManager.getClassRegistry();
		mSchemaManager = schema_manager;
	}

	public CompositeLoader getCompositeLoader() {
		return mCompositeLoader;
	}

	public ClassRegistryMBean getClassRegistry() {
		return mClassRegistry;
	}

	public void setClassRegistry(ClassRegistry mClassRegistry) {
		this.mClassRegistry = mClassRegistry;
	}

	public OCManager getOCManager() {
		return mOCManager;
	}

	public ComponentPackage loadPackage(String path) throws IOException {
		return loadPackage(path, InternalComponentPackage.class);
	}

	protected void initPackage(ComponentPackage pkg) {
		pkg.setPackageManager(this);
	}

	public void addPackage(ComponentPackage pkg) {
		String name = pkg.getName();
		if (pkg.getName() == null)
			throw new IllegalArgumentException("Package name can't be null");
		// if(mPackageNameMap.containsKey(name)){
		// throw new
		// IllegalArgumentException("Package with name "+name+" is already loaded");
		// }
		mPackageNameMap.put(name, pkg);

		// if(pkg.getPackageManager()!=this)
		initPackage(pkg);
		SchemaManager sm = pkg.getSchemaManager();
		if (sm != null)
			mSchemaManager.addAll(sm);
		ClassRegistry reg = pkg.getClassRegistry();
		if (reg != null)
			mClassRegistry.addAll(reg);
	}

	public ComponentPackage loadPackage(String path, Class implement_cls)
			throws IOException {
		File f = new File(path);
		path = f.getCanonicalPath();
		ComponentPackage pkg = mLoadedPackagePaths.get(path);
		if (pkg != null)
			return pkg;

		try {
			pkg = (ComponentPackage) implement_cls.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Can't create instance of "
					+ implement_cls.getName(), ex);
		}
		initPackage(pkg);
		pkg.load(path);
		addPackage(pkg);
		return pkg;
	}

	public ComponentPackage getPackage(String name) {
		return mPackageNameMap.get(name);
	}

	public SchemaManager getSchemaManager() {
		return mSchemaManager;
	}

	/**
	 * Load all package under specified directory
	 * 
	 * @param directory
	 *            root directory that contains packages to load
	 * @throws IOException
	 */
	public void loadPackgeDirectory(String root_directory) throws IOException {
		File path = new File(root_directory);
		if (!path.isDirectory())
			throw new IllegalArgumentException(root_directory
					+ " is not a directory");
		File[] files = path.listFiles();
		List<File> file_list = FileUtil.getSortedList(files);
		for (File file : file_list) {
			// for (int i = 0; i < files.length; i++) {
			// File file = files[i];
			if (isPackageDirectory(file))
				loadPackage(file.getAbsolutePath());
		}
	}

	protected void extractTempZipFile(JarInputStream jis, File baseDir,
			String file_name) throws IOException {
		File file = new File(baseDir, file_name);
		file.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(file);
		for (int c = jis.read(); c != -1; c = jis.read()) {
			fos.write(c);
		}
		fos.close();
	}

	/**
	 * Unzip specified entry in a jar file to temp directory
	 * 
	 * @param jar_path
	 * @param pkg_name
	 * @return
	 * @throws IOException
	 */
	protected File createTempPackageDir(String jar_path, String pkg_name)
			throws IOException {
		if (jar_path == null)
			throw new NullPointerException("jar_path parameter is null");
		InputStream is = null;
		File tempDir = null;
		if (this.mTempPath != null) {
			tempDir = new File(mTempPath);
			if (!tempDir.exists() || !tempDir.isDirectory())
				throw BuiltinExceptionFactory.createInvalidPathException(null,
						mTempPath);
		} else
			tempDir = new File(System.getProperty("java.io.tmpdir"));
		try {
			// to be enhanced. in weblogic+linux path doesn't start with file://
			try {
				URL u = new URL(jar_path);
				is = u.openStream();
			} catch (MalformedURLException ex) {
				is = new FileInputStream(jar_path);
			}
			JarInputStream jis = new JarInputStream(is);
			ZipEntry ze = null;

			File baseDir = tempDir;
			while ((ze = jis.getNextEntry()) != null) {
				String name = ze.getName();
				if (name.startsWith(pkg_name)) {
					if (ze.isDirectory()) {
						baseDir = new File(tempDir, name);
						if (baseDir.exists())
							FileUtil.deleteDirectory(baseDir);
						if (!baseDir.mkdirs())
							throw new IOException("Can't create dir "
									+ baseDir.getAbsolutePath());
						baseDir.deleteOnExit();
					} else {
						extractTempZipFile(jis, tempDir, name);
						jis.closeEntry();
					}
				}
			}
			return new File(tempDir, pkg_name);
		} finally {
			if (is != null)
				is.close();
		}
	}

	private File getClassPathDirectory(String pkg_name) throws IOException {
		/*
		 * URL url = Thread.currentThread().getContextClassLoader()
		 * .getResource(pkg_name);
		 */
		ClassLoader loader = PackageManager.class.getClassLoader();
		URL url = loader.getResource(pkg_name);
		if (url == null) {
			loader = Thread.currentThread().getContextClassLoader();
			url = loader.getResource(pkg_name);
		}
		if (url == null)
			throw new IOException("Can't find " + pkg_name
					+ " from current classpath");
		String file = url.getFile();
		int index = file.indexOf("!");
		if (index > 0) {
			// from jar
			String jar_file = file.substring(0, index);
			File dir = createTempPackageDir(jar_file, pkg_name);
			return dir;
		} else {
			try {
				return new File(new URI(url.toString()));
			} catch (URISyntaxException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Load a package from CLASSPATH, either from directory, or from an entry in
	 * jar
	 * 
	 * @param pkg_name
	 *            name of package, in relative form, such as
	 *            "uncertain_builtin_package/uncertain.test/" if the package is
	 *            in a jar file, the name must ends with path separator ( that
	 *            is, '/' )
	 * @throws IOException
	 */
	public void loadPackgeFromClassPath(String pkg_name) throws IOException {

		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(pkg_name);
		if (url == null)
			throw new IOException("Can't load " + pkg_name);
		String file = url.getFile();
		int index = file.indexOf("!");
		if (index > 0) {
			// from jar
			String jar_file = file.substring(0, index);
			File dir = createTempPackageDir(jar_file, pkg_name);
			loadPackage(dir.getAbsolutePath());
		} else {
			// from directory
			URI uri = null;
			try {
				uri = new URI(url.toString());
			} catch (URISyntaxException ex) {
				throw new RuntimeException("Can't parse uri from resource url "
						+ url.toString());
			}
			loadPackage(new File(uri).getAbsolutePath());
		}

	}

	/**
	 * Load all packages under a relative directory name in classpath, such as
	 * "uncertain_builtin_package/sub_package"
	 * 
	 * @param root_classpath
	 * @throws IOException
	 */
	public void loadPackageFromRootClassPath(String root_classpath)
			throws IOException {
		File path_file = getClassPathDirectory(root_classpath);
		loadPackgeDirectory(path_file.getAbsolutePath());
	}

	public void loadPackage(PackagePath path) throws IOException {
		if (path.getPath() != null)
			loadPackage(path.getPath());
		else if (path.getClassPath() != null)
			loadPackgeFromClassPath(path.getClassPath());
		else if (path.getRootClassPath() != null) {
			loadPackageFromRootClassPath(path.getRootClassPath());
		}
	}

	public void loadPackagePaths(PackagePath[] paths) throws IOException {
		for (int i = 0; i < paths.length; i++)
			loadPackage(paths[i]);
	}

	public void createInstances(IObjectRegistry reg,
			IInstanceCreationListener listener, boolean continueWithException) {
		List<InstanceConfig> lst = new LinkedList<InstanceConfig>();
		for (ComponentPackage pkg : mPackageNameMap.values()) {
			InstanceConfig cfg = pkg.getInstanceConfig();
			if (cfg != null)
				lst.add(cfg);
		}
		InstanceConfig.loadComponents(lst, reg, mCompositeLoader, mOCManager,
				listener, continueWithException);
	}

	public String getTempPath() {
		return mTempPath;
	}

	public void setTempPath(String tempPath) {
		this.mTempPath = tempPath;
	}

}
