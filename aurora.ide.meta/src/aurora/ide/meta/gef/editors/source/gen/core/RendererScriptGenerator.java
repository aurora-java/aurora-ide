package aurora.ide.meta.gef.editors.source.gen.core;

import java.util.List;

import aurora.ide.meta.gef.editors.models.Renderer;
import aurora.ide.meta.gef.editors.models.link.Parameter;

public class RendererScriptGenerator {

	private ScreenGenerator sg;

	public RendererScriptGenerator(ScreenGenerator sg) {
		this.sg = sg;
	}

	public String hrefScript(String functionName, String labelText,
			String newWindowName, String parameter) {
		String s = "function #functionName#(value,record, name){return '<a href=\"javascript:#newWindowName#(#parameters#)\">#LabelText#</a>';}";
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
		String s = " function #functionName#(#parameter_keys#) {var linkUrl = $('#linkId#'); #parameters# new Aurora.Window({id: '#windowId#',url:linkUrl.getUrl(),title: 'Title',height: 435,width: 620});}";
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

	public String[] getParametersDetail(Renderer link, String linkVar) {

		StringBuilder refParameters = new StringBuilder("");
		StringBuilder vars = new StringBuilder("");
		StringBuilder openParameters = new StringBuilder("");
		// '<a
		// href=\"javascript:#newWindowName#(#parameters#)\">#LabelText#</a>';
		// '<a
		// href="javascript:openCreateDeptEmpLink('+record.get('dept3310_pk')+')">查询员工</a>';
		List<Parameter> parameters = link.getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			Parameter p = parameters.get(i);
			refParameters.append("'+record.get('");
			refParameters.append(p.getValue());
			refParameters.append("')");
			if (i == parameters.size() - 1) {
				refParameters.append("+");
			}
			refParameters.append("'");
			String key = "v" + i;
			vars.append(key);
			if (i < parameters.size() - 1) {
				vars.append(",");
			}
			String op = addParameter(linkVar, p, key);
			openParameters.append(op);
		}
		return new String[] { refParameters.toString(), vars.toString(),
				openParameters.toString() };

		// StringBuilder sb = new StringBuilder("");
		// for (Parameter parameter : parameters) {
		// sb.append(addParameter("linkUrl", parameter));
		// }
		// script = script.replace("#parameters#", sb.toString());
		// return script;
		//
	}

	// public String addParameter(String linkVar, Parameter parameter) {
	// return linkVar + ".set('" + parameter.getName() + "'," + "record"
	// + ".get('" + parameter.getValue() + "'));";
	// }

	public String addParameter(String linkVar, Parameter parameter, String key) {
		return linkVar + ".set('" + parameter.getName() + "'," + key + ");";
	}

	public String buildHrefScript(String hrefScript, String[] parametersDetail) {
		hrefScript = hrefScript.replace("#parameters#", parametersDetail[0]);
		return hrefScript;
	}

	public String buildOpenScript(String openScript, String[] parametersDetail) {
		openScript = openScript
				.replace("#parameter_keys#", parametersDetail[1]);
		openScript = openScript.replace("#parameters#", parametersDetail[2]);
		return openScript;
	}

}
