package aurora.ide.meta.gef.editors.layout;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.InputPart;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class InputFieldLayout extends BackLayout {

	public Rectangle layout(ComponentPart parent) {

		if (parent instanceof InputPart) {
			// layout.width += box.getLabelWidth();
			Rectangle bounds = toDraw2d(parent.getComponent().getBoundsCopy());
			int labelWidth = 0;
			IFigure parentf = (((ComponentPart) parent.getParent()).getFigure());
			if (parentf instanceof BoxFigure) {
				labelWidth = ((BoxFigure) parentf).getLabelWidth();
			} else {
				labelWidth = ScreenBody.DLabelWidth;
			}
			return bounds.setWidth(bounds.width + labelWidth);

		}

		return super.layout(parent);
	}

}
