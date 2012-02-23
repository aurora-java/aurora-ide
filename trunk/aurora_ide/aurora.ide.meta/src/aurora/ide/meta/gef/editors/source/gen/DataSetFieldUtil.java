package aurora.ide.meta.gef.editors.source.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.composite.IterationHandle;
import uncertain.schema.Attribute;
import aurora.ide.bm.BMUtil;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.project.AuroraMetaProject;

public class DataSetFieldUtil {
	private String fieldName;
	private String bmPath;

	private AuroraMetaProject aProj;

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
			resource = BMUtil.getBMResourceFromClassPath(proj, bmPath);
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				CompositeMap map = new CompositeMapParser(new CompositeLoader())
						.parseStream(file.getContents());
				return map;
			}
		} catch (Exception e) {
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
		return getBmMap(bmPath);
	}

	/**
	 * get the options of current field
	 * 
	 * @return return null if not found
	 */
	public String getOptions() {
		CompositeMap fMap = getCurrentField();
		if (fMap != null) {
			String bmPath = fMap.getString("options");
			return bmPath;
		}
		return null;
	}

	/**
	 * if current field has a options (lov or comboBox) ,get the CompositeMap of
	 * that options
	 * 
	 * @return CompositeMap of <i>options</i> on current field<br/>
	 *         return null if not found
	 */
	public CompositeMap getOptionsMap() {
		return getBmMap(getOptions());
	}

	/**
	 * 
	 * @return
	 */
	public CompositeMap getCurrentField() {
		CompositeMap bmMap = getBmMap();
		if (bmMap == null)
			return null;
		for (CompositeMap m : getLocalFields(bmMap)) {
			if (m.getString("name").equalsIgnoreCase(fieldName))
				return m;
		}
		return null;
	}

	/**
	 * get the name of the first pk-field
	 * 
	 * @param bmMap
	 *            CompositeMap of a bm
	 * @return the first pk (or null no pk-field exists)
	 */
	public String getPK(CompositeMap bmMap) {
		final String pk[] = { null };
		bmMap.iterate(new IterationHandle() {

			@Override
			public int process(CompositeMap map) {
				if (map.getName().equalsIgnoreCase("pk-field")) {
					pk[0] = map.getString("name");
					return IterationHandle.IT_BREAK;
				}
				return 0;
			}
		}, true);
		return pk[0];
	}

	public ArrayList<CompositeMap> getLocalFields(CompositeMap bmMap) {
		final ArrayList<CompositeMap> als = new ArrayList<CompositeMap>();
		bmMap.iterate(new IterationHandle() {

			@Override
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
										|| map.getName().equalsIgnoreCase(
												"ref-field")) {
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
