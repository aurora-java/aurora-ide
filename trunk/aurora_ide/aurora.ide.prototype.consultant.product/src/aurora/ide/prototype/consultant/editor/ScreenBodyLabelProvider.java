package aurora.ide.prototype.consultant.editor;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.util.MessageUtil;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.CheckBox;
import aurora.plugin.source.gen.screen.model.CustomICon;
import aurora.plugin.source.gen.screen.model.FieldSet;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.HBox;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.Label;
import aurora.plugin.source.gen.screen.model.QueryForm;
import aurora.plugin.source.gen.screen.model.QueryFormBody;
import aurora.plugin.source.gen.screen.model.QueryFormToolBar;
import aurora.plugin.source.gen.screen.model.RadioItem;
import aurora.plugin.source.gen.screen.model.TabFolder;
import aurora.plugin.source.gen.screen.model.TabItem;
import aurora.plugin.source.gen.screen.model.TextArea;
import aurora.plugin.source.gen.screen.model.Toolbar;
import aurora.plugin.source.gen.screen.model.ToolbarButton;
import aurora.plugin.source.gen.screen.model.VBox;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class ScreenBodyLabelProvider extends LabelProvider implements
		ILabelProvider {

	public String getText(Object element) {
		if (element instanceof AuroraComponent) {
			
			String componentType = ((AuroraComponent) element)
					.getComponentType();
			String r = ((AuroraComponent) element).getPrompt();
			if(Button.BUTTON.equals(componentType)){
				r = MessageUtil.getButtonText((Button) element);
			}

			String title = ((AuroraComponent) element)
					.getStringPropertyValue(ComponentProperties.title);
			r = r == null || "".equals(r)|| "null".equals(r) ? title : r;

			String text = ((AuroraComponent) element)
					.getStringPropertyValue(ComponentProperties.text);
			r = r == null || "".equals(r)|| "null".equals(r) ? text : r;
			
			r = r == null || "".equals(r)|| "null".equals(r) ? componentType : r;

			return r;
		}

		return "";
	}

	
	public Image getImage(Object element) {
		String componentType = "";
		if (element instanceof AuroraComponent) {
			componentType = ((AuroraComponent) element).getComponentType();
		}
		if (Input.TEXT.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/input_edit.png");
		}
		if (Input.DATE_PICKER.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/itembar_02.png");
		}
		if (Input.DATETIMEPICKER.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/itembar_02.png");
		}
		if (Input.Combo.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/itembar_01.png");
		}
		if (Input.LOV.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/itembar_03.png");
		}

		if (Input.NUMBER.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/input_edit.png");
		}
		if (CheckBox.CHECKBOX.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/checkbox_01.png");
		}
		if (Label.Label.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/label.png");
		}
		if (TextArea.TEXT_AREA.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/input_edit.png");
		}

		if (ToolbarButton.TOOLBAR_BUTTON.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/toolbar_btn_01.png");
		}

		if (RadioItem.RADIO_ITEM.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/radio_01.png");
		}

		if (CustomICon.CUSTOM_ICON.equals(componentType)) {
			return aurora.ide.prototype.consultant.product.Activator
					.getDefault().getImageRegistry()
					.get("/icons/full/obj16/image_obj.gif");
		}

		if (TabItem.TAB.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/tabitem.png");
		}
		if (TabFolder.TAB_PANEL.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/tabfolder.png");
		}
		if (FieldSet.FIELD_SET.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/fieldset.png");
		}
		if (Button.BUTTON.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/toolbar_btn_01.png");
		}
		if (GridColumn.GRIDCOLUMN.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/column.png");
		}
		if (Toolbar.TOOLBAR.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/toolbar.png");
		}
		if (Grid.GRID.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/grid.png");
		}
		if (Form.FORM.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/form.png");
		}
		if (HBox.H_BOX.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/hbox.png");
		}

		if (VBox.V_BOX.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/vbox.png");
		}

		if (QueryForm.QUERY_FORM.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/form.png");
		}

		if (QueryFormBody.FORM_BODY.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/form.png");
		}

		if (QueryFormToolBar.FORM_TOOLBAR.equals(componentType)) {
			return PrototypeImagesUtils.getImage("palette/form.png");
		}

		return null;
	}

}
