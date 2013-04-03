package aurora.ide.meta.gef.editors.policies.tplt;

import aurora.plugin.source.gen.screen.model.Container;

public class TemplateModeUtil {
	static public boolean isBindTemplate(Object model) {
		if (model instanceof Container) {
			String sectionType = ((Container) model).getSectionType();
			if (Container.SECTION_TYPE_QUERY.equals(sectionType)
					|| Container.SECTION_TYPE_RESULT.equals(sectionType)
					|| Container.SECTION_TYPE_BUTTON.equals(sectionType)) {
				return true;
			}
		}
		return false;
	}
}
