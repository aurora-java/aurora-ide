package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;

import aurora.plugin.source.gen.screen.model.BOX;
import aurora.plugin.source.gen.screen.model.FieldSet;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.HBox;
import aurora.plugin.source.gen.screen.model.VBox;

/**

 */
public class BoxFigure extends Figure  implements IResourceDispose{

//	private int labelWidth;

	private BOX box;

	public BoxFigure() {
		this.setLayoutManager(new DummyLayout());
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
//		if (box instanceof FieldSet) {
//			Border border = getBorder();
//			if (border instanceof AbstractLabeledBorder) {
//				((AbstractLabeledBorder) border).setLabel(box.getTitle());
//			}
//		}
	}

	public void setBox(BOX model) {
		this.box = model;
		if (model instanceof HBox) {
			this.setBorder(new VirtualBoxBorder("H"));
		} else if (model instanceof VBox) {
			this.setBorder(new VirtualBoxBorder("V"));
		} else if (model instanceof FieldSet) {
			this.setBorder(new FieldsetBorder(model.getTitle()));
		}else if(model instanceof Form){
			this.setBorder(new TitleBorder(""));
		}
	}

	public BOX getBox() {
		return box;
	}

	public void disposeResource() {
		Border border = this.getBorder();
		if (border instanceof IResourceDispose) {
			((IResourceDispose) border).disposeResource();
		}
	}
}
