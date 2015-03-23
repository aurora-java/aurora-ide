package aurora.ide.schema;

import org.eclipse.core.resources.IProject;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.prompt.PromptManager;

public class PromptsTypeDelegate implements ITypeDelegate {
	private Attribute attribute;
	private CompositeMap map;

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public CompositeMap getMap() {
		return map;
	}

	public void setMap(CompositeMap map) {
		this.map = map;
	}

	public PromptsTypeDelegate(CompositeMap map, Attribute attribute) {
		this.map = map;
		this.attribute = attribute;
	}

	public final static QualifiedName PromptsTypeName = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "Prompts");

	public String getValue(String code) {
		if (PromptManager.isPromptsCode(code) == false)
			return code;
		String r = "";
		IProject project = ProjectUtil.getIProjectFromActiveEditor();
		if(project ==null){
			project = ProjectUtil.getIProjectFromSelection();
		}
		if(project ==null)
			return code;
		try {
			String[] prompts = PromptManager.getPrompts(code,
					DBConnectionUtil.getDBConnectionSyncExec(project));
			if (prompts[0] != null) {
				r += "中文: " + prompts[0];
			}
			if (prompts[1] != null) {
				r += "&nbsp;&nbsp;&nbsp;" + "英文: " + prompts[1];
			}
			return r;
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return  code;
	}

	
}
