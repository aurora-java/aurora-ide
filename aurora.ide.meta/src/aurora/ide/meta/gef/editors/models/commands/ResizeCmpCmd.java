package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;

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
		oldBounds = hostModel.getBounds().getCopy();
		hostModel.setBounds(hostModel.getBounds().getCopy().resize(sizeDelta));
	}

	@Override
	public String getLabel() {
		return "Resize Component";
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
		hostModel.setBounds(oldBounds);
	}

	public void setHostModel(AuroraComponent model) {
		this.hostModel = model;
	}

	public void setSizeDelt(Dimension sizeDelta) {
		this.sizeDelta = sizeDelta;
	}

}
