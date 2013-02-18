package aurora.ide.meta.gef.editors.actions;

import java.util.ArrayList;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;

import aurora.ide.meta.extensions.ComponentFactory;
import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.VBox;
import aurora.ide.meta.gef.editors.models.commands.ChangeTypeCommand;

public class TypeChangeUtil {
	private CommandStack commandStack;

	public TypeChangeUtil(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	public Action[] getActionFor(AuroraComponent ac) {
		ArrayList<Action> als = new ArrayList<Action>();
		if (ac instanceof Input) {
			String[] types = { Input.TEXT, Input.NUMBER, Input.Combo,
					Input.LOV, Input.DATE_PICKER, Input.DATETIMEPICKER };
			if (ac instanceof CheckBox) {
				for (String s : types) {
					InputTypeChangeAction action = new InputTypeChangeAction(s);
					action.setInput((Input) ac);
					als.add(action);
				}
			} else {
				for (String s : types) {
					if (s.equals(ac.getType()))
						s = CheckBox.CHECKBOX;
					InputTypeChangeAction action = new InputTypeChangeAction(s);
					action.setInput((Input) ac);
					als.add(action);
				}
			}
		} else if (ac instanceof Form) {
			BoxChangeAction action = new BoxChangeAction("fieldSet");
			action.setModelType((Form) ac, FieldSet.class);
			als.add(action);
		} else if (ac instanceof FieldSet) {
			BoxChangeAction action = new BoxChangeAction("form");
			action.setModelType((FieldSet) ac, Form.class);
			als.add(action);
		} else if (ac instanceof HBox) {
			BoxChangeAction action = new BoxChangeAction("VBox");
			action.setModelType((HBox) ac, VBox.class);
			als.add(action);
		} else if (ac instanceof VBox) {
			BoxChangeAction action = new BoxChangeAction("HBox");
			action.setModelType((VBox) ac, HBox.class);
			als.add(action);
		}
		return als.toArray(new Action[als.size()]);
	}

	/**
	 * 
	 * @author jessen
	 * 
	 */
	class InputTypeChangeAction extends TypeChangeAction {
		/**
		 * input type change.e.g. text->lov
		 */
		public static final int TYPE_CHANGE_MODE1 = 1;
		/**
		 * input->checkBox
		 */
		public static final int TYPE_CHANGE_MODE2 = 2;
		/**
		 * checkbox->input
		 */
		public static final int TYPE_CHANGE_MODE3 = 4;
		private String newType;
		private String oldType;
		private Input input;
		private Input newInput;
		private int type_change_mode = TYPE_CHANGE_MODE1;

		public InputTypeChangeAction(String text) {
			super(text);
			this.newType = text;
			String imageKey = "";
			if (Input.Combo.equals(text))
				imageKey = "palette/itembar_01.png";
			else if (Input.DATE_PICKER.equals(text)
					|| Input.DATETIMEPICKER.equals(text))
				imageKey = "palette/itembar_02.png";
			else if (Input.LOV.endsWith(text))
				imageKey = "palette/itembar_03.png";
			else if (Input.NUMBER.endsWith(text))
				imageKey = "palette/itembar_05.png";
			else if (Input.TEXT.equals(text))
				imageKey = "palette/itembar_04.png";
			else if (CheckBox.CHECKBOX.equals(text))
				imageKey = "palette/checkbox_01.png";
			setImageDescriptor(ImagesUtils.getImageDescriptor(imageKey));
		}

		public void setInput(Input input) {
			this.input = input;
			oldType = input.getType();
			if (CheckBox.CHECKBOX.equals(newType)) {
				type_change_mode = TYPE_CHANGE_MODE2;
			} else if (input.getClass().equals(CheckBox.class))
				type_change_mode = TYPE_CHANGE_MODE3;
		}

		public void run() {
			ChangeTypeCommand cmd = new ChangeTypeCommand(this);
			commandStack.execute(cmd);
		}

		public void apply() {
			newInput = (Input)ComponentFactory.createComponent(newType);
			
//			newInput = null;
//			if (type_change_mode == TYPE_CHANGE_MODE1) {
//				input.setType(newType);
//				return;
//			} else if (type_change_mode == TYPE_CHANGE_MODE2) {
//				newInput = new CheckBox();
//			} else if (type_change_mode == TYPE_CHANGE_MODE3) {
//				newInput = new Input();
//				newInput.setType(newType);
//			}
			newInput.setReadOnly(input.isReadOnly());
			newInput.setRequired(input.isRequired());
			newInput.setPrompt(input.getPrompt());
			newInput.setName(input.getName());
			newInput.setEmptyText(input.getEmptyText());
			newInput.setTypeCase(input.getTypeCase());

			Container cont = input.getParent();
			int idx = cont.getChildren().indexOf(input);
			cont.removeChild(idx);
			cont.addChild(newInput, idx);
		}

		@Override
		public void unApply() {
			if (type_change_mode == TYPE_CHANGE_MODE1) {
				input.setType(oldType);
				return;
			}
			Container cont = this.newInput.getParent();
			int idx = cont.getChildren().indexOf(this.newInput);
			cont.removeChild(idx);
			cont.addChild(input, idx);
			input.setReadOnly(newInput.isReadOnly());
			input.setRequired(newInput.isRequired());
			input.setPrompt(newInput.getPrompt());
			input.setName(newInput.getName());
			input.setEmptyText(newInput.getEmptyText());
			input.setTypeCase(newInput.getTypeCase());
		}
	}

	/**
	 * 
	 * @author jessen
	 * 
	 */
	class BoxChangeAction extends TypeChangeAction {
		private BOX oldCmp;
		private BOX newCmp;
		private Class<? extends BOX> newCls;

		public BoxChangeAction(String string) {
			super(string);
			String imgKey = "";
			if ("form".equals(string))
				imgKey = "palette/form.png";
			else if ("fieldSet".equalsIgnoreCase(string))
				imgKey = "palette/fieldset.png";
			else if ("hBox".equalsIgnoreCase(string))
				imgKey = "palette/hbox.png";
			else if ("vBox".equalsIgnoreCase(string))
				imgKey = "palette/vbox.png";
			setImageDescriptor(ImagesUtils.getImageDescriptor(imgKey));
		}

		public void setModelType(BOX oldType, Class<? extends BOX> newType) {
			oldCmp = oldType;
			newCls = newType;
		}

		@Override
		public void run() {
			ChangeTypeCommand cmd = new ChangeTypeCommand(this);
			commandStack.execute(cmd);
		}

		@Override
		public void apply() {
			try {
				newCmp = newCls.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (newCmp == null)
				return;
			Container cont = oldCmp.getParent();
			int idx = cont.getChildren().indexOf(oldCmp);
			cont.removeChild(idx);

			newCmp.setRow(oldCmp.getRow());
			newCmp.setCol(oldCmp.getCol());
			newCmp.setPrompt(oldCmp.getPrompt());
			newCmp.setTitle(oldCmp.getTitle());
			newCmp.setSectionType(oldCmp.getSectionType());
			for (AuroraComponent a : oldCmp.getChildren())
				newCmp.addChild(a);
			cont.addChild(newCmp, idx);
		}

		@Override
		public void unApply() {
			Container cont = this.newCmp.getParent();
			int idx = cont.getChildren().indexOf(this.newCmp);
			cont.removeChild(idx);
			oldCmp.getChildren().clear();
			for (AuroraComponent a : newCmp.getChildren())
				oldCmp.addChild(a);
			cont.addChild(oldCmp, idx);
			oldCmp.setRow(newCmp.getRow());
			oldCmp.setCol(newCmp.getCol());
			oldCmp.setPrompt(newCmp.getPrompt());
			oldCmp.setTitle(newCmp.getTitle());
			oldCmp.setSectionType(newCmp.getSectionType());
		}
	}
}
