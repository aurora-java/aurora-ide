package aurora.ide.meta.gef.designer.model;

import aurora.ide.meta.gef.designer.DataType;

public class PkRecord extends Record {

	public PkRecord() {
		super();
		setName("default_pk_name");// this name should be reset
		setPrompt("primary-key");
		setType(DataType.BIGNIT.getDisplayType());
		put(COLUMN_QUERYFIELD, true);
		put(COLUMN_QUERY_OP, OP_EQ);
		setForInsert(true);
		setForUpdate(false);
		// lov
		setForDisplay(true);
		setForQuery(false);
		setForLov(true);
	}
}
