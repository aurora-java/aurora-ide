package aurora.ide.editor.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;

public class OutlineTree {
	private OutlineTree parent;
	private String text;
	private String other;
	private IRegion startRegion;
	private IRegion endRegion;
	private IRegion region;
	private String image;
	private List<OutlineTree> children = new ArrayList<OutlineTree>();

	public int getChildrenCount() {
		return children.size();
	}

	public OutlineTree getParent() {
		return parent;
	}

	public List<OutlineTree> getChildren() {
		return children;
	}

	public void add(OutlineTree lt) {
		if (lt == null) {
			return;
		}
		lt.parent = this;
		children.add(lt);
	}

	public void add(int index, OutlineTree lt) {
		if (lt == null) {
			return;
		}
		lt.parent = this;
		children.add(index, lt);
	}

	public void removeAll() {
		children.clear();
	}

	public boolean remove(OutlineTree lt) {
		return children.remove(lt);
	}

	public OutlineTree remove(int index) {
		OutlineTree t = null;
		if (index < children.size() && index >= 0) {
			t = children.remove(index);
		}
		return t;
	}

	public OutlineTree getChild(int index) {
		if (index < 0 || index >= children.size()) {
			return null;
		}
		return children.get(index);
	}

	@Override
	public boolean equals(Object t) {
		if (t instanceof OutlineTree) {
			OutlineTree ot = (OutlineTree) t;
			return eq(ot.startRegion, startRegion) && eq(ot.endRegion, endRegion) && eq(ot.text, text)
					&& eq(ot.other, other) && eq(ot.region, region);
		}
		return false;
	}

	private boolean eq(Object o1, Object o2) {
		if (o1 == null) {
			return o1 == o2;
		}
		return o1.equals(o2);
	}

	public OutlineTree getRoot() {
		return getRoot(this);
	}

	private OutlineTree getRoot(OutlineTree lt) {
		if (null == lt) {
			return null;
		} else if (null == lt.getParent()) {
			return lt;
		}
		return getRoot(lt.getParent());
	}

	public void copy(OutlineTree lt) {
		if (lt == null) {
			return;
		}
		startRegion = lt.startRegion;
		endRegion = lt.endRegion;
		text = lt.text;
		other = lt.other;
		region = lt.region;
	}

	@Override
	public String toString() {
		return text + " " + other;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public IRegion getStartRegion() {
		return startRegion;
	}

	public void setStartRegion(IRegion startRegion) {
		this.startRegion = startRegion;
	}

	public IRegion getEndRegion() {
		return endRegion;
	}

	public void setEndRegion(IRegion endRegion) {
		this.endRegion = endRegion;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setParent(OutlineTree parent) {
		this.parent = parent;
	}

	public IRegion getRegion() {
		return region;
	}

	public void setRegion(IRegion region) {
		this.region = region;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

}
