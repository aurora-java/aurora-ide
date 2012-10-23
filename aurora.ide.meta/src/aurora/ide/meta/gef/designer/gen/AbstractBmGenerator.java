package aurora.ide.meta.gef.designer.gen;

import java.util.HashMap;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;

public abstract class AbstractBmGenerator {
	public static final String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
	public static final String bm_ns_pre = "bm";
	public static final String bm_ns_uri = "http://www.aurora-framework.org/schema/bm";
	public static final String f_ns_pre = "f";
	public static final String f_ns_uri = "aurora.database.features";
	public static final String o_ns_pre = "o";
	public static final String o_ns_uri = "aurora.database.local.oracle";

	private HashMap<String, String> nsMapping = new HashMap<String, String>();

	protected String getBmName() {
		return "model";
	}

	protected String getBaseTable() {
		return null;
	}

	protected String getExtend() {
		return null;
	}

	protected String getExtendMode() {
		return null;
	}

	protected String getAlias() {
		return "e";
	}

	public CompositeMap gen() throws DuplicateException {
		CompositeMap modelMap = newModelMap();
		setUpModelMap(modelMap);
		for (String uri : nsMapping.keySet()) {
			modelMap.put("xmlns:" + nsMapping.get(uri), uri);
		}
		return modelMap;
	}

	/**
	 * after root map is created,user should add some extra node or attributes
	 * to it
	 * 
	 * @param modelMap
	 *            default created by {@link #newModelMap()}
	 * @throws DuplicateException
	 */
	protected abstract void setUpModelMap(CompositeMap modelMap)
			throws DuplicateException;

	/**
	 * create a model map,with name "model",and has a simple nsmapping
	 * 
	 * @return
	 */
	protected CompositeMap newModelMap() {
		CompositeMap modelMap = newCompositeMap(getBmName());
		modelMap.setNamespaceMapping(nsMapping);
		modelMap.put("alias", getAlias());
		modelMap.put("baseTable", getBaseTable());
		modelMap.put("extend", getExtend());
		modelMap.put("extendMode", getExtendMode());
		return modelMap;
	}

	/**
	 * @see #newCompositeMap(String, String)
	 */
	protected CompositeMap newCompositeMap(String nodeName) {
		return newCompositeMap(nodeName, bm_ns_pre);
	}

	/**
	 * 
	 * @param nodeName
	 * @param prefix
	 *            the build-in prefix are:<br/>
	 *            bm=http://www.aurora-framework.org/schema/bm<br/>
	 *            f=aurora.database.features<br/>
	 *            o=aurora.database.local.oracle<br/>
	 * @return
	 */
	protected CompositeMap newCompositeMap(String nodeName, String prefix) {
		CompositeMap map = new CommentCompositeMap(nodeName);
		map.setPrefix(prefix);
		if (bm_ns_pre.equals(prefix))
			nsMapping.put(bm_ns_uri, bm_ns_pre);
		else if (f_ns_pre.equals(prefix))
			nsMapping.put(f_ns_uri, f_ns_pre);
		else if (o_ns_pre.equals(prefix))
			nsMapping.put(o_ns_uri, o_ns_pre);
		return map;
	}
}
