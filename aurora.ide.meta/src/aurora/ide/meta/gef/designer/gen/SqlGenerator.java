package aurora.ide.meta.gef.designer.gen;

import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

public class SqlGenerator implements IDesignerConst {
	private static final String line_sep = String.format("%n");
	private static final String prefix = "    ";
	private static final String header = "create table %s (" + line_sep;
	private static final String tail = ");";
	private static final String column_model = prefix + "%%-%ds %%s";
	private static final String comment_model = "comment on column %%-%ss is '%%s';%%n";

	private BMModel model;
	private String name;

	public SqlGenerator(BMModel model, String name) {
		this.model = model;
		this.name = name;
	}

	public String gen() {
		Record[] rs = model.getRecords();
		StringBuilder sb = new StringBuilder(2000);
		sb.append(String.format(header, name));
		String cm = String.format(column_model, getMaxNameLength());
		for (int i = 0; i < rs.length; i++) {
			String t = (i == rs.length - 1) ? "" : ",";
			sb.append(String.format(cm, rs[i].getName(),
					getRealType(rs[i].getType()))
					+ t + line_sep);
		}
		sb.append(tail);
		addComment(sb);
		return sb.toString();
	}

	private void addComment(StringBuilder sb) {
		sb.append(line_sep);
		sb.append("-- Add comments to the columns " + line_sep);
		String cm = String.format(comment_model, name.length() + 1
				+ getMaxNameLength());
		for (Record r : model.getRecordList()) {
			sb.append(String.format(cm, name + "." + r.getName(), r.getPrompt()));
		}
	}

	private int getMaxNameLength() {
		int length = 0;
		for (Record r : model.getRecordList()) {
			int l = r.getName().length();
			if (l > length)
				length = l;
		}
		return length;
	}

	private String getRealType(String type) {
		if (TEXT.equals(type))
			return "varchar2(50)";
		else if (LONG_TEXT.equals(type))
			return "clob";
		else if (INTEGER.equals(type))
			return "number";
		else if (FLOAT.equals(type))
			return "number(20,2)";
		else if (DATE.equals(type))
			return "date";
		else if (DATE_TIME.equals(type))
			return "date";
		return "unknown";
	}
}
