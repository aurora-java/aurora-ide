package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TextCellEditor;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.figures.InputField;
import aurora.ide.meta.gef.editors.figures.PromptCellEditorLocator;
import aurora.ide.meta.gef.editors.figures.SimpleDataCellEditorLocator;
import aurora.ide.meta.gef.editors.layout.InputFieldLayout;
import aurora.ide.meta.gef.editors.models.commands.ChangeTextStyleCommand;
import aurora.ide.meta.gef.editors.policies.ComponentDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditManager;
import aurora.ide.meta.gef.editors.wizard.dialog.TextEditDialog;
import aurora.ide.prototype.consultant.demonstrate.Demonstrating;
import aurora.plugin.source.gen.screen.model.Combox;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentFSDProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class InputPart extends ComponentPart {

	private String type;

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		InputField inputField = new InputField();
		Input model = (Input) getModel();
		inputField.setModel(model);
		return inputField;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		String stringPropertyValue = getModel().getStringPropertyValue(
				ComponentFSDProperties.FSD_MEANING);
		if ("".equals(stringPropertyValue) || null == stringPropertyValue) {
			getFigure().setToolTip(null);
		} else {
			getFigure().setToolTip(new Label(stringPropertyValue));
		}
	}

	public Input getModel() {
		return (Input) super.getModel();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new ComponentDirectEditPolicy());
	}

	@Override
	public int getResizeDirection() {
		return EAST_WEST;
	}

	public Rectangle layout() {
		InputFieldLayout layout = new InputFieldLayout();
		return layout.layout(this);
	}

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	public EditPolicy getEditPolicy(Object key) {
		return super.getEditPolicy(key);
	}

	protected DirectEditManager manager;

	@Override
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())
				&& req instanceof LocationRequest) {
			if (MetaPlugin.isDemonstrate) {
				new Demonstrating(this).demonstrating(getViewer().getControl()
						.getShell());
				return;
			}
			Point location = ((LocationRequest) req).getLocation();
			Rectangle dataBounds = getPromptBounds();
			if (dataBounds.contains(location) == false) {
				performEditStyledStringText(ComponentProperties.prompt);
			} else {
				performEditStyledStringText(ComponentInnerProperties.INPUT_SIMPLE_DATA);
			}
		}
		if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)
				&& req instanceof DirectEditRequest) {
			Point location = ((DirectEditRequest) req).getLocation();
			InputField figure = (InputField) getFigure();
			Rectangle dataBounds = getPromptBounds();
			if (dataBounds.contains(location) == false) {
				performPromptDirectEditRequest(figure);
			} else {
				performSimpleDataDirectEditRequest(figure);
			}
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

	protected Rectangle getPromptBounds() {
		InputField figure = (InputField) getFigure();
		Rectangle bounds = figure.getBounds().getCopy();
		figure.translateToAbsolute(bounds);
		int labelWidth = figure.getLabelWidth();
		Rectangle dataBounds = bounds.getCopy().setX(bounds.x + labelWidth);
		return dataBounds;
	}

	protected void performSimpleDataDirectEditRequest(InputField figure) {

		if (MetaPlugin.isDemonstrate
				&& Combox.Combo.equals(getModel().getComponentType())) {
			new Demonstrating(this).demonstrating(getViewer().getControl()
					.getShell());

		} else {
			NodeDirectEditManager manager = new aurora.ide.meta.gef.editors.policies.NodeDirectEditManager(
					this, TextCellEditor.class,
					new SimpleDataCellEditorLocator(figure),
					ComponentInnerProperties.INPUT_SIMPLE_DATA);
			manager.show();
		}
	}

	protected void performPromptDirectEditRequest(InputField figure) {
		NodeDirectEditManager manager = new aurora.ide.meta.gef.editors.policies.NodeDirectEditManager(
				this, TextCellEditor.class,
				new PromptCellEditorLocator(figure), ComponentProperties.prompt);
		manager.show();
	}
}
