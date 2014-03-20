package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.figures.ToolbarButtonFigure;
import aurora.ide.meta.gef.editors.models.commands.ChangeTextStyleCommand;
import aurora.ide.meta.gef.editors.policies.ComponentDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditManager;
import aurora.ide.meta.gef.editors.wizard.dialog.TextEditDialog;
import aurora.ide.prototype.consultant.demonstrate.Demonstrating;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.ToolbarButton;
import aurora.plugin.source.gen.screen.model.properties.ComponentFSDProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class ToolbarButtonPart extends ComponentPart {

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		ToolbarButtonFigure buttonFigure = new ToolbarButtonFigure();
		buttonFigure.setModel(getModel());
		return buttonFigure;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		String title = getModel().getTitle();
		getFigure()
				.setToolTip(
						(title != null && title.length() > 0) ? new Label(title)
								: null);
		super.refreshVisuals();

		String stringPropertyValue = getModel().getStringPropertyValue(
				ComponentFSDProperties.FSD_DESC);
		if ("".equals(stringPropertyValue) || null == stringPropertyValue) {
			getFigure().setToolTip(null);
		} else {
			getFigure().setToolTip(new Label(stringPropertyValue));
		}

	}

	public ToolbarButton getModel() {
		return (ToolbarButton) super.getModel();
	}

	public ToolbarButtonFigure getFigure() {
		return (ToolbarButtonFigure) super.getFigure();
	}


	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new ComponentDirectEditPolicy());
	}

	@Override
	public int getResizeDirection() {
		return NSEW;
	}

	@Override
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())
				&& req instanceof LocationRequest) {
			if (MetaPlugin.isDemonstrate) {
				new Demonstrating(this).demonstrating(getViewer().getControl()
						.getShell());
				return;
			} else {
				performEditStyledStringText(ComponentProperties.text);
			}
		}
		if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)
				&& req instanceof DirectEditRequest) {
			ToolbarButtonFigure figure = this.getFigure();
			Rectangle bounds = figure.getBounds().getCopy();
			figure.translateToAbsolute(bounds);
			performPromptDirectEditRequest(figure);
		} else
			super.performRequest(req);
	}

	protected void performEditStyledStringText(String propertyID) {
		TextEditDialog ted = new TextEditDialog(this.getViewer().getControl()
				.getShell());
		StyledStringText sst = new StyledStringText();
		Object obj = this.getModel().getPropertyValue(
				propertyID + ComponentInnerProperties.TEXT_STYLE);
		if (obj instanceof StyledStringText)
			sst = (StyledStringText) obj;
		sst.setText(this.getModel().getStringPropertyValue(propertyID));
		ted.setStyledStringText(sst);
		if (Dialog.OK == ted.open()) {
			sst = ted.getStyledStringText();
			ChangeTextStyleCommand command = new ChangeTextStyleCommand(
					getModel(), propertyID, sst.getText(), sst);
			this.getViewer().getEditDomain().getCommandStack().execute(command);
		}
	}

	protected void performPromptDirectEditRequest(final ToolbarButtonFigure figure) {
		NodeDirectEditManager manager = new aurora.ide.meta.gef.editors.policies.NodeDirectEditManager(
				this, TextCellEditor.class, new CellEditorLocator() {
					public void relocate(CellEditor celleditor) {
						Text text = (Text) celleditor.getControl();
						Rectangle bounds = figure.getBounds().getCopy();
						figure.translateToAbsolute(bounds);
						text.setBounds(bounds.x - 1, bounds.y - 1,
								bounds.width + 1, bounds.height + 1);
					}

				}, ComponentProperties.text);
		manager.show();
	}

}
