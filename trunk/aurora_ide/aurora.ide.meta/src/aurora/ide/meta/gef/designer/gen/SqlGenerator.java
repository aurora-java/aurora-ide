package aurora.ide.meta.gef.designer.gen;

import java.util.ArrayList;

import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

public class SqlGenerator implements IDesignerConst {
	private static final String line_sep = String.format("%n");
	private static final String prefix = "    ";
	private static final String header = "create table %s (" + line_sep;
	private static final String tail = ")";
	private static final String column_model = prefix + "%%-%ds %%s";
	private static final String comment_model = "comment on column %%-%ss is '%%s'";
	private static final String[] who_fields = { "created_by", "creation_date",
			"last_updated_by", "last_update_date" };
	private static final String[] who_fields_type = { "number", "date",
			"number", "date" };

	private BMModel model;
	private String name;
	private int maxNameLength = 0;

	public SqlGenerator(BMModel model, String name) {
		this.model = model;
		this.name = name;
	}

	public String[] gen() {
		ArrayList<String> sqls = new ArrayList<String>();
		String[][] rs = getFieldsInfo();
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
			sb.append(String.format(cm, rs[i][0], rs[i][1]) + t + line_sep);
		}
		sb.append(tail);
		sqls.add(sb.toString());
		addComment(sqls);
		String[] sqlArr = new String[sqls.size()];
		sqls.toArray(sqlArr);
		return sqlArr;
	}

	private String[][] getFieldsInfo() {
		ArrayList<String[]> list = new ArrayList<String[]>();
		for (Record r : model.getRecordList()) {
			list.add(new String[] { r.getName(), getSqlType(r.getType()) });
		}
		if (model.isWhoEnabled()) {
			for (int i = 0; i < who_fields.length; i++) {
				list.add(new String[] { who_fields[i], who_fields_type[i] });
			}
		}
		return list.toArray(new String[list.size()][]);
	}

	private void addComment(ArrayList<String> sqls) {
		String cm = String.format(comment_model, name.length() + 1
				+ maxNameLength);
		Record pkr = model.getPkRecord();
		sqls.add(String.format(cm, name + "." + pkr.getName(), pkr.getPrompt()));
		for (Record r : model.getRecordList()) {
			sqls.add(String.format(cm, name + "." + r.getName(), r.getPrompt()));
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
		if (model.isWhoEnabled()) {
			for (String n : who_fields) {
				if (n.length() > length)
					length = n.length();
			}
		}
		return length;
	}

	private String getSqlType(String type) {
		DataType dt = DataType.fromString(type);
		if (dt == null)
			dt = DataType.TEXT;
		return dt.getSqlType();
	}
}
