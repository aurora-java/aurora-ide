package aurora.ide.meta.gef.designer.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.IDesignerConst;

public class BMModel {
	public static final String STRUCTURE_RECORD = "structure_record";
	public static final String STRUCTURE_RELATION = "structure_relation";
	public static final String TITLE = "title";
	public static final String AUTOEXTEND = "autoextend";
	public static final String DEFAULT_DISPLAY = "defaultdisplay";
	private String FIELD_NAME_PREFIX = "c";
	public static final int RECORD = 0;
	public static final int RELATION = 1;
	private ArrayList<Record> records = new ArrayList<Record>();
	private ArrayList<Relation> relations = new ArrayList<Relation>();
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private String title = "";
	private String autoExtend = "";
	private String defaultDiaplay = "";
	private Record pkRecord = new Record() {
		{
			setName("default_pk_name");// this name should be reset
			setPrompt("primary-key");
			setType(DataType.BIGNIT.getDisplayType());
			put(IDesignerConst.COLUMN_QUERYFIELD, "true");
			put(IDesignerConst.COLUMN_QUERY_OP, "=");
		}
	};
	private PropertyChangeListener recordListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent evt) {
			Record r = (Record) evt.getSource();
			firePropertyChange(Record.class.getSimpleName() + ":" + r.getNum()
					+ ":" + evt.getPropertyName(), evt.getOldValue(),
					evt.getNewValue());
		}
	};

	private PropertyChangeListener relationListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent evt) {
			Record r = (Record) evt.getSource();
			firePropertyChange(
					Relation.class.getSimpleName() + ":" + r.getNum() + ":"
							+ evt.getPropertyName(), evt.getOldValue(),
					evt.getNewValue());
		}
	};

	public ArrayList<Record> getRecordList() {
		return records;
	}

	public ArrayList<Relation> getRelationList() {
		return relations;
	}

	public void setDefaultDisplay(String prompt) {
		String old = defaultDiaplay;
		defaultDiaplay = prompt;
		firePropertyChange(DEFAULT_DISPLAY, old, prompt);
	}

	public String getDefaultDisplay() {
		return defaultDiaplay;
	}

	public Record getDefaultDisplayRecord() {
		for (Record r : records)
			if (r.getPrompt().equals(defaultDiaplay))
				return r;
		return null;
	}

	public Record[] getRecords() {
		Record[] rcds = new Record[records.size()];
		records.toArray(rcds);
		return rcds;
	}

	public Relation[] getRelations() {
		Relation[] rels = new Relation[relations.size()];
		relations.toArray(rels);
		return rels;
	}

	public int add(Record record) {
		records.add(record);
		notifyModidy();
		return records.size() - 1;
	}

	public int add(Relation rel) {
		relations.add(rel);
		notifyRelationChange();
		return relations.size() - 1;
	}

	public void add(int idx, Record r) {
		records.add(idx, r);
		notifyModidy();
	}

	public void add(int idx, Relation rel) {
		relations.add(idx, rel);
		notifyRelationChange();
	}

	public void remove(Record r) {
		records.remove(r);
		notifyModidy();
	}

	public void remove(Relation rel) {
		relations.remove(rel);
		notifyRelationChange();
	}

	public Record removeRecord(int idx) {
		Record r = records.remove(idx);
		notifyModidy();
		return r;
	}

	public Relation removeRelation(int idx) {
		Relation rel = relations.remove(idx);
		notifyRelationChange();
		return rel;
	}

	private void notifyModidy() {
		ArrayList<Record> unNamedRecord = new ArrayList<Record>();
		int maxNum = 0;
		for (int i = 0; i < records.size(); i++) {
			Record r = records.get(i);
			if (r.getNum() != i + 1)
				r.setNum(i + 1);
			r.removePropertyChangeListener(recordListener);
			r.addPropertyChangeListener(recordListener);
			if (r.getName().trim().length() == 0)
				unNamedRecord.add(r);
			if (r.getName().matches(FIELD_NAME_PREFIX + "(0|([1-9]\\d*))")) {
				int n = Integer.parseInt(r.getName().substring(
						FIELD_NAME_PREFIX.length()));
				if (maxNum < n)
					maxNum = n;
			}
		}
		for (Record r : unNamedRecord) {
			r.setName(FIELD_NAME_PREFIX + (++maxNum));
		}
		firePropertyChange(STRUCTURE_RECORD, null, null);
	}

	private void notifyRelationChange() {
		for (int i = 0; i < relations.size(); i++) {
			Relation r = relations.get(i);
			if (r.getNum() != i + 1)
				r.setNum(i + 1);
			r.removePropertyChangeListener(relationListener);
			r.addPropertyChangeListener(relationListener);
		}
		firePropertyChange(STRUCTURE_RELATION, null, null);
	}

	public boolean isEmpty() {
		return records.isEmpty();
	}

	public boolean isFirst(Record r) {
		if (isEmpty())
			return false;
		return records.get(0).equals(r);
	}

	public boolean isLast(Record r) {
		if (isEmpty())
			return false;
		return records.get(records.size() - 1).equals(r);
	}

	public int indexOf(Record r) {
		return records.indexOf(r);
	}

	public Record getAt(int idx) {
		return records.get(idx);
	}

	public void setAt(int idx, Record r) {
		records.set(idx, r);
		notifyModidy();
	}

	public void remove(List<Record> list) {
		records.removeAll(list);
		notifyModidy();
	}

	public void removeRelations(List<Relation> list) {
		relations.removeAll(list);
		notifyRelationChange();
	}

	public void removeAll() {
		records.clear();
		notifyModidy();
	}

	public void removeAllRelations() {
		relations.clear();
		notifyRelationChange();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	protected void fireStructureChange(String prop, Object child) {
		listeners.firePropertyChange(prop, null, child);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null)
			title = "";
		if (this.title.equals(title))
			return;
		String old = this.title;
		this.title = title;
		firePropertyChange(TITLE, old, title);
	}

	public void setAutoExtends(String string) {
		this.autoExtend = string;
	}

	/**
	 * just return the string like query|lov
	 * 
	 * @return
	 */
	public String getAutoExtends() {
		return autoExtend;
	}

	/**
	 * return a string array,contains extend types<br>
	 * value always in low case and trimmed ,no duplication .<br/>
	 * do not check weather it is valid
	 * 
	 * @return if autoExtend==null ,return empty string array
	 */
	public String[] getAutoExtendTypes() {
		if (autoExtend == null)
			return new String[] {};
		StringTokenizer st = new StringTokenizer(autoExtend, "|");
		ArrayList<String> als = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim().toLowerCase();
			if (als.indexOf(s) == -1)
				als.add(s);
		}
		String[] ss = new String[als.size()];
		return als.toArray(ss);
	}

	public Record getPkRecord() {
		return pkRecord;
	}

	public void setPkRecord(Record pkRecord) {
		this.pkRecord = pkRecord;
	}

	public void setNamePrefix(String pre) {
		FIELD_NAME_PREFIX = pre;
	}

	public String getNamePrefix() {
		return FIELD_NAME_PREFIX;
	}
}
