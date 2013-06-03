package aurora.ide.meta.gef.editors.request;

import java.util.List;

import org.eclipse.gef.requests.LocationRequest;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class PasteComponentRequest extends LocationRequest {
	public PasteComponentRequest() {
		this.setType(AuroraRequestConstants.REQ_PASTE_COMPONENTS);
	}

	public List<AuroraComponent> getPasteAuroraComponent() {
		return pasteAuroraComponents;
	}

	public void setPasteAuroraComponent(
			List<AuroraComponent> pasteAuroraComponents) {
		this.pasteAuroraComponents = pasteAuroraComponents;
	}

	public ComponentPart getSelectionComponentPart() {
		return selectionComponentPart;
	}

	public void setSelectionComponentPart(ComponentPart selectionComponentPart) {
		this.selectionComponentPart = selectionComponentPart;
	}

	private ComponentPart selectionComponentPart;
	private List<AuroraComponent> pasteAuroraComponents;
}
