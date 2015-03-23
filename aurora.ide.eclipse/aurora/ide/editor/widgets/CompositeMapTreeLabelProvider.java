package aurora.ide.editor.widgets;


import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.schema.Element;

public class CompositeMapTreeLabelProvider extends BaseLabelProvider implements
		ILabelProvider {

	public CompositeMapTreeLabelProvider() {
		super();
	}

	public Image getImage(Object element) {
		CompositeMap elemenntCm = (CompositeMap) element;
//		Element ele = LoadSchemaManager.getSchemaManager().getElement(
//				elemenntCm);
		Element ele = CompositeMapUtil.getElement(elemenntCm);
		if (ele != null) {
			if (ele.isArray()) {
//				return AuroraPlugin.getImageDescriptor(
//						LocaleMessage.getString("array.icon")).createImage();
				return ImagesUtils.getImage("array.gif");
			}
		}
//		String defaultPath = LocaleMessage.getString("element.icon");
//		return AuroraPlugin.getImageDescriptor(defaultPath).createImage();
		return ImagesUtils.getImage("element.gif");
	}

	/**
	 * Returns the text for the label of the given element.
	 * 
	 * @param obj
	 *            the element for which to provide the label text
	 * @return the text string used to label the element, or <code>null</code>
	 *         if there is no text label for the given object
	 */
	public String getText(Object obj) {
		String elementText = null;
		CompositeMap elemenntCm = (CompositeMap) obj;

		String tagName = elemenntCm.getRawName();
		String elementName = getElementName(elemenntCm);
		if (elementName != null && !elementName.equals(""))
			elementText = elementName;
		else
			elementText = tagName;
//		Element element = LoadSchemaManager.getSchemaManager().getElement(
//				elemenntCm);
		Element element = CompositeMapUtil.getElement(elemenntCm);
		if (element != null) {
			if (element.isArray()) {
				int nodes = elemenntCm.getChildsNotNull().size();
				return "[" + nodes + "]" + elementText;
			}

		}

		return elementText;
	}

	private String getElementName(CompositeMap element) {

		String tagName = element.getRawName();
//		Element elm = LoadSchemaManager.getSchemaManager().getElement(element);
		Element elm = CompositeMapUtil.getElement(element);
		String elemDesc = null;
		if (elm != null && !elm.isArray()) {
			if (elm.getDisplayMask() != null) {
				elemDesc = TextParser.parse(elm.getDisplayMask(), element);
			}
			if (elemDesc != null)
				tagName = tagName + " " + elemDesc;
		}
		if (elemDesc == null) {
			if (element.get("id") != null) {
				elemDesc = element.getString("id");
			} else if (element.get("name") != null)
				elemDesc = element.get("name").toString();
			else if (element.get("Name") != null)
					elemDesc = element.get("Name").toString();
			if (elemDesc != null)
				tagName = tagName + " (" + elemDesc + ")";
		}

		return tagName;
	}
}
