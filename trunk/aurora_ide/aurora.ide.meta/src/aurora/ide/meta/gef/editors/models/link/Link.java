package aurora.ide.meta.gef.editors.models.link;

import aurora.ide.meta.gef.editors.models.AuroraComponent;

public class Link extends AuroraComponent {

	// <ss:element name="a:link">
	// <ss:attributes>
	// <ss:attribute name="id" document="唯一标志" type=""/>
	// <ss:attribute name="model" document="bm文件的pkg路径" type="a:URLReference"/>
	// <ss:attribute name="modelaction" document="对应bm的action" type=""/>
	// <ss:attribute name="url" document="screen文件的路径" type="a:URLReference"/>
	// </ss:attributes>
	// </ss:element>

	private String model;
	private String modelaction;
	private String url;

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModelaction() {
		return modelaction;
	}

	public void setModelaction(String modelaction) {
		this.modelaction = modelaction;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
