package aurora.ide.meta.gef.designer.gen;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.ide.undo.CreateFileOperation;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.ModelMerger;
import aurora.ide.meta.gef.designer.model.ModelUtil;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.designer.model.Relation;
import aurora.ide.meta.gef.editors.models.Input;

public class BaseBmGenerator {
	public static final String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
	public static final String bm_ns_pre = "bm";
	public static final String bm_ns_uri = "http://www.aurora-framework.org/schema/bm";
	public static final String f_ns_pre = "f";
	public static final String f_ns_uri = "aurora.database.features";
	public static final String o_ns_pre = "o";
	public static final String o_ns_uri = "aurora.database.local.oracle";
	/**
	 * this will be used to generate relation`s refAlias<br/>
	 * currently , we assume that ,seqRefAlias will never greater than 'z'(there
	 * is no so much more relations)
	 */
	private char seqRefAlias = 'f';

	private BMModel model;
	private String name;
	private IProject aProject;
	private IFile file;
	private HashMap<String, String> nsMapping = new HashMap<String, String>();
	private ModelMerger merger;

	public BaseBmGenerator(IFile file) {
		this();
		name = file.getName();
		int idx = name.indexOf('.');
		if (idx != -1)
			name = name.substring(0, idx);
		this.file = file;
		merger = new ModelMerger(file);
		model = merger.getOriginalModel();
		aProject = merger.getAuroraProject();
	}

	public BaseBmGenerator() {
		nsMapping.put(bm_ns_uri, bm_ns_pre);
		nsMapping.put(o_ns_uri, o_ns_pre);
	}

	public void process() throws Exception {
		IFile bmFile = merger.getBMFile();
		CompositeMap bmMap = null;
		if (bmFile.exists()) {
			// System.out.println("bm '" + bmFile.getFullPath()
			// + "' already exists.");
			bmMap = merger.getMergedCompositeMap();
		}
		if (bmMap == null)
			bmMap = gen();
		createOrWriteFile(bmFile, bmMap);
		new ExtendBmGenerator(model, bmFile).gen();
	}

	protected void createOrWriteFile(IFile file, CompositeMap map)
			throws Exception {
		String xml = xml_header + map.toXML();
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		if (file.exists()) {
			file.setContents(is, IFile.FORCE, new NullProgressMonitor());
			is.close();
		} else {
			CreateFileOperation cfo = new CreateFileOperation(file, null, is,
					"write " + file.getFullPath());
			cfo.execute(new NullProgressMonitor(), null);
		}
	}

	private CompositeMap gen() {
		CompositeMap map = genModelMap();
		map.put("title", model.getTitle());
		Record r = model.getDefaultDisplayRecord();
		if (r != null)
			map.put("defaultDisplayField", r.getName());
		map.addChild(genFieldsMap());
		map.addChild(genPkMap());
		map.addChild(genFeatureMap());
		map.addChild(genRelationMap());
		return map;
	}

	protected CompositeMap genModelMap() {
		CompositeMap map = newCompositeMap("model");
		map.setNamespaceMapping(nsMapping);
		for (String uri : nsMapping.keySet()) {
			map.put("xmlns:" + nsMapping.get(uri), uri);
		}
		map.put("alias", "e");
		map.put("baseTable", name);
		return map;
	}

	private CompositeMap genFieldsMap() {
		CompositeMap fMap = newCompositeMap("fields");
		String pk_name = model.getPkRecord().getName();
		boolean haspk = false;
		for (Record r : model.getRecordList()) {
			CompositeMap m = getNewFieldMap(r);
			if (pk_name.equals(m.getString("name")))
				haspk = true;
			fMap.addChild(getNewFieldMap(r));
		}
		if (!haspk)
			fMap.addChild(0, genPkFieldMap());
		return fMap;
	}

	private CompositeMap genPkFieldMap() {
		CompositeMap pk = newCompositeMap("field");
		Record r = model.getPkRecord();
		pk.put("name", r.getName());
		pk.put("databaseType", "BIGINT");
		pk.put("dataType", "java.lang.Long");
		pk.put("prompt", "primary_key");
		return pk;
	}

	private CompositeMap getNewFieldMap(Record r) {
		CompositeMap map = newCompositeMap("field");
		map.put("name", r.getName());
		DataType dt = DataType.fromString(r.getType());
		if (dt == null)
			dt = DataType.TEXT;
		map.put("databaseType", dt.getDbType());
		map.put("dataType", dt.getJavaType());
		String editor = r.getString(IDesignerConst.COLUMN_EDITOR);
		map.put("defaultEditor", editor);
		map.put("prompt", r.getPrompt());
		if (Input.Combo.equals(editor) || Input.LOV.equals(editor)) {
			String options = r.getString(IDesignerConst.COLUMN_OPTIONS);
			map.put("options", options);
		}
		return map;
	}

	private CompositeMap genPkMap() {
		CompositeMap pkMap = newCompositeMap("primary-key");
		CompositeMap pk = newCompositeMap("pk-field");
		pk.put("name", model.getPkRecord().getName());
		pkMap.addChild(pk);
		return pkMap;
	}

	private CompositeMap genFeatureMap() {
		CompositeMap fMap = newCompositeMap("features");
		CompositeMap spk = newCompositeMap("sequence-pk");
		spk.setPrefix(o_ns_pre);
		fMap.addChild(spk);
		return fMap;
	}

	private CompositeMap genRelationMap() {
		CompositeMap rMap = newCompositeMap("relations");
		for (Relation r : model.getRelationList()) {
			rMap.addChild(getNewRelationMap(r));
		}
		return rMap;
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
		return Character.toString(seqRefAlias++);
	}

	protected CompositeMap newCompositeMap(String name) {
		CompositeMap map = new CommentCompositeMap(name);
		map.setPrefix(bm_ns_pre);// set a default prefix
		return map;
	}
}
