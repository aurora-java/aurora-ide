package aurora.ide.prototype.consultant.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;

public class Node extends PlatformObject {
	private Node parent;
	private List<Node> children = new ArrayList<Node>();

	public static Node node;
	private IPath path;

	protected Node() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node && path != null) {
			return path.equals(((Node) obj).getPath());
		}
		return super.equals(obj);
	}

	public Node(IPath path) {
		super();
		this.setPath(path);
	}

	public File getFile() {
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
		children = new ArrayList<Node>();

		File[] listFiles = this.getFile().listFiles();

		if (listFiles != null) {
			for (File file : listFiles) {
				if (file.getName().endsWith(".uip") || file.isDirectory()) {

					this.addChild(new Node(new Path(file.getPath())));
				}
			}
		}
		return children;
	}

	public void addChild(Node child) {
		child.setParent(this);
		children.add(child);
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
}
