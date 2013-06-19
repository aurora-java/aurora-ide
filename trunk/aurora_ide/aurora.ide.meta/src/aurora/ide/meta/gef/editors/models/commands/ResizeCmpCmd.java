package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.util.BoundsConvert;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class ResizeCmpCmd extends Command {

	private AuroraComponent hostModel;
	private Dimension sizeDelta;
	private Rectangle oldBounds;

	@Override
	public boolean canExecute() {

		return super.canExecute();
	}

	@Override
	public boolean canUndo() {
		return super.canUndo();
	}

	@Override
	public void execute() {
		Rectangle draw2d = BoundsConvert.toDraw2d(hostModel.getBoundsCopy());
		oldBounds = draw2d;
		redo();
	}

	@Override
	public String getLabel() {
		return "Resize Component";
	}

	@Override
	public void redo() {
		hostModel.setBounds(BoundsConvert.toAurora(oldBounds
				.getResized(sizeDelta)));
	}

	@Override
	public void undo() {
		hostModel.setBounds(BoundsConvert.toAurora(oldBounds));
	}

	public void setHostModel(AuroraComponent model) {
		this.hostModel = model;
	}

	public void setSizeDelt(Dimension sizeDelta) {
		this.sizeDelta = sizeDelta;
	}

}
