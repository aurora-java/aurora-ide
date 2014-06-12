package aurora.ide.meta.gef.editors.components.figure;

import org.eclipse.draw2d.geometry.Dimension;

import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;

public class TreeNodeContainerFigure extends TreeNodeFigure {

	private CustomTreeContainerNode node;

	public TreeNodeContainerFigure(CustomTreeContainerNode node) {
		super(node);
		this.setNode(node);
	}

	private Dimension realSize;

	// expand

//	private void expand() {
////		this.setImage(TreePluginResources
////				.getImage(TreePluginResources.EXPANDED_KEY));
//		Dimension size = this.getSize();
//		if (realSize != null && realSize.height > size.height) {
//			this.setSize(realSize);
//		}
//	}

	// collapse
//	private void collapse() {
////		this.setImage(TreePluginResources
////				.getImage(TreePluginResources.COLLAPSED_KEY));
//		realSize = this.getSize().getCopy();
//		this.setSize(TreeLayoutManager.NODE_DEFUAULT_SIZE);
//	}

	public void expand(boolean expanded) {
		getNode().setExpand(expanded);
//		this.setExpand(expanded);
//		if (expanded) {
//			expand();
//		} else
//			collapse();
	}

	public CustomTreeContainerNode getNode() {
		return node;
	}

	public void setNode(CustomTreeContainerNode node) {
		this.node = node;
	}
}
