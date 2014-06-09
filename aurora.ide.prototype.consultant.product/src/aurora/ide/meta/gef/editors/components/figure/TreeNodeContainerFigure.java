package aurora.ide.meta.gef.editors.components.figure;

import org.eclipse.draw2d.geometry.Dimension;

public class TreeNodeContainerFigure extends TreeNodeFigure {

	public TreeNodeContainerFigure() {
	}

	private Dimension realSize;

	// expand

	private void expand() {
//		this.setImage(TreePluginResources
//				.getImage(TreePluginResources.EXPANDED_KEY));
		Dimension size = this.getSize();
		if (realSize != null && realSize.height > size.height) {
			this.setSize(realSize);
		}
	}

	// collapse
	private void collapse() {
//		this.setImage(TreePluginResources
//				.getImage(TreePluginResources.COLLAPSED_KEY));
		realSize = this.getSize().getCopy();
		this.setSize(TreeLayoutManager.NODE_DEFUAULT_SIZE);
	}

	public void expand(boolean expanded) {
		if (expanded) {
			expand();
		} else
			collapse();
	}
}
