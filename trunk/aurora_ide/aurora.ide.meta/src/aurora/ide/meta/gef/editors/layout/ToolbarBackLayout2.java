package aurora.ide.meta.gef.editors.layout;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.ComponentPart;

public class ToolbarBackLayout2 extends RowColBackLayout {
	final static private Insets padding = new Insets(2, 0, 0, 0);
	public ToolbarBackLayout2(){
		this.setPadding(padding);
	}

	protected Rectangle calculateRectangle(ComponentPart parent) {
		return this.getSelfRectangle().getCopy();
	}

}
