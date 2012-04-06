package aurora.ide.meta.gef.designer;

import java.util.ArrayList;

import aurora.ide.meta.gef.designer.model.Record;

public class DesignerUtil implements IDesignerConst {
	public static final ArrayList<Object[]> typeMap = new ArrayList<Object[]>();
	static {
		typeMap.add(new Object[] { DesignerMessages.DesignerUtil_0,
				DataType.INTEGER });
		typeMap.add(new Object[] { DesignerMessages.DesignerUtil_1,
				DataType.FLOAT });
		typeMap.add(new Object[] { DesignerMessages.DesignerUtil_2,
				DataType.DATE });
		typeMap.add(new Object[] { DesignerMessages.DesignerUtil_3,
				DataType.DATE_TIME });
		typeMap.add(new Object[] { DesignerMessages.DesignerUtil_4,
				DataType.LONG_TEXT });
		typeMap.add(new Object[] { DesignerMessages.DesignerUtil_5,
				DataType.TEXT });
	}

	public static Record createRecord(String prompt) {
		Record r = new Record();
		r.put(COLUMN_PROMPT, prompt);
		for (Object[] ss : typeMap) {
			if (prompt.matches((String) ss[0])) {
				DataType dt = (DataType) ss[1];
				r.put(COLUMN_TYPE, dt.getDisplayType());
				r.put(COLUMN_EDITOR, dt.getDefaultEditor());
				r.put(COLUMN_QUERY_OP, dt.getDefaultOperator());
				break;
			}
		}
		r.put(COLUMN_NAME, "");
		r.put(COLUMN_QUERYFIELD, false);
		r.put(COLUMN_ISFOREIGN, false);
		return r;
	}
}
