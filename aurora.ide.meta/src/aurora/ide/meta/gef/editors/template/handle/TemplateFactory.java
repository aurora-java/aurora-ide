package aurora.ide.meta.gef.editors.template.handle;

import aurora.ide.meta.gef.editors.template.Template;

public class TemplateFactory {

	public static ITemplateHandle getTemplateHandle(String type) {
		if (Template.TYPE_CREATE.equals(type)) {
			return new CreateTemplateHandle();
		}
		if(Template.TYPE_UPDATE.equals(type)){
			return new UpdateTemplateHandle();
		}
		if(Template.TYPE_DISPLAY.equals(type)){
			return new DisplayTemplateHandle();
		}
		return new UpdateTemplateHandle();
	}
}