package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;

import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.VBox;

/**

 */
public class BoxFigure extends Figure {

	private int labelWidth;

	private BOX box;

	public BoxFigure() {
		this.setLayoutManager(new DummyLayout());
		this.setBorder(new TitleBorder("大家好 ： 敬请期待。。。"));
	}

	public int getLabelWidth() {
		return box.getLabelWidth();
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		if (box instanceof FieldSet) {
			Border border = getBorder();
			if (border instanceof AbstractLabeledBorder) {
				((AbstractLabeledBorder) border).setLabel(box.getTitle());
			}
		}
	}

	public void setBox(BOX model) {
		this.box = model;
		if (model instanceof HBox) {
			this.setBorder(new VirtualBoxBorder("H"));
		} else if (model instanceof VBox) {
			this.setBorder(new VirtualBoxBorder("V"));
		} else if (model instanceof FieldSet) {
			this.setBorder(new FieldsetBorder(model.getTitle()));
		}
	}

	public BOX getBox() {
		return box;
	}

}
