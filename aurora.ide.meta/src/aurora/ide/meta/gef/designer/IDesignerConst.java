package aurora.ide.meta.gef.designer;

import aurora.ide.meta.gef.designer.editor.BMModelViewer;

public interface IDesignerConst {
	// modelviewer column properties
	String COLUMN_NUM = "no.";
	String COLUMN_PROMPT = "prompt";
	String COLUMN_TYPE = "type";
	String COLUMN_NAME = "name";
	String COLUMN_EDITOR = "editor";
	String COLUMN_QUERYFIELD = "queryfield";
	String COLUMN_ISFOREIGN = "foreign";
	String COLUMN_QUERY_OP = "query_op";

	String[] TABLE_COLUMN_PROPERTIES = { "", COLUMN_NUM, COLUMN_PROMPT,
			COLUMN_TYPE, COLUMN_NAME, COLUMN_EDITOR, COLUMN_QUERYFIELD,
			COLUMN_QUERY_OP };
	// relationviewer column properties
	String COLUMN_RELNAME = "rel_name";
	String COLUMN_REFMODEL = "ref_model";
	String COLUMN_LOCFIELD = "loc_field";
	String COLUMN_SRCFIELD = "src_field";
	String COLUMN_JOINTYPE = "join_type";
	String[] COLUMN_PROPERTIES = { "", BMModelViewer.COLUMN_NUM,
			COLUMN_RELNAME, COLUMN_REFMODEL, COLUMN_LOCFIELD, COLUMN_SRCFIELD,
			COLUMN_JOINTYPE };

	// query operator
	String OP_EQ = "=";
	String OP_GT = ">";
	String OP_GE = ">=";
	String OP_LT = "<";
	String OP_LE = "<=";
	String OP_LIKE = "like";
	String OP_PRE_MATCH = "(前端匹配)";
	String OP_END_MATCH = "(末端匹配)";
	String OP_ANY_MATCH = "(部分匹配)";
	String OP_INTERVAL = "(闭区间)";
	String[] OPERATORS = { OP_EQ, OP_GT, OP_GE, OP_LT, OP_LE, OP_LIKE,
			OP_PRE_MATCH, OP_END_MATCH, OP_ANY_MATCH, OP_INTERVAL };
	// data type
	String TEXT = "text";
	String LONG_TEXT = "long text";
	String INTEGER = "integer";
	String FLOAT = "float";
	String DATE = "date";
	String DATE_TIME = "dateTime";
	String[] data_types = { TEXT, LONG_TEXT, INTEGER, FLOAT, DATE, DATE_TIME };
}
