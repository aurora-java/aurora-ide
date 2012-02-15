package aurora.ide.meta.gef.editors.source.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import aurora.ide.editor.textpage.format.JSBeautifier;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Renderer;

public class ScriptGenerator {

	private CompositeMap scriptMap;
	private CompositeMap viewMap;

	private List<String> functionNames = new ArrayList<String>();

	private Map<ButtonClicker, String> linkIDs = new HashMap<ButtonClicker, String>();

	private StringBuilder script = new StringBuilder();
	private ScreenGenerator sg;

	public ScriptGenerator(ScreenGenerator sg, CompositeMap script) {
		this.sg = sg;
		this.scriptMap = script;
		this.viewMap = script.getParent();
	}

	private String newLine(String s) {
		return s + "\n\r";
	}

	public String getScript() {
		return format(script.toString());
	}

	public String format(String s) {
		JSBeautifier bf = new JSBeautifier();
		String prefix = XMLOutputter.DEFAULT_INDENT
				+ XMLOutputter.DEFAULT_INDENT;
		String indent = XMLOutputter.DEFAULT_INDENT + prefix;
		String jsCodeNew = (XMLOutputter.LINE_SEPARATOR + bf.beautify(s,
				bf.opts))
				.replaceAll("\n", XMLOutputter.LINE_SEPARATOR + indent)
				+ XMLOutputter.LINE_SEPARATOR + prefix;
		// if (jsCodeNew.equals(jsCode))
		return jsCodeNew;
	}

	public Map<ButtonClicker, String> getLinkIDs() {
		return linkIDs;
	}

	public String genButtonClicker(ButtonClicker bc) {
		String functionName = getFunctionName(bc);
		functionName = genID(functionName, 0);
		String actionID = bc.getActionID();
		String script = "";
		if (ButtonClicker.B_SEARCH.equals(actionID)) {
			String datasetID = this.getDatasetID(bc);
			if (datasetID == null)
				return "";
			script = this.searchScript(functionName, datasetID);
		}
		if (ButtonClicker.B_SAVE.equals(actionID)) {
			String datasetID = this.getDatasetID(bc);
			script = this.saveScript(functionName, datasetID);
		}
		if (ButtonClicker.B_RESET.equals(actionID)) {
			String datasetID = this.getDatasetID(bc);
			script = this.resetScript(functionName, datasetID);
		}
		if (ButtonClicker.B_OPEN.equals(actionID)) {
			String linkID = this.getLinkID(bc);
			script = this.openScript(functionName, linkID);
		}
		if (ButtonClicker.B_CLOSE.equals(actionID)) {
			String windowID = this.getWindowID(bc);
			script = this.closeScript(functionName, windowID);
		}
		if (ButtonClicker.DEFAULT.equals(actionID)) {
			// custom
		}
		this.script.append(script);
		return functionName;
	}

	private String genID(String id, int i) {
		if (i > 0) {
			id = id + "_" + i;
		}
		if (functionNames.contains(id)) {
			i++;
			return genID(id, i);
		} else {
			functionNames.add(id);
			return id;
		}
	}

	private String getWindowID(ButtonClicker bc) {
		return bc.getCloseWindowID();
	}

	private String getLinkID(ButtonClicker bc) {
		String genLinkID = linkIDs.get(bc);
		if (genLinkID == null) {
			// modules/debug/bm.screen
			String openPath = bc.getOpenPath();
			Path path = new Path(openPath);
			String fileName = path.removeFileExtension().lastSegment();
			genLinkID = sg.getIdGenerator().genLinkID(fileName);
			linkIDs.put(bc, genLinkID);
		}
		return genLinkID;
	}

	private String getDatasetID(ButtonClicker bc) {
		String actionID = bc.getActionID();
		if (ButtonClicker.B_SEARCH.equals(actionID)
				|| ButtonClicker.B_SAVE.equals(actionID)
				|| ButtonClicker.B_RESET.equals(actionID)) {
			AuroraComponent targetComponent = bc.getTargetComponent();
			if (targetComponent instanceof Container) {
				return ((Container) targetComponent).getDataset().getId();
			}
		}
		return null;
	}

	private String getFunctionName(ButtonClicker bc) {
		String actionID = bc.getActionID();
		String pre = getFunctionPrefix(bc);
		String suffix = "";
		if (ButtonClicker.B_SEARCH.equals(actionID)
				|| ButtonClicker.B_SAVE.equals(actionID)
				|| ButtonClicker.B_RESET.equals(actionID)) {
			suffix = this.getDatasetID(bc);
		}
		if (ButtonClicker.B_OPEN.equals(actionID)) {
			suffix = this.getLinkID(bc);
		}
		if (ButtonClicker.B_CLOSE.equals(actionID)) {
			suffix = this.getWindowID(bc);
		}
		String javaBeanName = toJavaBeanName("_" + suffix);
		return pre + javaBeanName;
	}

	private String getFunctionPrefix(ButtonClicker bc) {
		String actionID = bc.getActionID();
		if (ButtonClicker.B_SEARCH.equals(actionID)) {
			return "query";
		}
		if (ButtonClicker.B_SAVE.equals(actionID)) {
			return "save";
		}
		if (ButtonClicker.B_RESET.equals(actionID)) {
			return "reset";
		}
		if (ButtonClicker.B_OPEN.equals(actionID)) {
			return "open";
		}
		if (ButtonClicker.B_CLOSE.equals(actionID)) {
			return "close";
		}
		if (ButtonClicker.DEFAULT.equals(actionID)) {
			return "";
		}

		return null;
	}

	public String toJavaBeanName(String _name) {
		StringBuilder sb = new StringBuilder();
		char[] charArray = _name.toCharArray();
		boolean up = false;
		for (char c : charArray) {
			if ('_' == c) {
				up = true;
				continue;
			}
			if (up) {
				sb.append(String.valueOf(c).toUpperCase());
				up = false;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public String genRenderer(Renderer renderer) {
		String functionName = "";
		return functionName;
	}

	public String searchScript(String functionName, String datasetId) {
		String s = "function functionName(){$('datasetId').query();}";
		s = s.replace("functionName", functionName);
		s = s.replace("datasetId", datasetId);
		return s;
	}

	public String resetScript(String functionName, String datasetId) {
		String s = " function functionName(){$('datasetId').reset();}";
		s = s.replace("functionName", functionName);
		s = s.replace("datasetId", datasetId);
		return s;
	}

	public String saveScript(String functionName, String datasetId) {
		String s = " function functionName(){$('datasetId').submit();}";
		s = s.replace("functionName", functionName);
		s = s.replace("datasetId", datasetId);
		return s;
	}

	public String openScript(String functionName, String linkId) {
		String s = " function functionName() {new Aurora.Window({id: 'linkId',url:$('linkId').getUrl(),title: 'Title',height: 435,width: 620});}";
		s = s.replace("functionName", functionName);
		s = s.replaceAll("linkId", linkId);
		return s;
	}

	public String closeScript(String functionName, String windowId) {
		String s = "function functionName(){$('windowId').close();}";
		s = s.replace("functionName", functionName);
		s = s.replace("windowId", windowId);
		return s;
	}

	public String customScript() {
		return "";
	}

}
