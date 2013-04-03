package aurora.ide.meta.gef.designer.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import aurora.ide.api.composite.map.CommentCompositeLoader;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.gen.BaseBmGenerator;
import aurora.ide.meta.gef.editors.source.gen.DataSetFieldUtil;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.plugin.source.gen.screen.model.Input;

public class ModelMerger {
	private IFile file;
	private IFile bmFile;
	private AuroraMetaProject amProject;
	private IProject aProject;
	private IFolder classFolder;
	private IFolder modelFolder;
	private CompositeMap bmMap;
	private BMModel model;
	private char seqRefAlias = 'p';
	/**
	 * use to mark weather model if changed by bm file ,when read<br/>
	 * commonly ,this will happen when user change bm file manually.
	 */
	protected boolean dirty = false;

	/**
	 * 
	 * @param file
	 *            model prototype file
	 */
	public ModelMerger(IFile file) {
		this.file = file;
		try {
			init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void init() throws ResourceNotFoundException, CoreException,
			ApplicationException, SAXException, IOException {
		amProject = new AuroraMetaProject(file.getProject());
		aProject = amProject.getAuroraProject();
		classFolder = ResourceUtil.getBMHomeFolder(aProject);
		modelFolder = amProject.getModelFolder();
		bmFile = getBMFile();
	}

	public void initModel() throws SAXException, IOException, CoreException {
		CompositeMapParser parser = new CompositeMapParser(
				new CommentCompositeLoader());
		if (bmFile != null && bmFile.exists())
			bmMap = parser.parseStream(bmFile.getContents(true));
		CompositeMap map = parser.parseStream(file.getContents());
		model = ModelUtil.fromCompositeMap(map);
	}

	public IFile getBMFile() {
		if (bmFile == null) {
			IPath modelPath = file.getFullPath().makeRelativeTo(
					modelFolder.getFullPath());
			IPath bmPath = modelPath.removeFileExtension().addFileExtension(
					"bm");
			bmPath = classFolder.getFullPath().append(bmPath);
			bmFile = aProject.getParent().getFile(bmPath);
		}
		return bmFile;
	}

	public IFolder getClassFolder() {
		return classFolder;
	}

	public IFolder getModelFolder() {
		return modelFolder;
	}

	public IProject getAuroraProject() {
		return aProject;
	}

	public BMModel getOriginalModel() {
		return model;
	}

	public CompositeMap getOriginalBMMap() {
		return bmMap;
	}

	/**
	 * read model from bmq file<br/>
	 * the try to update model by it`s mirror bm
	 * 
	 * @return
	 */
	public BMModel getMergedModel() {
		// PropertyChangeListener pcl = new PropertyChangeListener() {
		//
		// public void propertyChange(PropertyChangeEvent evt) {
		// // System.out.println(evt.getSource() + ":"
		// // + evt.getPropertyName() + "," + evt.getOldValue()
		// // + "->" + evt.getNewValue());
		// dirty = true;
		// }
		// };
		// model.addPropertyChangeListener(pcl);
		// if (bmMap != null)
		// updateModel();
		// model.removePropertyChangeListener(pcl);
		model.setNamePrefix(ModelUtil.computeNamePrefix(file));
		return model;
	}

	/**
	 * after {@link #getMergedModel()},user can use this method to know weather
	 * model is modified.
	 * 
	 * @return
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * when read,use to bm to update bmmodel
	 */
	private void updateModel() {
		model.setTitle(bmMap.getString(BMModel.TITLE));
		BMCompositeMap bmc = new BMCompositeMap(bmMap);
		updateRecordsOfModel(model, bmc);
		updateRelationsOfModel(model, bmc);
		String ddf = getMapString(bmMap, "defaultDisplayField");
		for (Record r : model.getRecordList()) {
			if (r.getName().equals(ddf))
				model.setDefaultDisplay(r.getPrompt());
		}
	}

	/**
	 * when read,use bm to update bmmodel (record)
	 */
	private void updateRecordsOfModel(BMModel model, BMCompositeMap bmc) {
		CompositeMap fieldsMap = bmc.getFieldsMap();
		if (fieldsMap == null) {
			model.removeAll();
			return;
		}
		List<CompositeMap> list = new ArrayList<CompositeMap>(bmc.getFields());
		ArrayList<Record> records = model.getRecordList();
		for (int i = 0; i < records.size(); i++) {
			Record r = records.get(i);
			String prompt = r.getPrompt();
			CompositeMap m = searchCmList(list, "prompt", prompt);
			if (m == null) {
				model.remove(r);
				i--;
				continue;
			}
			updateRecordUseCompositeMap(r, m);
			list.remove(m);
		}
		CompositeMap pkf = bmc.getFieldOfPk();
		if (pkf != null) {
			Record r = model.getPkRecord();
			updateRecordUseCompositeMap(r, pkf);
		}
		for (CompositeMap m : list) {
			if (m.getString("name").equals(model.getPkRecord().getName()))
				continue;
			model.add(createNewRecord(m));
		}
	}

	private void updateRecordUseCompositeMap(Record r, CompositeMap fMap) {
		r.setName(fMap.getString("name"));
		r.setPrompt(fMap.getString("prompt"));
		String code = getMapString(fMap, "lookupCode");
		if (code == null || code.length() == 0)
			r.setOptions(fMap.getString("options"));
		else
			r.setOptions(code);
		r.setEditor(getMapString(fMap, "defaultEditor"));
		// TODO update other property of record when read
	}

	private Record createNewRecord(CompositeMap m) {
		Record r = new Record();
		updateRecordUseCompositeMap(r, m);
		return r;
	}

	/**
	 * when read,use bm to update bmmodel (relations)
	 */
	private void updateRelationsOfModel(BMModel model, BMCompositeMap bmc) {
		CompositeMap relMap = bmc.getRelationsMap();
		if (relMap == null) {
			model.removeAllRelations();
			return;
		}
		List<CompositeMap> list = new ArrayList<CompositeMap>(
				bmc.getRelations());
		ArrayList<Relation> relations = model.getRelationList();
		for (int i = 0; i < relations.size(); i++) {
			Relation r = relations.get(i);
			String name = r.getName();
			CompositeMap m = searchCmList(list, "name", name);
			if (m == null) {
				model.remove(r);
				i--;
				continue;
			}
			r.setJoinType(getMapString(m, "joinType"));
			r.setRefTable(getMapString(m, "refModel"));
			CompositeMap mm = m.getChild("reference");
			if (mm != null) {
				updateReferenceOfModel(mm, r);
			}
			list.remove(m);
		}
		for (CompositeMap m : list) {
			model.add(createNewRelation(m));
		}
	}

	private void updateReferenceOfModel(CompositeMap refMap, Relation r) {
		// update localField use new bm setting
		for (Record lr : model.getRelationList()) {
			if (lr.getName().equals(getMapString(refMap, "localField"))) {
				r.setLocalField(lr.getPrompt());
				break;
			}
		}
		// update sourceField use extern bm setting
		DataSetFieldUtil dsfu = new DataSetFieldUtil(file.getProject(), "",
				r.getRefTable());
		ArrayList<CompositeMap> locField = dsfu.getLocalFields(dsfu.getBmMap(),
				false);
		for (CompositeMap lfm : locField) {
			if (lfm.getString("name").equals(
					getMapString(refMap, "sourceField"))) {
				r.setSrcField(lfm.getString("prompt"));
				break;
			}
		}
	}

	private Relation createNewRelation(CompositeMap m) {
		Relation r = new Relation();
		r.setName(m.getString("name"));
		r.setJoinType(getMapString(m, "joinType"));
		r.setRefTable(getMapString(m, "refModel"));
		CompositeMap mm = m.getChild("reference");
		updateReferenceOfModel(mm, r);
		return r;
	}

	/**
	 * when write<br/>
	 * use model to update exists bm compositemap <br>
	 * if bm file not exists ,reutrns null
	 * 
	 * @return
	 */
	public CompositeMap getMergedCompositeMap() {
		// if (bmMap != null)
		// updateBmMap(bmMap, model);
		return bmMap;
	}

	private void updateBmMap(CompositeMap bmMap, BMModel model) {
		BMCompositeMap bmc = new BMCompositeMap(bmMap);
		bmMap.put(BMModel.TITLE, model.getTitle());
		Record r = model.getDefaultDisplayRecord();
		if (r != null)
			bmMap.put("defaultDisplayField", r.getName());
		updateFieldsOfBm(bmc, model);
		updateRelationsOfBm(bmc, model);
	}

	private void updateFieldsOfBm(BMCompositeMap bmc, BMModel model) {
		CompositeMap fieldsMap = bmc.getFieldsMap();
		List<CompositeMap> list = new ArrayList<CompositeMap>(bmc.getFields());
		List<Record> records = new ArrayList<Record>(model.getRecordList());
		String pk_name = bmc.getPkFieldName();
		for (int i = 0; i < list.size(); i++) {
			CompositeMap m = list.get(i);
			String prompt = m.getString("prompt");
			if (prompt == null || m.getString("name").equals(pk_name))
				continue;
			Record r = searchReList(records, "prompt", prompt);
			if (r == null) {
				// field that not exists any more, will be remove
				fieldsMap.removeChild(m);
				continue;
			}
			// field that exists, will be update
			updateFieldUseRecord(m, r);
			records.remove(r);
		}
		// new fields will be add to bm
		for (Record r : records) {
			fieldsMap.addChild(getNewFieldMap(r));
		}
		updatePk(model, bmc);
	}

	/**
	 * when write ,update pk of bm<br/>
	 * 
	 * @param model
	 * @param bmc
	 */
	private void updatePk(BMModel model, BMCompositeMap bmc) {
		Record r = model.getPkRecord();
		CompositeMap pkfMap = bmc.getFieldOfPk();
		if (pkfMap != null) {
			pkfMap.put("name", r.getName());
		}
		CompositeMap pkf = bmc.getFirstPkField();
		if (pkf != null)
			pkf.put("name", r.getName());

	}

	private void updateFieldUseRecord(CompositeMap m, Record r) {
		m.put("name", r.getName());
		DataType dt = DataType.fromString(r.getType());
		if (dt == null)
			dt = DataType.TEXT;
		m.put("databaseType", dt.getDbType());
		m.put("dataType", dt.getJavaType());
		String editor = r.getString(IDesignerConst.COLUMN_EDITOR);
		m.put("defaultEditor", editor);
		if (editor.equals(Input.Combo) || editor.equals(Input.LOV)) {
			String options = r.getString(IDesignerConst.COLUMN_OPTIONS);
			if (dt.getDisplayType().equalsIgnoreCase(IDesignerConst.LOOKUPCODE)) {
				m.put("lookupCode", options);
				m.put("lookupField", r.getName() + "_lookup");
			} else
				m.put("options", options);
		}
	}

	private void updateRelationsOfBm(BMCompositeMap bmc, BMModel model) {
		CompositeMap relMap = bmc.getRelationsMap();
		List<CompositeMap> list = new ArrayList<CompositeMap>(
				bmc.getRelations());
		List<Relation> relations = new ArrayList<Relation>(
				model.getRelationList());
		for (int i = 0; i < list.size(); i++) {
			CompositeMap m = list.get(i);
			String name = m.getString("name");
			Relation r = searchReList(relations, "name", name);
			if (r == null) {
				relMap.removeChild(m);
				continue;
			}
			m.put("joinType", r.getJoinType());
			m.put("refModel", r.getRefTable());
			CompositeMap rm = m.getChild("reference");
			rm.put("localField",
					ModelUtil.getLocalFieldName(model, r.getLocalField()));
			rm.put("foreignField",
					ModelUtil.getForeignFieldName(file.getProject(),
							r.getSrcField(), r.getRefTable()));
			relations.remove(r);
		}
		for (Relation r : relations) {
			relMap.addChild(getNewRelationMap(r));
		}
	}

	private CompositeMap getNewRelationMap(Relation r) {
		CompositeMap map = newCompositeMap("relation");
		map.put("name", r.getName());
		map.put("refModel", r.getRefTable());
		map.put("joinType", r.getJoinType());
		map.put("refAlias", getRefAlias(r));
		CompositeMap m = newCompositeMap("reference");
		m.put("localField",
				ModelUtil.getLocalFieldName(model, r.getLocalField()));
		m.put("foreignField",
				ModelUtil.getForeignFieldName(file.getProject(),
						r.getSrcField(), r.getRefTable()));
		map.addChild(m);
		return map;
	}

	private String getRefAlias(Relation r) {
		return "rel_" + Character.toString(seqRefAlias++);
	}

	private CompositeMap getNewFieldMap(Record r) {
		CompositeMap map = newCompositeMap("field");
		map.put("name", r.getName());
		DataType dt = DataType.fromString(r.getType());
		if (dt == null)
			dt = DataType.TEXT;
		map.put("databaseType", dt.getDbType());
		map.put("dataType", dt.getJavaType());
		map.put("defaultEditor", r.getString(IDesignerConst.COLUMN_EDITOR));
		map.put("prompt", r.getPrompt());
		return map;
	}

	private CompositeMap newCompositeMap(String name) {
		CompositeMap map = new CommentCompositeMap(name);
		map.setPrefix(BaseBmGenerator.bm_ns_pre);// set a default prefix
		return map;
	}

	private String getMapString(CompositeMap map, String attr) {
		String value = BMCompositeMap.getMapAttribute(map, attr);
		return value == null ? "" : value;
	}

	/**
	 * search in a CompositeMap list,find a CompositeMap use givven
	 * property,value
	 * 
	 * @param list
	 * @param property
	 * @param value
	 * @return
	 */
	private CompositeMap searchCmList(List<CompositeMap> list, String property,
			String value) {
		for (CompositeMap m : list) {
			if (value.equals(m.getString(property)))
				return m;
		}
		return null;
	}

	/**
	 * search in a Record (Relation) list find a Record (Relation) use givven
	 * property ,value
	 * 
	 * @param list
	 * @param property
	 * @param value
	 * @return
	 */
	private <T extends Record> T searchReList(List<T> list, String property,
			String value) {
		for (T r : list) {
			if (value.equals(r.getString(property)))
				return r;
		}
		return null;
	}
}
