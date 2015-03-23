package aurora.ide.bpmn.model.ex;

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.model.ModelDecorator;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.runtime.BaseRuntimeExtensionDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ModelExtensionDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class AuroraEXModel extends BaseRuntimeExtensionDescriptor {
	// {
	// TargetRuntime.getCurrentRuntime().addModelExtension(this);
	// }

	public static final String OBJ_NAME = "auroraEx";

	private String name;

	@Override
	public String getExtensionName() {
		return "AuroraEX";
	}

	public static EObject createExtObject(BaseElement element) {
		EObject style = null;
		try {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter
					.adapt(element);
			ModelExtensionDescriptor med = TargetRuntime.getDefaultRuntime()
					.getModelExtensionDescriptor(element);
			List<ModelExtensionDescriptor> modelExtensionDescriptors = TargetRuntime
					.getDefaultRuntime().getModelExtensionDescriptors();
			for (ModelExtensionDescriptor modelExtensionDescriptor : modelExtensionDescriptors) {
				String name2 = modelExtensionDescriptor.getName();
				if ("auroraEX".equals(name2)) {
					EStructuralFeature styleFeature = modelExtensionDescriptor
							.getModelDecorator().getEStructuralFeature(element,
									OBJ_NAME);
					if (styleFeature != null) {
						AuroraEXModel ss = getShapeStyle(element);
						style = (EObject) adapter.getFeatureDescriptor(
								styleFeature).getValue();
						if (style == null) {
							// this object does not have a <style> extension
							// element yet
							// so create one
							// and initialize it from the User Preference store
							style = med.createObject((EClass) styleFeature
									.getEType());
							setShapeStyle(element, style, ss);
							// add it to the BaseElement extension values
							InsertionAdapter.add(element, styleFeature, style);
						} else {
							setShapeStyle(element, style, ss);
						}
					}
				}
			}

		} catch (Exception e) {
			// ignore exceptions - the BaseElement doesn't have a <style>
			// extension element
			e.printStackTrace();
		}
		return style;
	}

	private static void setStyleValue(EObject style, String feature,
			Object value) {
		try {
			EStructuralFeature f = style.eClass()
					.getEStructuralFeature(feature);
			Object oldValue = style.eGet(f);
			if (value != null && !value.equals(oldValue))
				style.eSet(f, value);
		} catch (Exception e) {
		}
	}

	public static AuroraEXModel getShapeStyle(BaseElement element) {
		AuroraEXModel auroraEXModel = new AuroraEXModel();
		auroraEXModel.setName("hi i am here.");
		return auroraEXModel;

		// Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(element);
		// ShapeStyle ss = preferences.getShapeStyle(element);
		// ss = new ShapeStyle(ss); // makes a copy of the value in Preference
		// Store
		//
		// EObject style = getStyleObject(element);
		// if (style!=null) {
		// style.eSetDeliver(false);
		//
		// RGB shapeForeground = (RGB)
		// getStyleValue(style,STYLE_SHAPE_FOREGROUND);
		// RGB shapeBackground = (RGB)
		// getStyleValue(style,STYLE_SHAPE_BACKGROUND);
		// RGB labelForeground = (RGB)
		// getStyleValue(style,STYLE_LABEL_FOREGROUND);
		// FontData labelFont = (FontData)
		// getStyleValue(style,STYLE_LABEL_FONT);
		// EEnumLiteral labelPosition = (EEnumLiteral)
		// getStyleValue(style,STYLE_LABEL_POSITION);
		// EEnumLiteral routingStyle = (EEnumLiteral)
		// getStyleValue(style,STYLE_ROUTING_STYLE);
		//
		// if (shapeBackground!=null) {
		// IColorConstant cc = ShapeStyle.RGBToColor(shapeBackground);
		// ss.setShapeBackground(cc);
		// ss.setShapePrimarySelectedColor(StyleUtil.shiftColor(cc, 32));
		// ss.setShapeSecondarySelectedColor(StyleUtil.shiftColor(cc, -32));
		// }
		// else
		// setStyleValue(style, STYLE_SHAPE_BACKGROUND,
		// ShapeStyle.colorToRGB(ss.getShapeBackground()));
		//
		// if (shapeForeground!=null)
		// ss.setShapeForeground(ShapeStyle.RGBToColor(shapeForeground));
		// else
		// setStyleValue(style, STYLE_SHAPE_FOREGROUND,
		// ShapeStyle.colorToRGB(ss.getShapeForeground()));
		//
		// if (labelForeground!=null)
		// ss.setLabelForeground(ShapeStyle.RGBToColor(labelForeground));
		// else
		// setStyleValue(style, STYLE_LABEL_FOREGROUND,
		// ShapeStyle.colorToRGB(ss.getLabelForeground()));
		//
		// if (labelFont!=null) {
		// // roundabout way to get the Diagram for a Business Object:
		// // see {@link DIUtils} for details.
		// Resource res = ExtendedPropertiesAdapter.getResource(element);
		// List<PictogramElement> pes =
		// DIUtils.getPictogramElements(res.getResourceSet(), element);
		// if (pes.size()>0) {
		// Diagram diagram =
		// Graphiti.getPeService().getDiagramForPictogramElement(pes.get(0));
		// ss.setLabelFont(ShapeStyle.toGraphitiFont(diagram, labelFont));
		// }
		// }
		// else
		// setStyleValue(style, STYLE_LABEL_FONT,
		// ShapeStyle.toFontData(ss.getLabelFont()));
		//
		// if (labelPosition!=null)
		// ss.setLabelPosition((LabelPosition)fromEENumLiteral(element,
		// labelPosition));
		// else
		// setStyleValue(style, STYLE_LABEL_POSITION, toEENumLiteral(element,
		// ss.getLabelPosition()));
		//
		// if (routingStyle!=null)
		// ss.setRoutingStyle( (RoutingStyle)fromEENumLiteral(element,
		// routingStyle) );
		// else
		// setStyleValue(style, STYLE_ROUTING_STYLE, toEENumLiteral(element,
		// ss.getRoutingStyle()));
		//
		// style.eSetDeliver(true);
		// }
		// return ss;
	}

	public void setName(String string) {
		this.name = string;
	}

	public String getName() {
		return name;
	}

	public static Object getStyleValue(EObject style, String feature) {
		EStructuralFeature f = style.eClass().getEStructuralFeature(feature);
		if (f != null && style.eIsSet(f))
			return style.eGet(f);
		return null;
	}

	public Object getStyleValue(BaseElement element, String feature) {
		// if (STYLE_SHAPE_FOREGROUND.equals(feature))
		// return colorToRGB(getShapeForeground());
		// if (STYLE_SHAPE_BACKGROUND.equals(feature))
		// return colorToRGB(getShapeBackground());
		// if (STYLE_LABEL_FOREGROUND.equals(feature))
		// return colorToRGB(getLabelForeground());
		// if (STYLE_LABEL_BACKGROUND.equals(feature))
		// return null;
		// if (STYLE_LABEL_FONT.equals(feature))
		// return ShapeStyle.toFontData(getLabelFont());
		// if (STYLE_LABEL_POSITION.equals(feature))
		// return ShapeStyle.toEENumLiteral(element, getLabelPosition());
		// if (STYLE_ROUTING_STYLE.equals(feature))
		// return ShapeStyle.toEENumLiteral(element, getRoutingStyle());
		// return null;
		return "hello";
	}

	public static void setShapeStyle(BaseElement element, EObject style,
			AuroraEXModel ss) {
		setStyleValue(style, "name", ss.getName());

		// if (hasStyle(element)) {
		// if (style==null)
		// style = getStyleObject(element);
		//
		// setStyleValue(style, STYLE_SHAPE_FOREGROUND,
		// ShapeStyle.colorToRGB(ss.getShapeForeground()));
		// setStyleValue(style, STYLE_SHAPE_BACKGROUND,
		// ShapeStyle.colorToRGB(ss.getShapeBackground()));
		// setStyleValue(style, STYLE_LABEL_FOREGROUND,
		// ShapeStyle.colorToRGB(ss.getLabelForeground()));
		// setStyleValue(style, STYLE_LABEL_FONT,
		// ShapeStyle.toFontData(ss.getLabelFont()));
		// setStyleValue(style, STYLE_LABEL_POSITION, toEENumLiteral(element,
		// ss.getLabelPosition()));
		// setStyleValue(style, STYLE_ROUTING_STYLE, toEENumLiteral(element,
		// ss.getRoutingStyle()));
		// }
		// else {
		// Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(element);
		// preferences.setShapeStyle(element,ss);
		// }
	}
	public static EObject getStyleObject(BaseElement element) {

		EObject style = null;
		try {
			
			
			List<ModelExtensionDescriptor> modelExtensionDescriptors = TargetRuntime
					.getDefaultRuntime().getModelExtensionDescriptors();
			for (ModelExtensionDescriptor modelExtensionDescriptor : modelExtensionDescriptors) {
				String name2 = modelExtensionDescriptor.getName();
				if ("auroraEX".equals(name2)) {
					EStructuralFeature styleFeature = modelExtensionDescriptor
							.getModelDecorator().getEStructuralFeature(element,
									OBJ_NAME);
					if (styleFeature!=null) {
						ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(element);
						if (adapter!=null)
							style = (EObject)adapter.getFeatureDescriptor(styleFeature).getValue();
					}
				}}
			
		}
		catch (Exception e) {
			// ignore exceptions - the BaseElement doesn't have a <style> extension element
		}
		return style;
	}
}
