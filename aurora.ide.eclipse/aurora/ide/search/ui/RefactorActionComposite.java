package aurora.ide.search.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.search.action.AddAttributeAction;
import aurora.ide.search.action.ChangeElementAction;
import aurora.ide.search.action.IActionChangedListener;
import aurora.ide.search.action.ISearchResultPageAction;
import aurora.ide.search.action.RemoveAttributeAction;
import aurora.ide.search.action.RemoveElementAction;
import aurora.ide.search.action.ReplaceAttributeAction;

public class RefactorActionComposite extends Composite implements
		ISelectionChangedListener, IActionChangedListener {
	private List<Button> radios = new ArrayList<Button>();

	private ISearchResultPageAction[] actions = new ISearchResultPageAction[6];

	private Button refactorSelection;

	private Button refactorAll;

	public RefactorActionComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		createActions(parent, style);
	}

	private void createActions(Composite parent, int style) {
		Group radioGroup = new Group(this, SWT.NONE);
		radioGroup.setLayout(new GridLayout(5, false));
		radioGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, true, 2, 1));

		this.createRemove(radioGroup, radioGroup);
		this.createSetAttribute(radioGroup, radioGroup);
		this.createChangeElement(radioGroup, radioGroup);
		this.createRemoveAttribute(radioGroup, radioGroup);
		this.createReplaceAttributeName(radioGroup, radioGroup);
		this.createReplaceAttributeValue(radioGroup, radioGroup);

		refactorSelection = new Button(this, SWT.PUSH);
		refactorSelection.setText("Refactor Selection");
		refactorSelection.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				getAction().runSelection();
			}
		});
		refactorAll = new Button(this, SWT.PUSH);
		refactorAll.setText("Refactor All");
		refactorAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				getAction().runAll();
			}
		});
		this.updateButton(false, false);
	}

	private void createRemove(Group radioGroup, Composite actionParent) {
		Button radio = createButton(radioGroup, SWT.RADIO, "Remove Element", 0,
				true);
		radios.add(radio);
		addSelectionListener(radio);
		Label label = new Label(actionParent, SWT.LEFT);
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false,
				false, 4, 1));
		actions[0] = new RemoveElementAction(this.getShell());
	}

	private void addSelectionListener(Button radio) {
		radio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISearchResultPageAction action = getAction();
				actionChanged(action);
			}
		});
	}

	private void createSetAttribute(Group radioGroup, Composite actionParent) {
		Button radio = createButton(radioGroup, SWT.RADIO, "Add Attribute:", 1,
				false);
		radios.add(radio);
		addSelectionListener(radio);
		Text name = createInputBox(actionParent, "attribute name");
		Text value = createInputBox(actionParent, "value");
		AddAttributeAction action = new AddAttributeAction(this.getShell());
		action.setControl(name, value);
		action.addActionChangedListener(this);
		actions[1] = action;
	}

	private void createChangeElement(Group radioGroup, Composite actionParent) {
		Button radio = createButton(radioGroup, SWT.RADIO, "Change element", 2,
				false);
		radios.add(radio);
		addSelectionListener(radio);
		Text namespace = createInputBox(actionParent, "namespace");
		Text name = createInputBox(actionParent, "element name");
		ChangeElementAction action = new ChangeElementAction(this.getShell());
		action.setControl(namespace, name);
		action.addActionChangedListener(this);
		actions[2] = action;
	}

	private void createRemoveAttribute(Group radioGroup, Composite actionParent) {
		Button radio = createButton(radioGroup, SWT.RADIO, "Remove Attribute:",
				3, false);
		radios.add(radio);
		addSelectionListener(radio);
		Text name = createInputBox(actionParent, "attribute name");
		Label label = new Label(actionParent, SWT.LEFT);
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false,
				false, 2, 1));
		RemoveAttributeAction action = new RemoveAttributeAction(
				this.getShell());
		action.setControl(name);
		action.addActionChangedListener(this);
		actions[3] = action;
	}

	private void createReplaceAttributeName(Group radioGroup,
			Composite actionParent) {
		Button radio = createButton(radioGroup, SWT.RADIO,
				"Replace Attribute Name", 4, false);
		radios.add(radio);
		addSelectionListener(radio);
		Text replace = createInputBox(actionParent, "replace");
		Text with = createInputBox(actionParent, "with");
		ReplaceAttributeAction action = new ReplaceAttributeAction(this.getShell(),
				ReplaceAttributeAction.NAME);
		action.setControl(replace, with);
		action.addActionChangedListener(this);
		actions[4] = action;
	}

	private void createReplaceAttributeValue(Group radioGroup,
			Composite actionParent) {
		Button radio = createButton(radioGroup, SWT.RADIO,
				"Replace Attribute Value", 5, false);
		radios.add(radio);
		addSelectionListener(radio);
		Text replace = createInputBox(actionParent, "replace");
		Text with = createInputBox(actionParent, "with");
		ReplaceAttributeAction action = new ReplaceAttributeAction(this.getShell(),
				ReplaceAttributeAction.VALUE);
		action.setControl(replace, with);
		action.addActionChangedListener(this);
		actions[5] = action;
	}

	private Text createInputBox(Composite actionParent, String text) {
		Label label = new Label(actionParent, SWT.LEFT);
		label.setText(text);
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false,
				false, 1, 1));
		Text box = new Text(actionParent, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, false,
				1, 1);
		box.setLayoutData(data);
		return box;
	}

	private Button createButton(Composite parent, int style, String text,
			int data, boolean isSelected) {
		Button button = new Button(parent, style);
		button.setText(text);
		button.setData(new Integer(data));
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		button.setSelection(isSelected);
		return button;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		for (ISearchResultPageAction action : actions) {
			action.selectionChanged(event);
		}
		ISearchResultPageAction action = getAction();
		actionChanged(action);
	}

	public void actionChanged(ISearchResultPageAction action) {
		if (!getAction().equals(action)) {
			return;
		}
		boolean isAll = action == null ? false : action.isRefactorAllEnabled();
		boolean isSelection = action == null ? false : action
				.isRefactorSelectionEnabled();
		updateButton(isAll, isSelection);
	}

	private ISearchResultPageAction getAction() {
		int index = -1;
		for (Button b : radios) {
			if (b.getSelection()) {
				index = (Integer) b.getData();
				break;
			}
		}
		if (index >= 0 && index < actions.length) {
			ISearchResultPageAction action = this.actions[index];
			return action;
		}
		return null;
	}

	private void updateButton(boolean isAll, boolean isSelection) {
		this.refactorSelection.setEnabled(isSelection);
		this.refactorAll.setEnabled(isAll);
	}

}
