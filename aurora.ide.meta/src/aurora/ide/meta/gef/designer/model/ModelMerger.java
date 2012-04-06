package aurora.ide.meta.gef.designer.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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
import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.gen.BaseBmGenerator;
import aurora.ide.meta.gef.editors.source.gen.DataSetFieldUtil;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.cache.CacheManager;

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
		classFolder = ResourceUtil.getWebInf(aProject).getFolder("classes");
		modelFolder = amProject.getModelFolder();
		bmFile = getBMFile();
		if (bmFile != null && bmFile.exists())
			bmMap = CacheManager.getCompositeMap(bmFile);
		CompositeMap map = new CompositeMapParser(new CommentCompositeLoader())
				.parseStream(file.getContents());
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
		if (bmMap != null)
			updateModel();
		return model;
	}

	/**
	 * when read
	 */
	private void updateModel() {
		model.setTitle(bmMap.getString(BMModel.TITLE));
		updateRecords();
		updateRelations();
	}

	/**
	 * when read
	 */
	@SuppressWarnings("unchecked")
	private void updateRecords() {
		CompositeMap fieldsMap = bmMap.getChild("fields");
		if (fieldsMap == null) {
			model.removeAll();
			return;
		}
		LinkedList<CompositeMap> list = (LinkedList<CompositeMap>) fieldsMap
				.getChildsNotNull();
		list = (LinkedList<CompositeMap>) list.clone();
		ArrayList<Record> records = model.getRecordList();
		for (int i = 0; i < records.size(); i++) {
			Record r = records.get(i);
			String prompt = r.getPrompt();
			CompositeMap m = null;
			for (CompositeMap mm : list) {
				if (prompt.equals(mm.getString("prompt"))) {
					m = mm;
					break;
				}
			}
			if (m == null) {
				model.remove(r);
				i--;
				continue;
			}
			r.setName(m.getString("name"));
			// r.setType(type);
			list.remove(m);
		}
		for (CompositeMap m : list) {
			model.add(createNewRecord(m));
		}
	}

	private Record createNewRecord(CompositeMap m) {
		Record r = new Record();
		r.setName(m.getString("name"));
		r.setPrompt(m.getString("prompt"));
		// TODO createNewRecord
		return r;
	}

	/**
	 * when read
	 */
	@SuppressWarnings("unchecked")
	private void updateRelations() {
		CompositeMap relMap = bmMap.getChild("relations");
		if (relMap == null) {
			model.removeAllRelations();
			return;
		}
		LinkedList<CompositeMap> list = (LinkedList<CompositeMap>) relMap
				.getChildsNotNull();
		list = (LinkedList<CompositeMap>) list.clone();
		ArrayList<Relation> relations = model.getRelationList();
		for (int i = 0; i < relations.size(); i++) {
			Relation r = relations.get(i);
			String name = r.getName();
			CompositeMap m = null;
			for (CompositeMap mm : list) {
				if (name.equals(mm.getString("name"))) {
					m = mm;
					break;
				}
			}
			if (m == null) {
				model.remove(r);
				i--;
				continue;
			}
			r.setJoinType(m.getString("joinType"));
			r.setRefTable(m.getString("refModel"));
			CompositeMap mm = m.getChild("reference");
			if (mm != null) {
				updateRefrence(mm, r);
			}
			list.remove(m);
		}
		for (CompositeMap m : list) {
			model.add(createNewRelation(m));
		}

	}

	private void updateRefrence(CompositeMap refMap, Relation r) {
		// update localField use new bm setting
		for (Record lr : model.getRelationList()) {
			if (lr.getName().equals(refMap.getString("localField"))) {
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
			if (lfm.getString("name").equals(refMap.getString("sourceField"))) {
				r.setSrcField(lfm.getString("prompt"));
				break;
			}
		}
	}

	private Relation createNewRelation(CompositeMap m) {
		Relation r = new Relation();
		r.setName(m.getString("name"));
		r.setJoinType(m.getString("joinType"));
		r.setRefTable(m.getString("refModel"));
		CompositeMap mm = m.getChild("reference");
		updateRefrence(mm, r);
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
		if (bmMap != null)
			updateBmMap();
		return bmMap;
	}

	private void updateBmMap() {
		bmMap.put(BMModel.TITLE, model.getTitle());
		CompositeMap fieldsMap = bmMap.getChild("fields");
		updateFields(fieldsMap);
		CompositeMap relMap = bmMap.getChild("relations");
		updateRelations(relMap);
	}

	@SuppressWarnings("unchecked")
	private void updateFields(CompositeMap fieldsMap) {
		List<CompositeMap> list = fieldsMap.getChildsNotNull();
		List<Record> records = (ArrayList<Record>) model.getRecordList()
				.clone();
		String pk_name = new DataSetFieldUtil().getPK(fieldsMap.getParent());
		for (int i = 0; i < list.size(); i++) {
			CompositeMap m = list.get(i);
			String prompt = m.getString("prompt");
			if (prompt == null || m.getString("name").equals(pk_name))
				continue;
			Record r = null;
			for (Record rr : records) {
				if (rr.getPrompt().equals(prompt)) {
					r = rr;
					break;
				}
			}
			if (r == null) {
				// field that not exists any more, will be remove
				fieldsMap.removeChild(m);
				continue;
			}
			// field that exists, will be update
			m.put("name", r.getName());
			DataType dt = DataType.fromString(r.getType());
			if (dt == null)
				dt = DataType.TEXT;
			m.put("databaseType", dt.getDbType());
			m.put("dataType", dt.getJavaType());
			m.put("defaultEditor", r.getString(IDesignerConst.COLUMN_EDITOR));
			records.remove(r);
		}
		// new fields will be add to bm
		for (Record r : records) {
			if (!r.getName().equals(pk_name))
				fieldsMap.addChild(getNewFieldMap(r));
		}
	}

	@SuppressWarnings("unchecked")
	private void updateRelations(CompositeMap relMap) {
		List<CompositeMap> list = relMap.getChildsNotNull();
		List<Relation> relations = (ArrayList<Relation>) model
				.getRelationList().clone();
		for (int i = 0; i < list.size(); i++) {
			CompositeMap m = list.get(i);
			String name = m.getString("name");
			Relation r = null;
			for (Relation rr : relations) {
				if (rr.getName().equals(name)) {
					r = rr;
					break;
				}
			}
			if (r == null) {
				relMap.remove(m);
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
		map.put("datatype", dt.getJavaType());
		map.put("defaultEditor", r.getString(IDesignerConst.COLUMN_EDITOR));
		map.put("prompt", r.getPrompt());
		return map;
	}

	private CompositeMap newCompositeMap(String name) {
		CompositeMap map = new CommentCompositeMap(name);
		map.setPrefix(BaseBmGenerator.bm_ns_pre);// set a default prefix
		return map;
	}
}
