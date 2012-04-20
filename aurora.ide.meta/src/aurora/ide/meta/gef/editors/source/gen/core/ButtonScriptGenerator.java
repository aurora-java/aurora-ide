package aurora.ide.meta.gef.editors.source.gen.core;


public class ButtonScriptGenerator {
	
	private ScreenGenerator sg;

	public ButtonScriptGenerator(ScreenGenerator sg){
		this.sg = sg;
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
		String s = " function #functionName#() {var linkUrl = $('#linkId#'); #parameters# new Aurora.Window({id: '#windowId#',url:linkUrl.getUrl(),title: 'Title',height: 435,width: 620});}";
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
