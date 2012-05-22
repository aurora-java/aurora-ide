package aurora.ide.meta.gef.designer;

import aurora.ide.meta.gef.designer.model.Relation;

public interface IDesignerConst {
	String EXTENSION = "bmq"; //$NON-NLS-1$
	// modelviewer column properties
	String COLUMN_NUM = "no."; //$NON-NLS-1$
	String COLUMN_PROMPT = "prompt"; //$NON-NLS-1$
	String COLUMN_TYPE = "type"; //$NON-NLS-1$
	String COLUMN_NAME = "name"; //$NON-NLS-1$
	String COLUMN_EDITOR = "editor"; //$NON-NLS-1$
	String COLUMN_QUERYFIELD = "queryfield"; //$NON-NLS-1$
	String COLUMN_ISFOREIGN = "foreign"; //$NON-NLS-1$
	String COLUMN_QUERY_OP = "query_op"; //$NON-NLS-1$
	String COLUMN_OPTIONS = "options"; //$NON-NLS-1$

	String[] TABLE_COLUMN_PROPERTIES = { "", COLUMN_NUM, COLUMN_PROMPT, //$NON-NLS-1$
			COLUMN_TYPE, COLUMN_NAME, COLUMN_EDITOR, COLUMN_QUERYFIELD,
			COLUMN_OPTIONS };
	// relationviewer column properties
	String COLUMN_RELNAME = "rel_name"; //$NON-NLS-1$
	String COLUMN_REFMODEL = "ref_model"; //$NON-NLS-1$
	String COLUMN_LOCFIELD = "loc_field"; //$NON-NLS-1$
	String COLUMN_SRCFIELD = "src_field"; //$NON-NLS-1$
	String COLUMN_JOINTYPE = "join_type"; //$NON-NLS-1$
	String[] COLUMN_PROPERTIES = { "", COLUMN_NUM, //$NON-NLS-1$
			COLUMN_RELNAME, COLUMN_REFMODEL, COLUMN_LOCFIELD, COLUMN_SRCFIELD,
			COLUMN_JOINTYPE, Relation.REF_PROMPTS };

	String[] JOIN_TYPES = new String[] { "LEFT OUTER", "RIGHT OUTER",
			"FULL OUTER", "INNER", "CROSS" };

	// query operator
	String OP_EQ = "="; //$NON-NLS-1$
	String OP_GT = ">"; //$NON-NLS-1$
	String OP_GE = ">="; //$NON-NLS-1$
	String OP_LT = "<"; //$NON-NLS-1$
	String OP_LE = "<="; //$NON-NLS-1$
	String OP_LIKE = "like"; //$NON-NLS-1$
	String OP_PRE_MATCH = DesignerMessages.IDesignerConst_0;
	String OP_END_MATCH = DesignerMessages.IDesignerConst_1;
	String OP_ANY_MATCH = DesignerMessages.IDesignerConst_2;
	String OP_INTERVAL = DesignerMessages.IDesignerConst_3;
	String[] OPERATORS = { OP_EQ, OP_GT, OP_GE, OP_LT, OP_LE, OP_LIKE,
			OP_PRE_MATCH, OP_END_MATCH, OP_ANY_MATCH, OP_INTERVAL };
	// data type
	String TEXT = "text"; //$NON-NLS-1$
	String LONG_TEXT = "long text"; //$NON-NLS-1$
	String INTEGER = "integer"; //$NON-NLS-1$
	String BIGINT = "big int";
	String FLOAT = "float"; //$NON-NLS-1$
	String DATE = "date"; //$NON-NLS-1$
	String DATE_TIME = "dateTime"; //$NON-NLS-1$
	String LOOKUPCODE = "lookupCode";
	String[] data_types = { TEXT, LONG_TEXT, INTEGER, BIGINT, FLOAT, DATE,
			DATE_TIME, LOOKUPCODE };
	// auto extend types
	String AE_LOV = "lov"; //$NON-NLS-1$
	String AE_QUERY = "query"; //$NON-NLS-1$
	String AE_UPDATE = "update"; //$NON-NLS-1$
	String AE_MAINTAIN = "maintain"; //$NON-NLS-1$
	String[] AE_TYPES = { AE_QUERY, AE_LOV, AE_MAINTAIN };
	//
	String FOR_UPDATE = "forUpdate";
	String FOR_INSERT = "forInsert";
	String FOR_DISPLAY = "forDisplay";
	String FOR_QUERY = "forQuery";
	String INSERT_EXPRESSION = "insertExpression";
	String UPDATE_EXPRESSION = "updateExpression";
	String FOR_LOV = "forLov";
}
