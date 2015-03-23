package aurora.ide.view;

import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.helpers.CompositeMapUtil;

public class ViewNode {
	private CompositeMap cm;
	private String promptsCode;
	private Attribute att;
	private String promptPrefix = "";
	private String zhsPrompt;
	private String usPrompt;

	public ViewNode(CompositeMap map, Attribute attrib, String promptPrefix) {
		this.setCompositeMap(map);
		this.setAttribute(attrib);
		this.setPromptPrefix(promptPrefix);
		this.setPromptsCode(getDefaultPromptsCode(map));
		this.setUsPrompt("");
		this.setZhsPrompt(this.getPromptAttribute());
	}

	private String getDefaultPromptsCode(CompositeMap map) {
		if ("".equals(promptPrefix))
			return getNameAttribute().toUpperCase();
		return (promptPrefix + "." + getNameAttribute()).toUpperCase();
	}

	public String getPromptsCode() {
		return promptsCode;
	}

	public void setPromptsCode(String promptsCode) {
		this.promptsCode = promptsCode;
	}

	public CompositeMap getCompositeMap() {
		return cm;
	}

	public void setCompositeMap(CompositeMap cm) {
		this.cm = cm;
	}

	public String getElementRawName() {
		return cm.getRawName();
	}

	public String getNameAttribute() {
		return cm.getString("name", "NONE");
	}

	public String getPromptAttribute() {
		return CompositeMapUtil.getValueIgnoreCase(att, cm);
	}

	public Attribute getAttribute() {
		return att;
	}

	public void setAttribute(Attribute att) {
		this.att = att;
	}

	private void setPromptPrefix(String promptPrefix) {
		this.promptPrefix = promptPrefix;
	}

	private void setPromptAttribute(String text) {
		// removeKey();
		cm.putString(att.getName(), text);
	}

	public void applyCode() {
		setPromptAttribute(getPromptsCode());
	}

	private void removeKey() {
		Set keySet = cm.keySet();
		for (Object object : keySet) {
			if (object instanceof String
					&& ((String) object).equalsIgnoreCase(att.getName())) {
				cm.remove(object);
			}
		}
	}

	public String getZhsPrompt() {
		return zhsPrompt;
	}

	public void setZhsPrompt(String zhsPrompt) {
		this.zhsPrompt = zhsPrompt;
	}

	public String getUsPrompt() {
		return usPrompt;
	}

	public void setUsPrompt(String usPrompt) {
		this.usPrompt = usPrompt;
	}
}
