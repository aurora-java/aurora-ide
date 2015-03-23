package aurora.ide.preferencepages;


import org.eclipse.jface.text.templates.GlobalTemplateVariables;

import aurora.ide.AuroraPlugin;


public class AuroraTemplateContextType extends
		org.eclipse.jface.text.templates.TemplateContextType {

	public static final String new_screen = AuroraPlugin.PLUGIN_ID+ ".new_screen";
	public static final String SIGN = AuroraPlugin.PLUGIN_ID+ ".sign";
	public AuroraTemplateContextType() {
		super();
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.Year());
	}

}