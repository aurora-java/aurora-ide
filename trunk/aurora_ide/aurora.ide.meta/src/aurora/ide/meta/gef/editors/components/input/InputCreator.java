package aurora.ide.meta.gef.editors.components.input;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.PaletteEntry;

import aurora.ide.meta.gef.editors.components.ComponentCreator;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.parts.InputPart;

public class InputCreator extends ComponentCreator {

	public InputCreator() {
	}
	public PaletteEntry createPaletteEntry(){
		return null;
	}
	public EditPart createEditPart(Object model){
		if(model instanceof Input)
			return new InputPart();
		return null;
	}
	public Class<? extends AuroraComponent> clazz() {
		return Input.class;
	}

}
