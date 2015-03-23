package aurora.bpmn.designer.rcp.viewer;

public interface IParent extends INode {
	public INode[] getChildren();
	
	public void removeChild(INode node);
	
	public void addChild(INode node);
}
