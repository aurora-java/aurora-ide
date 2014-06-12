package aurora.ide.meta.gef.editors.components.eidtpolicy;

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.jface.dialogs.Dialog;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.models.commands.ChangeTextStyleCommand;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.wizard.dialog.TextEditDialog;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class TextStyleSupport {

	private ComponentPart part;
	private String property;

	public TextStyleSupport(ComponentPart part,String property) {
		super();
		this.part = part;
		this.property = property;
	}


	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())
				&& req instanceof LocationRequest) {
			if (MetaPlugin.isDemonstrate) {
				return;
			}
			performEditStyledStringText(this.property);
		}
	}

	protected void performEditStyledStringText(String propertyID) {
		TextEditDialog ted = new TextEditDialog(part.getViewer().getControl()
				.getShell());
		StyledStringText sst = new StyledStringText();
		Object obj = part.getComponent().getPropertyValue(
				propertyID + ComponentInnerProperties.TEXT_STYLE);
		if (obj instanceof StyledStringText)
			sst = (StyledStringText) obj;
		sst.setText(part.getComponent().getStringPropertyValue(propertyID));
		ted.setStyledStringText(sst);
		if (Dialog.OK == ted.open()) {
			sst = ted.getStyledStringText();
			ChangeTextStyleCommand command = new ChangeTextStyleCommand(
					part.getComponent(), propertyID, sst.getText(), sst);
			part.getViewer().getEditDomain().getCommandStack().execute(command);
		}
	}

}
