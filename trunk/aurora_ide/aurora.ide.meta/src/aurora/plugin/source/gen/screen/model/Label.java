package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;

//import org.eclipse.draw2d.geometry.Dimension;
//import org.eclipse.ui.views.properties.IPropertyDescriptor;
//
//import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class Label extends Input {


	public static final String RENDERER = "renderer";
//	protected static final IPropertyDescriptor PD_RENDERER = new StringPropertyDescriptor(
//			RENDERER, "renderer");
//	private static final IPropertyDescriptor[] pds = { PD_PROMPT, PD_NAME,
//			PD_WIDTH, PD_RENDERER };
//	private DatasetField datasetField = new DatasetField();

	public static String Label = "label";

	public Label() {
		setPrompt("label");
		this.setSize(120, 20);
		this.setComponentType(Label);
		this.setRenderer("");
	}

//	public Object getPropertyValue(Object propName) {
//		if (RENDERER.equals(propName)) {
//			return this.getRenderer();
//		}
//		return super.getPropertyValue(propName);
//	}

//	public void setPropertyValue(Object propName, Object val) {
//		if (RENDERER.equals(propName)) {
//			this.setRenderer(val.toString());
//		}
//		super.setPropertyValue(propName, val);
//	}

//	private String renderer = "";

	public String getRenderer() {
//		return renderer;
		return this.getStringPropertyValue(ComponentProperties.renderer);
		
	}

	public void setRenderer(String renderer) {
//		this.renderer = renderer;
//		this.firePropertyChange("renderer", "", renderer);
		this.setPropertyValue(ComponentProperties.renderer, renderer);
	}


//	public DatasetField getDatasetField() {
//		return datasetField;
//	}
//
//	public void setDatasetField(DatasetField datasetField) {
//		this.datasetField = datasetField;
//	}

}