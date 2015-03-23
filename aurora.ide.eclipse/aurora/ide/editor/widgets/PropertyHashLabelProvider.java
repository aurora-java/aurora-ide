package aurora.ide.editor.widgets;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.widgets.core.CategoryLabel;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;

import uncertain.schema.Attribute;
import uncertain.schema.editor.AttributeValue;

public class PropertyHashLabelProvider extends BaseLabelProvider implements
		ITableLabelProvider, ITableColorProvider {

	public PropertyHashLabelProvider() {
		super();
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		// String imagePath = LocaleMessage.getString("property.icon");
		if (columnIndex == 0) {
			if (element instanceof CategoryLabel) {
				// return
				// AuroraPlugin.getImageDescriptor(LocaleMessage.getString("category.icon"))
				// .createImage();
				return ImagesUtils.getImage("category.gif");
			}
			return ImagesUtils.getImage("attribute.gif");
			// return AuroraPlugin.getImageDescriptor(imagePath).createImage();
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {

		if (!(element instanceof AttributeValue)) {
			return element.toString();
		}

		AttributeValue av = (AttributeValue) element;

		if (element instanceof CategoryLabel) {
			if (columnIndex == 0)
				return av.getValueString();
			else if (columnIndex == 1) {
				return "";
			}
		}
		if (av.getAttribute() == null)
			return av.getValueString();

		if (columnIndex == 0) {
			Attribute attr = av.getAttribute();
			String text = attr.getLocalName();
			if (attr.getUse() != null && attr.getUse().equals("required"))
				text = " * " + text;
			return text;
		} else if (columnIndex == 2) {
			Attribute attr = av.getAttribute();
			String document = attr.getDocument();
			return document;
		} else if (columnIndex == 1) {
			return av.getValueString();
		} else
			return "";
	}

	public Color getBackground(Object element, int columnIndex) {
		if (columnIndex == 0)
			if (rowNum % 2 == 0)
				rowNum++;
			else
				rowNum--;
		return (rowNum == 0) ? COLOR_EVEN : COLOR_ODD;
	}

	private int rowNum = 0;
	private Color COLOR_ODD = new Color(null, 245, 255, 255);
	private Color COLOR_EVEN = new Color(null, 255, 255, 255);

	public Color getForeground(Object element, int columnIndex) {
		return null;
	}
}
