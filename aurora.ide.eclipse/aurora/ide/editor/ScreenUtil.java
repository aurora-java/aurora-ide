package aurora.ide.editor;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.helpers.AuroraConstant;

public class ScreenUtil {

	
	public static CompositeMap createScreenTopNode(){
		CompositeMap model = new CommentCompositeMap("a",AuroraConstant.ScreenQN.getNameSpace(),AuroraConstant.ScreenQN.getLocalName());
		return model;
	}
}
