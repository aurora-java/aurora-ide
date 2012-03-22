package aurora.ide.meta.gef.designer.editor;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;

public class BmFieldLabelProvider extends LabelProvider implements
		IColorProvider {
	private static Color field_color = new Color(null, 0, 0, 0);
	private static Color ref_field_color = new Color(null, 50, 50, 50);

	public Color getForeground(Object element) {
		CompositeMap map = (CompositeMap) element;
		return map.getName().equals("field") ? field_color : ref_field_color;
	}

	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Image getImage(Object element) {
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		CompositeMap map = (CompositeMap) element;
		String p = map.getString("prompt");
		if (p == null)
			p = map.getString("PROMPT");
		if (p == null)
			p = "";
		String n = map.getString("name");
		return p + " [ " + n + " ] - " + map.getName();
	}
}
