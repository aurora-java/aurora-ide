package aurora.ide.prototype.consultant.view;

import java.util.List;

public class Root extends Node {
//	private List<Node> children = new ArrayList<Node>();

	public Root() {
	}
//	public List<Node> getChildren() {
//		
//		return children;
//	}
//
//	public void addChild(Node child) {
//		child.setParent(this);
//		children.add(child);
//	}
//	
	public List<Node> makeChildren() {
		return getChildren();
	}

	//
//	private List<IPath> paths = new ArrayList<IPath>();
//
//	public List<IPath> getPaths() {
//		return paths;
//	}
//
//	public void addPath(IPath path) {
//		this.paths.add(path);
//	}
//	
//	public File[] getFiles(){
//		File[] r =  new File[paths.size()];
//		for (int i = 0;i< paths.size();i++){
//			r[i] = paths.get(i).toFile();
//		}
//		return r;
//	}
	
	
}
