package aurora.ide.editor.textpage.quickfix;

/**
 * 有两个属性,String,int<br/>
 * 可以按照int值比较大小
 * 
 * @author jessen
 * 
 */
class SortElement implements Comparable<SortElement> {
	String name;
	int sortnum;

	public SortElement(String name, int sortnum) {
		this.name = name;
		this.sortnum = sortnum;
	}

	public int compareTo(SortElement o) {
		return this.sortnum - o.sortnum;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SortElement))
			return false;
		SortElement se = (SortElement) obj;
		return name.equals(se.name);
	}
}