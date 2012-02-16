package aurora.ide.meta.gef.editors.models.io;

import java.util.ArrayList;
import java.util.HashMap;

import aurora.ide.meta.gef.editors.models.AuroraComponent;

public class ModelIOContext {
	/**
	 * used to record a reference declare( the ref object has not been created
	 * when ref occured,so record it,and solve it later( in the ModelIOManager))
	 */
	ArrayList<ReferenceDecl> refDeclList = new ArrayList<ReferenceDecl>(20);
	/**
	 * a map to record the( markid --> AuroraComponent ) mapping
	 */
	HashMap<String, AuroraComponent> markMap = new HashMap<String, AuroraComponent>(
			100);
}
