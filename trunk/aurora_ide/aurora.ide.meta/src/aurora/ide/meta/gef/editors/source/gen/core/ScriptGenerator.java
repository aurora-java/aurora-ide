package aurora.ide.meta.gef.editors.source.gen.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import aurora.ide.api.javascript.JavascriptRhino;
import aurora.ide.editor.textpage.format.JSBeautifier;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ButtonClicker;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ILink;
import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.meta.gef.editors.models.link.Parameter;

public class ScriptGenerator {

	private CompositeMap scriptMap;
	private CompositeMap viewMap;

	private List<String> functionNames = new ArrayList<String>();

	private Map<Object, String> linkIDs = new HashMap<Object, String>();

	private StringBuilder script = new StringBuilder();
	private List<String> scriptList = new ArrayList<String>();
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

	public Map<Object, String> getLinkIDs() {
		return linkIDs;
	}

	public String genButtonClicker(ButtonClicker bc) {
		String functionName = getFunctionName(bc);
		if (null == functionName || "".equals(functionName))
			return "";
		String actionID = bc.getActionID();
		if (!ButtonClicker.B_CUSTOM.equals(actionID)) {
			functionName = uniqueID(functionName, 0);
		}
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
			script = setLinkParameters(bc, script, linkID);
		}
		if (ButtonClicker.B_CLOSE.equals(actionID)) {
			String windowID = this.getWindowID(bc);
			script = this.closeScript(functionName, windowID);
		}
		if (ButtonClicker.B_CUSTOM.equals(actionID)) {
			script = bc.getFunction();
		}

		appendScript(script);
		return functionName;
	}

	private String uniqueID(String id, int i) {
		String oldID = id;
		if (i > 0) {
			id = id + "_" + i;
		}
		if (functionNames.contains(id)) {
			i++;
			return uniqueID(oldID, i);
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
			if (fileName == null)
				return null;
			genLinkID = sg.getIdGenerator().genLinkID(fileName);
			linkIDs.put(bc, genLinkID);
		}
		return genLinkID;
	}

	private String getLinkID(Renderer bc) {
		String genLinkID = linkIDs.get(bc);
		if (genLinkID == null) {

			String fileName = getOpenFileName(bc);
			if (fileName == null)
				return null;
			genLinkID = sg.getIdGenerator().genLinkID(fileName);
			linkIDs.put(bc, genLinkID);
		}
		return genLinkID;
	}

	public String getOpenFileName(Renderer bc) {
		String openPath = bc.getOpenPath();
		IPath path = new Path(openPath);
		return getOpenFileName(path);
	}

	public String getOpenFileName(IPath path) {
		String fileName = path.removeFileExtension().lastSegment();
		return fileName;
	}

	private String getDatasetID(ButtonClicker bc) {
		String actionID = bc.getActionID();
		if (ButtonClicker.B_SEARCH.equals(actionID)
				|| ButtonClicker.B_SAVE.equals(actionID)
				|| ButtonClicker.B_RESET.equals(actionID)) {
			AuroraComponent targetComponent = bc.getTargetComponent();
			if (targetComponent instanceof Container) {
				CompositeMap dsMap = this.sg
						.fillDatasets((Container) targetComponent);
				if (dsMap != null)
					return dsMap.getString("id");
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
		if (ButtonClicker.B_CUSTOM.equals(actionID)) {
			JavascriptRhino js = new JavascriptRhino(bc.getFunction());
			suffix = js.getFirstFunctionName();
			return suffix;
		}
		if (null == suffix || "".equals(suffix))
			return null;
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
		if (ButtonClicker.B_CUSTOM.equals(actionID)) {
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
		String script = "";
		String type = renderer.getRendererType();
		if (Renderer.INNER_FUNCTION.equals(type)) {
			functionName = renderer.getFunctionName();
		}
		if (Renderer.PAGE_REDIRECT.equals(type)) {
			String openPath = renderer.getOpenPath();
			if (null == openPath || "".equals(openPath)) {
				return functionName;
			}
			String linkID = this.getLinkID(renderer);
			String javaBeanName = toJavaBeanName("_" + linkID);
			String openName = "open" + javaBeanName;
			openName = this.uniqueID(openName, 0);

			this.functionNames.add(openName);
			String openScript = this.openScript(openName, linkID);

			String openFileName = this.getOpenFileName(renderer);
			javaBeanName = toJavaBeanName("_" + openFileName);
			functionName = "assign" + javaBeanName;
			functionName = this.uniqueID(functionName, 0);
			String hrefScript = this.hrefScript(functionName,
					renderer.getLabelText(), openName, "");
			appendScript(hrefScript);
			openScript = setLinkParameters(renderer, openScript, linkID);
			appendScript(openScript);
		}
		if (Renderer.USER_FUNCTION.equals(type)) {
			script = renderer.getFunction();
			JavascriptRhino js = new JavascriptRhino(renderer.getFunction());
			functionName = js.getFirstFunctionName();
			appendScript(script);
		}
		this.functionNames.add(functionName);
		return functionName;
	}

	private String setLinkParameters(Renderer link, String script, String linkID) {
		List<Parameter> parameters = link.getParameters();
		StringBuilder sb = new StringBuilder("");
		for (Parameter parameter : parameters) {
			sb.append(linkID + ".set(" + parameter.getName() + "," + "record"
					+ ".get(" + parameter.getValue() + "));");
		}
		script.replace("#parameters#", sb.toString());
		return script;
	}

	private String setLinkParameters(ButtonClicker link, String script,
			String linkID) {
		//有参数的，且uip的，做一个list管理起来，在，sg生成完成后再生成。
		List<Parameter> parameters = link.getParameters();

		if (parameters.size() > 0) {
			Parameter p = parameters.get(0);
			Container container = p.getContainer();
			String findDatasetId = sg.findDatasetId(container);
			String ds = "var record = $('" + findDatasetId
					+ "').getCurrentRecord();";
			script.replace("#parameters#", ds + " #parameters# ");
		}

		StringBuilder sb = new StringBuilder("");
		for (Parameter parameter : parameters) {
			sb.append(linkID + ".set(" + parameter.getName() + "," + "record"
					+ ".get(" + parameter.getValue() + "));");
		}
		script.replace("#parameters#", sb.toString());
		return script;
	}

	// public String genTabRef(TabRef bc) {
	// String url = bc.getUrl();
	// List<Parameter> parameters = bc.getParameters();
	// IPath path = new Path(url);
	// if("uip".equals(path.getFileExtension())){
	// path = path.removeFileExtension().addFileExtension("screen");
	// }
	// String openFileName = this.getOpenFileName(path);
	// String functionName = getFunctionName(bc);
	// if (null == functionName || "".equals(functionName))
	// return "";
	// String actionID = bc.getActionID();
	// if (!ButtonClicker.B_CUSTOM.equals(actionID)) {
	// functionName = uniqueID(functionName, 0);
	// }
	// String script = "";
	// if (ButtonClicker.B_SEARCH.equals(actionID)) {
	// String datasetID = this.getDatasetID(bc);
	// if (datasetID == null)
	// return "";
	// script = this.searchScript(functionName, datasetID);
	// }
	// if (ButtonClicker.B_SAVE.equals(actionID)) {
	// String datasetID = this.getDatasetID(bc);
	// script = this.saveScript(functionName, datasetID);
	// }
	// if (ButtonClicker.B_RESET.equals(actionID)) {
	// String datasetID = this.getDatasetID(bc);
	// script = this.resetScript(functionName, datasetID);
	// }
	// if (ButtonClicker.B_OPEN.equals(actionID)) {
	// String linkID = this.getLinkID(bc);
	// script = this.openScript(functionName, linkID);
	// }
	// if (ButtonClicker.B_CLOSE.equals(actionID)) {
	// String windowID = this.getWindowID(bc);
	// script = this.closeScript(functionName, windowID);
	// }
	// if (ButtonClicker.B_CUSTOM.equals(actionID)) {
	// script = bc.getFunction();
	// }
	//
	// appendScript(script);
	// return functionName;
	// }

	public void appendScript(String script) {
		if (scriptList.contains(script)) {
			return;
		}
		scriptList.add(script);
		this.script.append(script);

	}

	public String hrefScript(String functionName, String labelText,
			String newWindowName, String parameter) {
		String s = "function #functionName#(value,record, name){return '<a href=\"javascript:#newWindowName#(record)\">#LabelText#</a>';}";
		s = s.replace("#functionName#", functionName);
		s = s.replace("#newWindowName#", newWindowName);
		s = s.replace("#LabelText#", labelText);
		return s;
	}

	public String searchScript(String functionName, String datasetId) {
		String s = "function #functionName#(){$('#datasetId#').query();}";
		s = s.replace("#functionName#", functionName);
		s = s.replace("#datasetId#", datasetId);
		return s;
	}

	public String resetScript(String functionName, String datasetId) {
		String s = " function #functionName#(){$('#datasetId#').reset();}";
		s = s.replace("#functionName#", functionName);
		s = s.replace("#datasetId#", datasetId);
		return s;
	}

	public String saveScript(String functionName, String datasetId) {
		String s = " function #functionName#(){$('#datasetId#').submit();}";
		s = s.replace("#functionName#", functionName);
		s = s.replace("#datasetId#", datasetId);
		return s;
	}

	public String openScript(String functionName, String linkId) {
		String s = " function #functionName#() {var linkUrl = $('#linkId#').getUrl(); #parameters# new Aurora.Window({id: '#windowId#',url:linkUrl,title: 'Title',height: 435,width: 620});}";
		s = s.replace("#functionName#", functionName);
		String windowID = sg.getIdGenerator().genWindowID(linkId);
		s = s.replaceAll("#windowId#", windowID);
		s = s.replaceAll("#linkId#", linkId);

		return s;
	}

	public String closeScript(String functionName, String windowId) {
		String s = "function #functionName#(){$('#windowId#').close();}";
		s = s.replace("#functionName#", functionName);
		s = s.replace("#windowId#", windowId);
		return s;
	}

}
