package aurora.ide.meta.gef.designer.model;

import aurora.ide.meta.gef.designer.editor.RelationViewer;

public class Relation extends Record {
	public String getName() {
		return getStringNotNull(RelationViewer.COLUMN_RELNAME);
	}

	public void setName(String name) {
		put(RelationViewer.COLUMN_RELNAME, name);
	}

	public String getLocalField() {
		return getStringNotNull(RelationViewer.COLUMN_LOCFIELD);
	}

	public void setLocalField(String f) {
		put(RelationViewer.COLUMN_LOCFIELD, f);
	}

	public String getSrcField() {
		return getStringNotNull(RelationViewer.COLUMN_SRCFIELD);
	}

	public void setSrcField(String f) {
		put(RelationViewer.COLUMN_SRCFIELD, f);
	}

	public String getJoinType() {
		return getStringNotNull(RelationViewer.COLUMN_JOINTYPE);
	}

	public void setJoinType(String t) {
		put(RelationViewer.COLUMN_JOINTYPE, t);
	}

	public String getRefTable() {
		return getStringNotNull(RelationViewer.COLUMN_REFMODEL);
	}

	public void setRefTable(String table) {
		put(RelationViewer.COLUMN_REFMODEL, table);
	}
}