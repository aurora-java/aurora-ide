package aurora.ide.meta.gef.designer.model;

import aurora.ide.meta.gef.designer.IDesignerConst;

public class Relation extends Record implements IDesignerConst {
	public String getName() {
		return getStringNotNull(COLUMN_RELNAME);
	}

	public void setName(String name) {
		put(COLUMN_RELNAME, name);
	}

	public String getLocalField() {
		return getStringNotNull(COLUMN_LOCFIELD);
	}

	public void setLocalField(String f) {
		put(COLUMN_LOCFIELD, f);
	}

	public String getSrcField() {
		return getStringNotNull(COLUMN_SRCFIELD);
	}

	public void setSrcField(String f) {
		put(COLUMN_SRCFIELD, f);
	}

	public String getJoinType() {
		return getStringNotNull(COLUMN_JOINTYPE);
	}

	public void setJoinType(String t) {
		put(COLUMN_JOINTYPE, t);
	}

	public String getRefTable() {
		return getStringNotNull(COLUMN_REFMODEL);
	}

	public void setRefTable(String table) {
		put(COLUMN_REFMODEL, table);
	}
}