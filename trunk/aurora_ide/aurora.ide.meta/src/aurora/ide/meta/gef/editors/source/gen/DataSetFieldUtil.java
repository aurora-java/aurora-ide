package aurora.ide.meta.gef.editors.source.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.schema.Attribute;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.cache.CacheManager;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.Dataset;

public class DataSetFieldUtil {
	private String fieldName;
	private String bmPath;
	private CompositeMap bmMap = null;

	private AuroraMetaProject aProj;
	private CompositeMap optionMap;
	private CompositeMap currentField;
	private String pkName;
	private String options;
	private String lookupCode;

	public DataSetFieldUtil(IProject project, String fieldName, String bmPath) {
		super();
		this.fieldName = fieldName;
		this.bmPath = bmPath;
		aProj = new AuroraMetaProject(project);
	}

	/**
	 * get the CompositeMap of the given model path
	 * 
	 * @param bmPath
	 *            model path of a bm<br/>
	 *            e.g. <i>a.b.c</i>
	 * @return
	 */
	public CompositeMap getBmMap(String bmPath) {
		if (bmPath == null || bmPath.length() == 0) {
			return null;
		}
		IResource resource = null;
		try {
			IProject proj = aProj.getAuroraProject();
			resource = ResourceUtil.getBMFile(proj, bmPath);
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				CompositeMap map = CacheManager.getWholeBMCompositeMap(file);
				return map;
			}
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * get the CompositeMap of current model path
	 * 
	 * @return return null if not found
	 */
	public CompositeMap getBmMap() {
		if (bmMap == null)
			bmMap = getBmMap(bmPath);
		return bmMap;
	}

	/**
	 * get the options of current field
	 * 
	 * @return return null if not found
	 */
	public String getOptions() {
		if (options == null) {
			CompositeMap fMap = getCurrentField();
			if (fMap != null) {
				options = fMap.getString("options");
			}
		}
		return options;
	}
	public String getLookupCode() {
		if (lookupCode == null) {
			CompositeMap fMap = getCurrentField();
			if (fMap != null) {
				lookupCode = Util.getValueIgnoreCase(fMap, "lookupCode");
			}
		}
		return lookupCode;
	}

	/**
	 * if current field has a options (lov or comboBox) ,get the CompositeMap of
	 * that options
	 * 
	 * @return CompositeMap of <i>options</i> on current field<br/>
	 *         return null if not found
	 */
	public CompositeMap getOptionsMap() {
		if (optionMap == null)
			optionMap = getBmMap(getOptions());
		return optionMap;
	}

	/**
	 * 
	 * @return
	 */
	public CompositeMap getCurrentField() {
		CompositeMap bmMap = getBmMap();
		if (bmMap == null)
			return null;
		if (currentField == null) {
			for (CompositeMap m : getLocalFields(bmMap)) {
				if (m.getString("name").equalsIgnoreCase(fieldName)) {
					currentField = m;
					break;
				}
			}
		}
		return currentField;
	}

	/**
	 * get the name of the first pk-field
	 * 
	 * @param bmMap
	 *            CompositeMap of a bm
	 * @return the first pk (or null no pk-field exists)
	 */
	public String getPK(CompositeMap bmMap) {
		if (bmMap == null)
			return null;
		CompositeMap pkMap = bmMap.getChild("primary-key");
		if (pkMap != null) {
			@SuppressWarnings("unchecked")
			List<CompositeMap> list = pkMap.getChildsNotNull();
			if (list.size() > 0) {
				return list.get(0).getString("name");
			}
		}
		return null;
	}

	public String getPk() {
		if (pkName == null)
			pkName = getPK(getBmMap());
		return pkName;
	}

	/**
	 * 
	 * @param bmMap
	 * @param includeRefField
	 * @return
	 */
	public ArrayList<CompositeMap> getLocalFields(CompositeMap bmMap,
			final boolean includeRefField) {
		final ArrayList<CompositeMap> als = new ArrayList<CompositeMap>();
		if (bmMap == null)
			return als;
		bmMap.iterate(new IterationHandle() {

			public int process(CompositeMap map) {
				try {
					List<Attribute> list = SxsdUtil.getAttributesNotNull(map);
					if (list == null)
						return 0;
					for (Attribute a : list) {
						if (map.get(a.getName()) != null) {
							if (SxsdUtil.isLocalFieldReference(a
									.getAttributeType())) {
								String name = a.getName();
								if (map.getName().equalsIgnoreCase("field")
										|| (map.getName().equalsIgnoreCase(
												"ref-field") && includeRefField)) {
									if (name.equalsIgnoreCase("name")) {
										als.add(map);
										continue;
									}
								}
							}
						}
					}
				} catch (Exception e) {
				}
				return 0;
			}
		}, true);
		return als;
	}

	/**
	 * {@link #getLocalFields(CompositeMap, boolean)}
	 * 
	 * @param bmMap
	 * @return
	 */
	public ArrayList<CompositeMap> getLocalFields(CompositeMap bmMap) {
		return getLocalFields(bmMap, true);
	}

	public static Dataset findDataset(Container container) {
		if (container == null)
			return null;
		Dataset dataset = container.getDataset();
		if (dataset == null)
			return null;
		boolean useParentBM = isUseParentBM(container, dataset);
		if (useParentBM) {
			return findDataset(container.getParent());
		}
		return dataset;
	}

	public static boolean isUseParentBM(Container container, Dataset dataset) {
		if (Container.SECTION_TYPE_QUERY.equals(container.getSectionType())
				|| Container.SECTION_TYPE_RESULT.equals(container
						.getSectionType())) {
			return false;
		}
		return dataset == null;
	}
}
