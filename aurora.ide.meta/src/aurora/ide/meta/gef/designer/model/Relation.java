package aurora.ide.meta.gef.designer.model;

import java.util.StringTokenizer;

import aurora.ide.meta.gef.designer.IDesignerConst;

public class Relation extends Record implements IDesignerConst {
	public static final String REF_PROMPTS = "ref-prompts";

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

	public String getRefPrompts() {
		return getStringNotNull(REF_PROMPTS);
	}

	public String[] getRefPromptsArray() {
		StringTokenizer st = new StringTokenizer(getRefPrompts(), ",");
		String[] strs = new String[st.countTokens()];
		for (int i = 0; st.hasMoreElements(); i++)
			strs[i] = st.nextToken();
		return strs;
	}

	public void setRefPrompts(String str) {
		put(REF_PROMPTS, str);
	}

	public void setRefPromptsArray(String[] prompts) {
		StringBuilder sb = new StringBuilder();
		if (prompts.length > 0) {
			sb.append(prompts[0]);
			for (int i = 1; i < prompts.length; i++)
				sb.append("," + prompts[i]);
		}
		setRefPrompts(sb.toString());
	}
}