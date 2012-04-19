package aurora.ide.meta.gef.designer.gen;

import aurora.ide.meta.gef.designer.DataType;
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
	private int maxNameLength = 0;

	public SqlGenerator(BMModel model, String name) {
		this.model = model;
		this.name = name;
	}

	public String gen() {
		Record[] rs = model.getRecords();
		StringBuilder sb = new StringBuilder(10000);
		sb.append(String.format(header, name));
		maxNameLength = getMaxNameLength();
		String cm = String.format(column_model, maxNameLength);
		String t = (rs.length == 0) ? "" : ",";
		Record r = model.getPkRecord();
		sb.append(String.format(cm, r.getName(), getSqlType(r.getType()))
				+ " not null" + t + line_sep);
		for (int i = 0; i < rs.length; i++) {
			t = (i == rs.length - 1) ? "" : ",";
			sb.append(String.format(cm, rs[i].getName(),
					getSqlType(rs[i].getType()))
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
				+ maxNameLength);
		Record pkr = model.getPkRecord();
		sb.append(String.format(cm, name + "." + pkr.getName(), pkr.getPrompt()));
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
		int l = model.getPkRecord().getName().length();
		if (l > length)
			length = l;
		return length;
	}

	private String getSqlType(String type) {
		DataType dt = DataType.fromString(type);
		if (dt == null)
			dt = DataType.TEXT;
		return dt.getSqlType();
	}
}
