package aurora.ide.meta.gef.editors.template.handle;

import aurora.ide.meta.gef.editors.template.Template;

public class TemplateFactory {

	public static TemplateHandle getTemplateHandle(String type,TemplateConfig config) {
		if (Template.TYPE_CREATE.equals(type)) {
			return new CreateTemplateHandle(config);
		}
		if(Template.TYPE_UPDATE.equals(type)){
			return new UpdateTemplateHandle(config);
		}
		if(Template.TYPE_DISPLAY.equals(type)){
			return new DisplayTemplateHandle(config);
		}
		if(Template.TYPE_SERACH.equals(type)){
			return new SerachTemplateHandle(config);
		}
		return null;
	}
}