package aurora.ide.schema;

import java.util.List;

import org.eclipse.core.resources.IProject;

import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.syscode.Syscode;
import aurora.ide.syscode.SyscodeManager;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;

public class SysCodeTypeDelegate implements ITypeDelegate {
	// type="bm:Lookupcode"
	public final static QualifiedName LookUpCodeTypeName = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "Lookupcode");

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

	public SysCodeTypeDelegate(CompositeMap map, Attribute attribute) {
		this.map = map;
		this.attribute = attribute;
	}

	public String getValue(String key) {
		return getHtmlDocument(key);
	}

	public String getHtmlDocument(String code) {

//		if (PromptsTypeDelegate.isPromptsCode(code) == false)
//			return code;
		IProject project = ProjectUtil.getIProjectFromActiveEditor();
		if (project == null) {
			project = ProjectUtil.getIProjectFromSelection();
		}
		if (project == null)
			return code;

		StringBuilder sb = new StringBuilder(6 * 1024);
		try {
			List<Syscode> syscode = SyscodeManager.getSyscode(code,
					DBConnectionUtil.getDBConnectionSyncExec(project));
			if (syscode.size() == 0) {
				sb.append(String.format("SYS_CODE: [%s] <br/>", code));
			} else {
				sb.append(String
						.format("SYS_CODE: [ %s ]  :<br/><br/><table><tr><th>CODE_VALUE</th><th>CODE_VALUE_NAME</th><th>LANG</th><th>ENABLED_FLAG</th></tr>",
								code));
				for (Syscode c : syscode) {
					sb.append(String
							.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
									c.getCode_value(), c.getCode_value_name(),
									c.getLanguage(), c.getEnabled_flag()));
				}
				sb.append("</table><br/>");
			}
		} catch (ApplicationException e) {
			e.printStackTrace();
			sb.append(String.format("SYS_CODE: [%s] <br/>", code));
		}
		return sb.toString();
	}
}
