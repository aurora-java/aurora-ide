package aurora.ide.api.composite.map;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.SAXException;

import aurora.ide.helpers.DialogUtil;

import uncertain.cache.ICache;
import uncertain.cache.MapBasedCache;
import uncertain.composite.CharCaseProcessor;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeLoaderSilentyWrapper;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.composite.NameProcessor;
import uncertain.composite.decorate.ElementModifier;
import uncertain.util.resource.CachedSourceFileCleaner;
import uncertain.util.resource.ISourceFile;
import uncertain.util.resource.ISourceFileManager;
import uncertain.util.resource.SourceFileManager;

public class CommentCompositeLoader extends CompositeLoader {

	public static CompositeLoader createInstanceForOCM(String extension) {
		CompositeLoader loader = new CommentCompositeLoader();
		loader.ignoreAttributeCase();
		loader.setDefaultExt(extension == null ? DEFAULT_EXT : extension);
		return loader;
	}

	public static CompositeLoader createInstanceForOCM() {
		return createInstanceForOCM(DEFAULT_EXT);
	}

	CompositeMap parse(InputStream stream) throws IOException, SAXException {
		CompositeMapParser p = new CommentCompositeMapParser(this);
		CompositeMap m = p.parseStream(stream);
		if (mSupportFileMerge) {
			String base_file = m.getString(KEY_BASE_FILE);
			if (base_file != null && m.getChilds() != null) {
				CompositeMap base = load(base_file);
				CompositeMap merged = ElementModifier.process(m.getChilds(),
						base);
				return merged;
			}
		}
		return m;
	}

	public CompositeMap createCompositeMap(String _prefix, String _uri,
			String _name) {
		return new CommentCompositeMap(_prefix, _uri, _name);
	}

	public static final String KEY_BASE_FILE = "_base_file";

	public static CompositeLoader createInstanceWithExt(String default_file_ext) {
		CompositeLoader loader = new CommentCompositeLoader();
		loader.setDefaultExt(default_file_ext);
		return loader;
	}

	public static CompositeLoader createInstanceWithBaseDir(String base_dir) {
		CompositeLoader loader = new CommentCompositeLoader();
		loader.setBaseDir(base_dir);
		return loader;
	}

	public static final String DEFAULT_EXT = "xml";

	String mBaseDir;
	String mDefaultExt;
	boolean mSupportXinclude = true;
	boolean mCaseInsensitive = false;
	NameProcessor mNameProcessor = null;

	ClassLoader mClassLoader = Thread.currentThread().getContextClassLoader();
	LinkedList mExtraPathList = null;
	// cache feature
	ICache mCache;
	ISourceFileManager mSourceFileManager;
	boolean mCacheEnabled = false;

	// CompositeMap merge, so that a CompositeMap can be declared to 'extend' a
	// base
	boolean mSupportFileMerge = false;
	boolean mSaveNamespaceMapping = false;

	CompositeLoaderSilentyWrapper wrapper = new CompositeLoaderSilentyWrapper(
			this);

	protected CompositeMap getCachedMap(Object key) {
		return mCache == null ? null : (CompositeMap) mCache.getValue(key);
	}

	/*
	 * public void clearCache(){ if(mCache!=null) mCache.clear(); }
	 */

	protected void saveCachedMap(Object key, CompositeMap map) {
		if (mCache != null && map != null)
			mCache.setValue(key, map);
		if (mSourceFileManager != null) {
			File source = map.getSourceFile();
			if (source != null) {
				ISourceFile sf = mSourceFileManager.addSourceFile(source);
				sf.addUpdateListener(new CachedSourceFileCleaner(mCache, key));
			}
		}
	}

	public void addExtraLoader(CompositeLoader loader) {
		if (mExtraPathList == null)
			mExtraPathList = new LinkedList();
		mExtraPathList.add(loader);
	}

	public void addDocumentPath(CompositeLoader loader) {
		addExtraLoader(loader);
	}

	public List getExtraLoader() {
		return this.mExtraPathList;
	}

	/** convert path from class style to file style */
	public String convertResourcePath(String path) {
		return path.replace('.', '/') + '.' + mDefaultExt;
	}

	public String convertResourcePath(String path, String file_ext) {
		return path.replace('.', '/') + '.' + file_ext;
	}

	public CompositeMap loadFromString(String str) throws IOException,
			SAXException {
		return parse(new ByteArrayInputStream(str.getBytes()));
	}

	public CompositeMap loadFromString(String str, String charsetName)
			throws IOException, SAXException {
		return parse(new ByteArrayInputStream(str.getBytes(charsetName)));
	}

	public CompositeMap loadFromStream(InputStream stream) throws IOException,
			SAXException {
		return parse(stream);
	}

	protected CompositeMap loadByURL_NC(String url) throws IOException,
			SAXException {
		InputStream stream = null;
		try {
			URL the_url = new URL(url);
			stream = the_url.openStream();
			return parse(stream);
		} catch (Throwable thr) {
			throw new IOException(thr.getMessage());
		} finally {
			if (stream != null)
				stream.close();
		}

	}

	public CompositeMap loadByURL(String url) throws IOException, SAXException {

		if (!getCacheEnabled())
			return loadByURL_NC(url);
		CompositeMap m = getCachedMap(url);
		if (m == null) {
			m = loadByURL_NC(url);
			saveCachedMap(url, m);
		}
		return (CompositeMap) m.clone();
	}

	public CompositeMap loadByFullFilePath_NC(String file_name)
			throws IOException, SAXException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file_name);
			CompositeMap map = parse(fis);
			map.setSourceFile(new File(file_name));
			return map;
		} catch (Exception e){
			e.printStackTrace();
			DialogUtil.logErrorException(e);
			return null;
		}finally {
			if (fis != null)
				fis.close();
		}
	}

	public CompositeMap loadByFullFilePath(String file_name)
			throws IOException, SAXException {

		if (!getCacheEnabled())
			return loadByFullFilePath_NC(file_name);
		CompositeMap m = getCachedMap(file_name);
		if (m == null) {
			m = loadByFullFilePath_NC(file_name);
			saveCachedMap(file_name, m);
		}
		return (CompositeMap) m.clone();
		// return m;
	}

	String getFullPath(String file_name) {
		String full_name = file_name;
		;
		if (file_name == null)
			return null;
		// attach default extension and file path if nessesary
		if (this.mDefaultExt != null && file_name.indexOf('.') < 0)
			full_name = full_name + '.' + this.mDefaultExt;
		if (this.mBaseDir != null)
			full_name = this.mBaseDir + full_name;
		return full_name;
	}

	public File getFile(String file_name) {
		File file = new File(getFullPath(file_name));
		if (file.exists())
			return file;
		else {
			if (this.mExtraPathList == null)
				return null;
			else {
				Iterator it = this.mExtraPathList.iterator();
				while (it.hasNext()) {
					CompositeLoader ld = (CompositeLoader) it.next();
					file = ld.getFile(file_name);
					if (file != null)
						return file;
				}
				return null;
			}
		}
	}

	public CompositeMap loadByFile(String file_name) throws IOException,
			SAXException {
		String full_name = getFullPath(file_name);

		try {
			return loadByFullFilePath(full_name);
		} catch (IOException ex) {
			if (this.mExtraPathList == null)
				throw ex;
			else {
				Iterator it = this.mExtraPathList.iterator();
				while (it.hasNext()) {
					CompositeLoader ld = (CompositeLoader) it.next();
					try {
						CompositeMap m = ld.loadByFile(file_name);
						return m;
						/*
						 * if(m!=null){ if(getCacheEnabled())
						 * composite_map_cache.put(file_name,m); return m; }
						 */
					} catch (IOException nex) {
					}
				}
				throw ex;
			}
		}
	}

	/*
	 * protected CompositeMap loadNC( String resource_name) throws IOException,
	 * SAXException { if( resource_name == null) return null; // First try to
	 * load by URL if( resource_name.indexOf(':')>0) return loadByURL_NC(
	 * resource_name); return loadByFile( resource_name);
	 * 
	 * }
	 */

	public CompositeMap load(String resource_name) throws IOException,
			SAXException {
		if (resource_name == null)
			return null;
		// First try to load by URL
		if (resource_name.indexOf(':') > 0)
			return loadByURL(resource_name);
		return loadByFile(resource_name);
	}

	public CompositeMap loadFromClassPath(String full_name) throws IOException,
			SAXException {
		return loadFromClassPath(full_name, mDefaultExt);
	}

	public CompositeMap loadFromClassPath(String full_name, String file_ext)
			throws IOException, SAXException {
		if (!mCacheEnabled || mCache == null)
			return loadFromClassPath_NC(full_name, file_ext);
		String name = full_name + '#' + file_ext;
		CompositeMap m = (CompositeMap) mCache.getValue(name);
		if (m == null) {
			m = loadFromClassPath_NC(full_name, file_ext);
			saveCachedMap(name, m);
		}
		return (CompositeMap) m.clone();
	}

	private CompositeMap loadFromClassPath_NC(String full_name, String file_ext)
			throws IOException, SAXException {
		if (full_name == null)
			throw new IllegalArgumentException(
					"path to load CompositeMap is null");
		InputStream stream = null;
		String path = convertResourcePath(full_name, file_ext);
		try {
			URL url = mClassLoader.getResource(path);
			String file = url == null ? null : url.getFile();
			// should load from stream?
			boolean need_stream = false;
			if (file == null)
				need_stream = true;
			else {
				File f = new File(file);
				if (!f.exists())
					need_stream = true;
			}
			if (need_stream) {
				stream = mClassLoader.getResourceAsStream(path);
				if (stream == null)
					throw new IOException("Can't get resource from " + path);
				return parse(stream);
			} else {
				return loadByFullFilePath_NC(file);
			}
		} finally {
			if (stream != null)
				stream.close();
		}
	}

	/**
	 * Returns the base_dir.
	 * 
	 * @return String
	 */
	public String getBaseDir() {
		return mBaseDir;
	}

	/**
	 * Returns the default_ext.
	 * 
	 * @return String
	 */
	public String getDefaultExt() {
		return mDefaultExt;
	}

	/**
	 * Sets the base_dir.
	 * 
	 * @param base_dir
	 *            The base_dir to set
	 */
	public void setBaseDir(String base_dir) {
		File f = new File(base_dir);
		if (!f.exists())
			throw new IllegalArgumentException("Directory not exists:"
					+ base_dir);
		int len = base_dir.length();
		if (base_dir.charAt(len - 1) != '\\' && base_dir.charAt(len - 1) != '/')
			this.mBaseDir = base_dir + File.separatorChar;
		else
			this.mBaseDir = base_dir;
	}

	/**
	 * Sets the default_ext.
	 * 
	 * @param default_ext
	 *            The default_ext to set
	 */
	public void setDefaultExt(String default_ext) {
		this.mDefaultExt = default_ext;
	}

	/**
	 * Returns the support_xinclude.
	 * 
	 * @return boolean
	 */
	public boolean getSupportXInclude() {
		return mSupportXinclude;
	}

	/**
	 * Sets the support_xinclude.
	 * 
	 * @param support_xinclude
	 *            The support_xinclude to set
	 */
	public void setSupportXInclude(boolean support_xinclude) {
		this.mSupportXinclude = support_xinclude;
	}

	/**
	 * @return Returns the caseInsensitive.
	 */
	public boolean getCaseInsensitive() {
		return mCaseInsensitive;
	}

	/**
	 * @param caseInsensitive
	 *            The caseInsensitive to set.
	 */
	public void setCaseInsensitive(boolean caseInsensitive) {
		this.mCaseInsensitive = caseInsensitive;
	}

	/**
	 * @return Returns the cache_enabled.
	 */
	public boolean getCacheEnabled() {
		return mCacheEnabled;
	}

	/**
	 * @param cache_enabled
	 *            The cache_enabled to set.
	 */
	public void setCacheEnabled(boolean cache_enabled) {
		this.mCacheEnabled = cache_enabled;
		if (mCacheEnabled && mCache == null)
			mCache = new MapBasedCache();
		if (mExtraPathList != null) {
			Iterator it = mExtraPathList.iterator();
			while (it.hasNext()) {
				CompositeLoader l = (CompositeLoader) it.next();
				if (mCacheEnabled)
					l.setCache(mCache);
				l.setCacheEnabled(cache_enabled);
			}
		}
	}

	public NameProcessor getNameProcessor() {
		return mNameProcessor;
	}

	public void setNameProcessor(NameProcessor name_processor) {
		this.mNameProcessor = name_processor;
	}

	public void ignoreAttributeCase() {
		NameProcessor p = new CharCaseProcessor(CharCaseProcessor.CASE_LOWER,
				CharCaseProcessor.CASE_UNCHANGED);
		setNameProcessor(p);
	}

	public ClassLoader getClassLoader() {
		return mClassLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		mClassLoader = classLoader;
	}

	public boolean getSupportFileMerge() {
		return mSupportFileMerge;
	}

	public void setSupportFileMerge(boolean supportFileMerge) {
		this.mSupportFileMerge = supportFileMerge;
	}

	/**
	 * Get/set flag to indicate whether namespace mapping should be saved in
	 * parsed CompositeMap
	 * 
	 * @return
	 */
	public boolean getSaveNamespaceMapping() {
		return mSaveNamespaceMapping;
	}

	public void setSaveNamespaceMapping(boolean saveNamespaceMapping) {
		mSaveNamespaceMapping = saveNamespaceMapping;
	}

	public ICache getCache() {
		return mCache;
	}

	public void setCache(ICache cache) {
		mCache = cache;
		if (mSourceFileManager == null)
			mSourceFileManager = SourceFileManager.getInstance();
	}

	public ISourceFileManager getSourceFileManager() {
		return mSourceFileManager;
	}

	public void setSourceFileManager(ISourceFileManager mSourceFileManager) {
		this.mSourceFileManager = mSourceFileManager;
	}

	public CompositeLoaderSilentyWrapper silently() {
		return wrapper;
	}

}
