package aurora.ide.meta.gef.editors.source.gen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.api.javascript.JavascriptRhino;
import aurora.ide.meta.extensions.ExtensionLoader;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.meta.gef.editors.source.gen.DataSetFieldUtil;
import aurora.ide.meta.js.StopRunningException;

public class UIPParser {
	private CompositeMap uipMap;
	private IFile file;

	public UIPParser(CompositeMap uipMap) {
		this.setUipMap(uipMap);
		String path = uipMap.getString("file_path", "");
		// file = ResourcesPlugin.getWorkspace().getRoot().getFile(new
		// Path(path));
	}

	public CompositeMap getUipMap() {
		return uipMap;
	}

	public void setUipMap(CompositeMap uipMap) {
		this.uipMap = uipMap;
	}

	public List<CompositeMap> getComponents(String componentType) {
		return getComponents(uipMap, componentType);
	}

	private List<CompositeMap> getComponents(CompositeMap map,
			String componentType) {
		List<CompositeMap> result = new ArrayList<CompositeMap>();
		List<?> childs = map.getChilds();
		if (childs != null) {
			for (Object object : childs) {
				if (object instanceof CompositeMap) {
					String _type = ((CompositeMap) object).getString(
							"component_type", "");
					if (_type.equalsIgnoreCase(componentType)) {
						result.add((CompositeMap) object);
					}
					if (hasChild((CompositeMap) object)) {
						result.addAll(getComponents((CompositeMap) object,
								componentType));
					}
				}
			}
		}
		return result;
	}

	private List<CompositeMap> getComponents(CompositeMap map,
			List<String> componentTypes) {
		List<CompositeMap> result = new ArrayList<CompositeMap>();
		List<?> childs = map.getChilds();
		if (childs != null) {
			for (Object object : childs) {
				if (object instanceof CompositeMap) {
					String _type = ((CompositeMap) object).getString(
							"component_type", "");
					if (componentTypes.contains(_type.toLowerCase())) {
						result.add((CompositeMap) object);
					}
					if (hasChild((CompositeMap) object)) {
						result.addAll(getComponents((CompositeMap) object,
								componentTypes));
					}
				}
			}
		}
		return result;
	}

	public boolean hasChild(CompositeMap map) {
		List<?> childs = map.getChilds();
		return false == (childs == null || childs.isEmpty());
	}

	public List<CompositeMap> getLayouts() {
		List<String> names = ExtensionLoader
				.getComponentNamesByCategoryId("aurora.ide.meta.palette.category.layout");
		return getComponents(uipMap, names);
	}

	public List<CompositeMap> getButtons() {
		List<String> names = ExtensionLoader
				.getComponentNamesByCategoryId("aurora.ide.meta.palette.category.button");
		return getComponents(uipMap, names);
	}

	public List<CompositeMap> getGrids() {
		return getComponents("grid");
	}

	public List<CompositeMap> getInputComponents() {
		List<String> names = ExtensionLoader
				.getComponentNamesByCategoryId("aurora.ide.meta.palette.category.input");
		return getComponents(uipMap, names);
	}

	public List<CompositeMap> getDatasets() {
		List<CompositeMap> datasets = new ArrayList<CompositeMap>();
		List<CompositeMap> qds = this.getComponents("querydataset");
		for (CompositeMap q : qds) {
			if (isRealDataset(q)) {
				datasets.add(q);
			}
		}
		List<CompositeMap> rds = this.getComponents("resultdataset");
		for (CompositeMap r : rds) {
			if (isRealDataset(r)) {
				datasets.add(r);
			}
		}
		// List<CompositeMap> combobox = getComponents(uipMap, "combobox");
		return datasets;
	}

	public boolean isRealDataset(CompositeMap ds) {
		CompositeMap parent = ds.getParent();
		return isSectionComponent(parent);
	}

	public boolean isSectionComponent(CompositeMap parent) {
		if (parent == null)
			return false;
		String sectionType = parent.getString("sectiontype", "");
		if ("SECTION_TYPE_QUERY".equalsIgnoreCase(sectionType)
				|| "SECTION_TYPE_RESULT".equalsIgnoreCase(sectionType)) {
			return true;
		}
		return false;
	}

	public List<CompositeMap> getDatasetFields(CompositeMap ds) {
		List<CompositeMap> fields = new ArrayList<CompositeMap>();
		CompositeMap parent = ds.getParent();
		List<CompositeMap> components = this.getComponents(parent,
				"datasetfield");
		for (CompositeMap f : components) {
			if (hasDSFieldChild(parent, f)) {
				fields.add(f);
			}
		}
		return fields;
	}

	private boolean hasDSFieldChild(CompositeMap parent, CompositeMap child) {
		if (child == null)
			return false;
		CompositeMap _p = child.getParent();
		if (parent.equals(_p)) {
			return true;
		}
		if (isSectionComponent(_p)) {
			return false;
		}
		return hasDSFieldChild(parent, _p);
	}

	public String getFunctionName(String script) {
		JavascriptRhino js = new JavascriptRhino(script);
		String name = js.getFirstFunctionName();
		return name == null ? "" : name;
	}

	public List<CompositeMap> getLovMaps(CompositeMap fieldMap) {
		if (true)
			return Collections.EMPTY_LIST;
		DataSetFieldUtil dataSetFieldUtil = new DataSetFieldUtil(
				file.getProject(), fieldMap.getString("field_name", ""),
				fieldMap.getString("options", ""));
		CompositeMap bmMap = dataSetFieldUtil.getBmMap();
		if (bmMap == null)
			return null;
		MapFinder mf = new MapFinder();
		CompositeMap relation = mf.lookupRelation(
				fieldMap.getString("field_name", ""), bmMap);
		if (relation != null) {
			String rName = relation.getString("name", "");
			List<CompositeMap> lovFields = mf.lookupLovFields(rName, bmMap);
			return lovFields;
		}
		return null;
	}

	public void bindMapping(CompositeMap fieldMap, List<CompositeMap> lovMaps) {
		if (true)
			return;

		DataSetFieldUtil dataSetFieldUtil = new DataSetFieldUtil(
				file.getProject(), fieldMap.getString("field_name", ""),
				fieldMap.getString("options", ""));
		CompositeMap bmMap = dataSetFieldUtil.getBmMap();
		MapFinder mf = new MapFinder();
		CompositeMap relation = mf.lookupRelation(
				fieldMap.getString("field_name", ""), bmMap);
		CompositeMap refMap = relation.getChild("reference");
		CompositeMap mappingMap = AuroraComponent2CompositMap
				.createChild("mapping");

		String foreignField = Util.getCompositeValue("foreignField", refMap);
		String localField = Util.getCompositeValue("localField", refMap);
		if (localField != null && foreignField != null) {
			CompositeMap idMap = AuroraComponent2CompositMap.createChild("map");
			idMap.put("from", foreignField);
			idMap.put("to", localField);
			mappingMap.addChild(idMap);
		}
		CompositeMap optionsMap = dataSetFieldUtil.getOptionsMap();
		BMCompositeMap opb = null;
		if (optionsMap != null)
			opb = new BMCompositeMap(optionsMap);

		for (CompositeMap lovMap : lovMaps) {
			String source = Util.getCompositeValue("sourceField", lovMap);
			String name = Util.getCompositeValue("name", lovMap);

			if (opb == null || opb.getFieldByName(source) == null)
				continue;
			if (source != null && name != null) {
				CompositeMap m = AuroraComponent2CompositMap.createChild("map");
				m.put("from", source);
				m.put("to", name);
				mappingMap.addChild(m);
			}
		}
		fieldMap.addChild(mappingMap);
	}

	public String getComboDisplayField(CompositeMap fieldMap) {
		if (true)
			return "getComboDisplayField";
		DataSetFieldUtil dataSetFieldUtil = new DataSetFieldUtil(
				file.getProject(), fieldMap.getString("field_name", ""),
				fieldMap.getString("options", ""));
		String value = "";
		CompositeMap optionsMap = dataSetFieldUtil.getOptionsMap();
		if (optionsMap != null) {
			value = Util.getValueIgnoreCase(optionsMap, "defaultDisplayField");
		} else if (dataSetFieldUtil.getLookupCode() != null) {
			value = "code_value_name";
		}
		return value;
	}

	public String getComboValueField(CompositeMap fieldMap) {
		if (true)
			return "getComboValueField";
		DataSetFieldUtil dataSetFieldUtil = new DataSetFieldUtil(
				file.getProject(), fieldMap.getString("field_name", ""),
				fieldMap.getString("options", ""));
		String value = "";
		CompositeMap optionsMap = dataSetFieldUtil.getOptionsMap();
		if (optionsMap != null) {
			value = dataSetFieldUtil.getPK(optionsMap);
		} else if (dataSetFieldUtil.getLookupCode() != null) {
			value = "code_value";
		}
		return value;
	}

	public String getButtonOpenParameters(CompositeMap buttonMap) {
		// List<Parameter> parameters = link.getParameters();
		// buttonMap.getclicker.getparmeters
		// mapping
		//

		// if (parameters.size() > 0) {
		// Parameter p = parameters.get(0);
		// Container container = p.getContainer();
		// String findDatasetId = sg.findDatasetId(container);
		// String ds = "var record = $('" + findDatasetId
		// + "').getCurrentRecord();";
		// script = script.replace("#parameters#", ds + " #parameters# ");
		// }

		StringBuilder sb = new StringBuilder("");
		// for (Parameter parameter : parameters) {
		// sb.append(addParameter("linkUrl", parameter));
		// }
		return "getButtonOpenParameters";
	}

	public String[] getParametersDetail(Renderer link, String linkVar) {

		// StringBuilder refParameters = new StringBuilder("");
		// StringBuilder vars = new StringBuilder("");
		// StringBuilder openParameters = new StringBuilder("");
		// // '<a
		// //
		// href=\"javascript:#newWindowName#(#parameters#)\">#LabelText#</a>';
		// // '<a
		// //
		// href="javascript:openCreateDeptEmpLink('+record.get('dept3310_pk')+')">查询员工</a>';
		// List<Parameter> parameters = link.getParameters();
		// for (int i = 0; i < parameters.size(); i++) {
		// Parameter p = parameters.get(i);
		// refParameters.append("'+record.get('");
		// refParameters.append(p.getValue());
		// refParameters.append("')");
		// if (i == parameters.size() - 1) {
		// refParameters.append("+");
		// }
		// refParameters.append("'");
		// String key = "v" + i;
		// vars.append(key);
		// if (i < parameters.size() - 1) {
		// vars.append(",");
		// }
		// String op = addParameter(linkVar, p, key);
		// openParameters.append(op);
		// }
		// return new String[] { refParameters.toString(), vars.toString(),
		// openParameters.toString() };
		return new String[] { "getParametersDetail", "getParametersDetail",
				"getParametersDetail" };

		// StringBuilder sb = new StringBuilder("");
		// for (Parameter parameter : parameters) {
		// sb.append(addParameter("linkUrl", parameter));
		// }
		// script = script.replace("#parameters#", sb.toString());
		// return script;
		//
	}

	public String getButtonTargetDatasetID(final CompositeMap buttonMap) {
		CompositeMap clicker = buttonMap.getChild("ButtonClicker");
		CompositeMap childByAttrib = clicker.getChildByAttrib("comment",
				"target");
		if (childByAttrib == null) {
			return "";
		}
		final String refID = childByAttrib.getString("referenceid", "");

		uipMap.iterate(new IterationHandle() {

			@Override
			public int process(CompositeMap map) {
				if (refID.equals(map.getString("markid"))) {
					CompositeMap child = map.getChild("Dataset");
					String id = child.getString("ds_id", "");
					buttonMap.put("ds_id", id);
					return IterationHandle.IT_BREAK;
				}
				return IterationHandle.IT_CONTINUE;
			}
		}, false);
		return buttonMap.getString("ds_id", "");
	}
	public void debug(Object o) throws StopRunningException{
		System.out.println(o);
		throw new StopRunningException();
	}
}
