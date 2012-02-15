package aurora.ide.meta.gef.editors.layout;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.BoxPart;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.GridColumnPart;
import aurora.ide.meta.gef.editors.parts.GridPart;
import aurora.ide.meta.gef.editors.parts.InputPart;
import aurora.ide.meta.gef.editors.parts.NavbarPart;
import aurora.ide.meta.gef.editors.parts.TabFolderPart;
import aurora.ide.meta.gef.editors.parts.ToolbarPart;

public class GraphLayoutManager {
	private static final Insets BOX_PADDING = new Insets(8, 16, 8, 6);

	static BackLayout createLayout(ComponentPart ep) {
		if (ep instanceof BoxPart) {
			RowColBackLayout rowColBackLayout = new RowColBackLayout();
			rowColBackLayout.setPadding(BOX_PADDING);
			return rowColBackLayout;
		}
		if (ep instanceof InputPart) {
			return new InputFieldLayout();
		}
		if (ep instanceof GridPart) {
			return new GridBackLayout();
		}
		if (ep instanceof GridColumnPart) {
			return new GridColumnBackLayout2();
		}
		if (ep instanceof ToolbarPart) {
			return new ToolbarBackLayout2();
		}
		if (ep instanceof NavbarPart) {
			return new ToolbarBackLayout2();
		}
		if (ep instanceof TabFolderPart)
			return new TabFolderLayout();
		return new BackLayout();
	}

	static public Rectangle layout(ComponentPart ep) {
		return createLayout(ep).layout(ep);
	}

}
