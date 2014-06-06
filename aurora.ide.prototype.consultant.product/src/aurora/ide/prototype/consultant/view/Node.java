package aurora.ide.prototype.consultant.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;

import aurora.ide.prototype.consultant.view.util.ResourceUtil;

public class Node extends PlatformObject {
	private Node parent;
	private List<Node> children = new ArrayList<Node>();

	private IPath path;

	private boolean makeChildFinish = false;

	private boolean isChecked = false;

	private boolean isGrayed = false;

	protected Node() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		// if (obj instanceof Node && path != null) {
		// return path.equals(((Node) obj).getPath());
		// }
		return super.equals(obj);
	}

	public Node(IPath path) {
		super();
		this.setPath(path);
	}

	public Node(File file) {
		this(new Path(file.getPath()));
	}

	public File getFile() {
		if (path == null)
			return null;
		return path.toFile();
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public boolean hasChildren() {
		// return this.getFile().isDirectory();
		File[] list = this.getFile().listFiles();
		return list != null && list.length > 0;
	}

	public List<Node> getChildren() {
		return children;
	}

	public List<Node> makeChildren() {
		// children = new ArrayList<Node>();
		if (makeChildFinish)
			return children;

		File[] listFiles = this.getFile().listFiles();

		if (listFiles != null) {
			for (File file : listFiles) {
				String name = file.getName();
				if ("CVS".equals(name) || ".svn".equals(name))
					continue;
				if (name
				// .toLowerCase()
				.endsWith(".uip") || file.isDirectory()) {
					Node child = new Node(new Path(file.getPath()));
					if (contains(child) == false) {
						this.addChild(child);
					}
				}
			}
		}
		makeChildFinish = true;
		return children;
	}

	private boolean contains(Node node) {
		for (Node n : children) {
			if (n.getPath().equals(node.getPath())) {
				return true;
			}
		}
		return false;
	}

	public void addChild(Node child) {
		child.setParent(this);
		children.add(child);
	}

	public void removeChild(Node child) {
		Node del = null;
		for (Node n : children) {
			if (n.getPath().equals(child.getPath())) {
				del = n;
				break;
			}
		}
		if (del != null)
			children.remove(del);
	}

	public IPath getPath() {
		return path;
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public Node getChild(String name) {
		for (Node n : children) {
			if (name.equals(n.getPath().lastSegment())) {
				return n;
			}
		}
		return null;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getPropertiesPath() {
		boolean project = ResourceUtil.isProject(getFile());
		if (project)
			return ResourceUtil.getProjectPropertyFile(getFile()).getPath();
		boolean module = ResourceUtil.isModule(this.getFile());
		if (module)
			return ResourceUtil.getModulePropertyFile(getFile()).getPath();
		boolean function = ResourceUtil.isFunction(this.getFile());
		if (function)
			return ResourceUtil.getFunctionPropertyFile(getFile()).getPath();

		return path == null ? "" : path.toString();
	}

	public boolean isGrayed() {
		return isGrayed;
	}

	public void setGrayed(boolean isGrayed) {
		this.isGrayed = isGrayed;
	}
}
