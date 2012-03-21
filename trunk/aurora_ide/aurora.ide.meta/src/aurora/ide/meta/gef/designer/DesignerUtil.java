package aurora.ide.meta.gef.designer;

import java.util.ArrayList;

import aurora.ide.meta.gef.designer.editor.BMModelViewer;
import aurora.ide.meta.gef.designer.model.Record;

public class DesignerUtil {
	public static final ArrayList<String[]> typeMap = new ArrayList<String[]>();
	static {
		typeMap.add(new String[] { ".*(数量|大小)", BMModelViewer.data_types[2],
				"numberField" });
		typeMap.add(new String[] { ".*(金额|单价)", BMModelViewer.data_types[3],
				"numberField" });
		typeMap.add(new String[] { ".*(日期)", BMModelViewer.data_types[4],
				"datePicker" });
		typeMap.add(new String[] { ".*(时间)", BMModelViewer.data_types[5],
				"dateTimePicker" });
		typeMap.add(new String[] { ".*超长.*", BMModelViewer.data_types[1],
				"textField" });
		typeMap.add(new String[] { ".*", BMModelViewer.data_types[0],
				"textField" });
	}

	public static Record createRecord(String prompt) {
		Record r = new Record();
		r.put(BMModelViewer.COLUMN_PROMPT, prompt);
		for (String[] ss : typeMap) {
			if (prompt.matches(ss[0])) {
				r.put(BMModelViewer.COLUMN_TYPE, ss[1]);
				r.put(BMModelViewer.COLUMN_EDITOR, ss[2]);
				break;
			}
		}
		r.put(BMModelViewer.COLUMN_NAME, "");
		r.put(BMModelViewer.COLUMN_QUERYFIELD, false);
		r.put(BMModelViewer.COLUMN_ISFOREIGN, false);
		return r;
	}
}
