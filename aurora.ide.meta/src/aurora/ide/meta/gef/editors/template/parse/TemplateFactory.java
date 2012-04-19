package aurora.ide.meta.gef.editors.template.parse;

import aurora.ide.meta.gef.editors.template.Template;

public class TemplateFactory {

	public static ITemplateHandle getTemplateHandle(String type) {
		if (Template.TYPE_CREATE.equals(type)) {
			return new TemplateOfCreatehandle();
		}
		return null;
	}
}