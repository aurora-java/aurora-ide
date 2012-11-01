package aurora.ide.views.wizard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.template.BMBindComponent;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.ButtonComponent;
import aurora.ide.meta.gef.editors.template.Component;
import aurora.ide.meta.gef.editors.template.Template;

public class ScreenGenerator {
	private CompositeMap initProcMap = newMap("init-procedure");
	private CompositeMap scriptMap = newMap("script", null);
	private CompositeMap dataSetsMap = newMap("dataSets");

	StringBuilder scriptTemp = new StringBuilder();
	private Template template;
	private HashMap<String, BMReference> bmrMap = new HashMap<String, BMReference>();
	private HashMap<String, Component> comMap = new HashMap<String, Component>();

	public CompositeMap gen(Template view) {
		this.template = view;
		extractBMReference();
		extractComponent();
		CompositeMap scrMap = newMap("screen", "a",
				AuroraConstant.ApplicationUri);
		scrMap.addChild(initProcMap);
		CompositeMap viewMap = newMap("view");
		scrMap.addChild(viewMap);
		viewMap.addChild(scriptMap);
		viewMap.addChild(dataSetsMap);
		for (Component ac : view.getChildren()) {
			CompositeMap m = createMapFromCom(ac);
			viewMap.addChild(m);
		}
		// ------
		String str = scriptTemp.toString();
		str = str.replace("\n", "\n\t\t\t") + "\n\t\t";
		scriptMap.setText(str);
		return scrMap;
	}

	private void extractBMReference() {
		List<BMReference> list = template.getBms();
		if (list != null) {
			for (BMReference bm : list) {
				bmrMap.put(bm.getId(), bm);
			}
		}
		list = template.getLinkBms();
		if (list != null) {
			for (BMReference bm : list) {
				bmrMap.put(bm.getId(), bm);
			}
		}
	}

	/**
	 * travel template tree,make a <i>id->component</i> relation map
	 */
	private void extractComponent() {
		LinkedList<Component> queue = new LinkedList<Component>();
		queue.add(template);
		while (!queue.isEmpty()) {
			Component c = queue.remove(0);
			String id = c.getId();
			if (id != null && id.length() > 0)
				comMap.put(id, c);
			if (c.getChildren() != null)
				queue.addAll(c.getChildren());
		}
	}

	private CompositeMap createMapFromCom(Component ac2) {
		String type = ac2.getComponentType().toLowerCase();
		if ("grid".equals(type)) {
			return createMapFromGrid(ac2);
		} else if (ac2 instanceof ButtonComponent) {
			return createMapFromButton((ButtonComponent) ac2);
		} else if (oneOf(type, "form", "fieldset")) {
			return createMapFromBox(ac2);
		} else if ("tabpanel".equals(type)) {
			return createMapFromTabPanel(ac2);
		}
		CompositeMap map = newMap(ac2.getComponentType());
		if (ac2.getChildren() != null) {
			for (Component ac : ac2.getChildren()) {
				CompositeMap m = createMapFromCom(ac);
				map.addChild(m);
			}
		}
		return map;
	}

	private CompositeMap createMapFromTabPanel(Component tp) {
		CompositeMap tpMap = newMap(tp.getComponentType());
		CompositeMap tabsMap = newMap("tabs");
		tpMap.addChild(tabsMap);
		for (Component c : tp.getChildren()) {
			tabsMap.addChild(createMapFromCom(c));
		}
		return tpMap;
	}

	private CompositeMap createMapFromButton(ButtonComponent ac2) {
		CompositeMap btnMap = newMap("button");
		String type = ac2.getType();
		if ("toolBar".equals(ac2.getParent().getComponentType())) {
			btnMap.put("type", type);
		} else {
			btnMap.put("text", ac2.getText());
			String id = ac2.getTarget();
			Component c = comMap.get(id);
			if (c != null) {
				if (ButtonClicker.B_SEARCH.equals(type)) {
					String bmrID = ((BMBindComponent) c).getBmReferenceID();
					BMReference bm = bmrMap.get(bmrID);
					if (bm != null) {
						String ds_id = getDataSetId(bm.getModel(), false);
						String fName = "query_" + ds_id;
						String function = "\nfunction %s() {\n\t$('%s').query();\n}";
						scriptTemp
								.append(String.format(function, fName, ds_id));
						btnMap.put("click", fName);
					}
				} else if (ButtonClicker.B_RESET.equals(type)) {
					BMBindComponent bmbc = getQueryOwner(c);
					if (bmbc != null) {
						String bmrID = bmbc.getBmReferenceID();
						BMReference bm = bmrMap.get(bmrID);
						if (bm != null) {
							String ds_id = getDataSetId(bm.getModel(), true);
							String fName = "reset_" + ds_id;
							String function = "\nfunction %s() {\n\t$('%s').reset();\n}";
							scriptTemp.append(String.format(function, fName,
									ds_id));
							btnMap.put("click", fName);
						}
					}
				}
			}

		}

		return btnMap;
	}

	private CompositeMap createMapFromBox(Component ac2) {
		CompositeMap map = newMap(ac2.getComponentType());
		String type = ac2.getComponentType().toLowerCase();
		if (oneOf(type, "field", "form")) {
			map.put("column", 3);
		}
		if (ac2 instanceof BMBindComponent) {
			BMBindComponent bmbc = (BMBindComponent) ac2;
			String bmrID = bmbc.getBmReferenceID();
			BMReference bm = getBMReference(bmrID);
			if (bm != null) {
				fillBoxArea(map, bm, false);
			}
		} else if (ac2.getId() != null) {
			BMBindComponent queryOwner = getQueryOwner(ac2);
			if (queryOwner != null) {
				String bmrID = queryOwner.getBmReferenceID();
				BMReference bm = getBMReference(bmrID);
				if (bm != null) {
					fillBoxArea(map, bm, true);
				}
			}
		}
		return map;
	}

	private void fillBoxArea(CompositeMap boxMap, BMReference bm, boolean query) {
		IFile file = bm.getModel();
		if (file != null) {
			BMCompositeMap bmc = new BMCompositeMap(file);
			CompositeMap ds = createDataSet(bmc, file, query);
			List<CompositeMap> fields = query ? bmc.getQueryFields() : bmc
					.getFieldsWithoutPk();
			String pkName = bmc.getPkFieldName();
			for (CompositeMap f : fields) {
				if (pkName != null && pkName.equals(f.get("field")))
					continue;
				if (f.getString("field") != null) {
					f = bmc.getFieldByName(f.getString("field"));
				}
				String inputType = getRecommendEditor(f);
				CompositeMap input = newMap(inputType);
				input.put("bindTarget", ds.getString("id"));
				input.put("name", f.getString("name"));
				input.put("prompt", f.getString("prompt"));
				boxMap.addChild(input);
			}
			dataSetsMap.addChild(ds);
		}
	}

	private String getRecommendEditor(CompositeMap f) {
		String inputType = null;
		String dataType = BMCompositeMap.getMapAttribute(f, "datatype");
		for (DataType dt : DataType.values()) {
			if (dt.getJavaType().equals(dataType)) {
				inputType = dt.getDefaultEditor();
				break;
			}
		}
		if (inputType == null)
			inputType = gussesEditor(f.getName());
		return inputType;
	}

	/**
	 * find whitch {@code Component} use <i>c</i> as query container
	 * 
	 * @param c
	 * @return
	 */
	private BMBindComponent getQueryOwner(Component c) {
		Component root = c;
		while (root.getParent() != null)
			root = root.getParent();
		LinkedList<Component> queue = new LinkedList<Component>();
		queue.add(root);
		while (!queue.isEmpty()) {
			Component com = queue.remove(0);
			if (com.getChildren() != null) {
				queue.addAll(com.getChildren());
			}
			if (com instanceof BMBindComponent) {
				BMBindComponent bmbc = (BMBindComponent) com;
				String queryContainer = bmbc.getQueryComponent();
				if (queryContainer != null && queryContainer.equals(c.getId())) {
					return bmbc;
				}
			}
		}
		return null;
	}

	private CompositeMap createMapFromGrid(Component ac2) {
		CompositeMap map = newMap(ac2.getComponentType());
		map.put("width", "800");
		map.put("height", "400");
		if (ac2.getChildren() != null) {
			for (Component c : ac2.getChildren()) {
				CompositeMap m = createMapFromCom(c);
				map.addChild(m);
			}
		}
		if (ac2 instanceof BMBindComponent) {
			BMBindComponent bmbc = (BMBindComponent) ac2;
			String bmrID = bmbc.getBmReferenceID();
			BMReference bm = getBMReference(bmrID);
			if (bm != null) {
				IFile file = bm.getModel();
				BMCompositeMap bmc = new BMCompositeMap(file);
				fillGridColumns(map, bmc, bm);
				// --bind dataset
				CompositeMap ds = createDataSet(bmc, file, false);
				map.put("bindTarget", ds.getString("id"));
				// -- set query ds (or bindTarget)of dataset
				Component queryComponent = comMap.get(bmbc.getQueryComponent());
				if (queryComponent instanceof BMBindComponent) {
					bm = getBMReference(((BMBindComponent) queryComponent)
							.getBmReferenceID());
					if (bm != null) {
						file = bm.getModel();
						ds.put("bindTarget", getDataSetId(file, false));
						ds.put("bindName",
								ds.getString("model").replace('.', '_'));
					}
				} else if (queryComponent != null)
					ds.put("queryDataSet", getDataSetId(file, true));
				dataSetsMap.addChild(ds);
			}
		}
		return map;
	}

	private void fillGridColumns(CompositeMap gridMap, BMCompositeMap bmc,
			BMReference bm) {
		CompositeMap cols = newMap("columns");

		CompositeMap pkField = bmc.getFieldOfPk();
		List<CompositeMap> fields = bmc.getFields(false, true);
		if ("java.lang.String".equals(BMCompositeMap.getMapAttribute(pkField,
				"datatype"))) {
			fields.add(0, pkField);
		}
		HashMap<String, String> editorMapping = new HashMap<String, String>();
		boolean editable = oneOf(template.getType(), "update", "create");
		for (CompositeMap f : fields) {
			CompositeMap col = newMap("column");
			col.put("name", f.getString("name"));
			if (editable) {
				String editor = getRecommendEditor(f);
				String editorId = editorMapping.get(editor);
				if (editorId == null) {
					editorId = editor + "_id";
					editorMapping.put(editor, editorId);
				}
				col.put("editor", editorId);
			}
			col.put("prompt", f.getString("prompt"));
			col.put("width", 100);
			cols.addChild(col);
		}
		gridMap.addChild(cols);
		if (editable) {
			CompositeMap editorsMap = newMap("editors");
			for (String s : editorMapping.keySet()) {
				CompositeMap eMap = newMap(s);
				eMap.put("id", editorMapping.get(s));
				editorsMap.addChild(eMap);
			}
			gridMap.addChild(editorsMap);
		}
	}

	private String getDataSetId(IFile file, boolean query) {
		String fileName = file.getName();
		int idx = fileName.lastIndexOf('.');
		fileName = fileName.substring(0, idx);
		return fileName + (query ? "_query_ds" : "_result_ds");
	}

	private CompositeMap createDataSet(BMCompositeMap bmc, IFile file,
			boolean query) {
		CompositeMap dsMap = newMap("dataSet");
		dsMap.put("id", getDataSetId(file, query));
		if (query) {
			dsMap.put("autoCreate", true);
		} else {
			String pkg = ResourceUtil.getBmPkgPath(file);
			dsMap.put("model", pkg);
		}
		// -----
		CompositeMap fields = newMap("fields");
		List<CompositeMap> list = query ? bmc.getQueryFields() : bmc.getFields(
				true, true);
		for (CompositeMap f : list) {
			CompositeMap m = newMap("field");
			if (f.getString("field") != null) {
				f = bmc.getFieldByName(f.getString("field"));
			}
			m.put("name", f.get("name"));
			fields.addChild(m);
		}
		dsMap.addChild(fields);
		// -----
		return dsMap;
	}

	private BMReference getBMReference(String bmrID) {
		return bmrMap.get(bmrID);
	}

	private String gussesEditor(String fieldName) {
		if (fieldName != null && fieldName.contains("date"))
			return DataType.DATE.getDefaultEditor();
		return DataType.TEXT.getDefaultEditor();
	}

	private boolean oneOf(String base, String... target) {
		return Arrays.asList(target).contains(base);
	}

	/**
	 * create CompositeMap with <i>[name,[prefix,[namespaceURI]]]</i>
	 * 
	 * @param para
	 *            default prefix is {@code "a"}
	 * @return
	 */
	public CompositeMap newMap(String... para) {
		CompositeMap m = new CommentCompositeMap();
		if (para == null || para.length == 0)
			return m;
		m.setName(para[0]);
		if (para.length > 1)
			m.setPrefix(para[1]);
		else
			m.setPrefix("a");
		if (para.length > 2)
			m.setNameSpaceURI(para[2]);
		return m;
	}
}
