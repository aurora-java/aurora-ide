package aurora.ide.editor.widgets;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.helpers.LocaleMessage;


/**
 * This class demonstrates Lists
 */
public class ListElementsExchangeDialog {
	// Strings to use as list items
	private String dialogTitle;
	private String leftGroupTitle;
	private String rightGroupTitle;
	private String[] leftItems;
	private String[] rightItems;
	private List leftList;
	private List rightList;
	Shell shell;
	public final static int OK = 0;
	public final static int CANCEL=1;
	private int result;
	public ListElementsExchangeDialog(Shell shell, String dialogTitle,
			String leftGroupTitle, String rightGroupTitle, String[] leftItems,
			String[] rightItems) {
		this.shell = shell;
		this.dialogTitle = dialogTitle;
		this.leftGroupTitle = leftGroupTitle;
		this.rightGroupTitle = rightGroupTitle;
		this.leftItems = leftItems;
		this.rightItems = rightItems;
	}

	public ListElementsExchangeDialog(String dialogTitle,
			String leftGroupTitle, String rightGroupTitle, String[] leftItems,
			String[] rightItems) {
		this.dialogTitle = dialogTitle;
		this.leftGroupTitle = leftGroupTitle;
		this.rightGroupTitle = rightGroupTitle;
		this.leftItems = leftItems;
		this.rightItems = rightItems;
	}

	public int open() {
		result = CANCEL;
		if (shell == null){
			shell = new Shell(SWT.MIN | SWT.MAX | SWT.DIALOG_TRIM
					| SWT.APPLICATION_MODAL);
//			shell.setSize(600, 500);
		}
		
		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		if (dialogTitle != null)
			shell.setText(dialogTitle);

		Group mainGroup = new Group(shell, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		mainGroup.setLayoutData(gridData);

		gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		mainGroup.setLayout(gridLayout);

		Group leftGroup = new Group(mainGroup, SWT.NONE);
		if (leftGroupTitle != null)
			leftGroup.setText(leftGroupTitle);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 3;
		leftGroup.setLayoutData(gridData);
		leftGroup.setLayout(new FillLayout());
		leftList = new List(leftGroup,SWT.MULTI | SWT.V_SCROLL);
		
		Group actionGroup = new Group(mainGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL, GridData.FILL, false, true);
		gridData.horizontalSpan = 2;
		actionGroup.setLayoutData(gridData);

		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = false;
		rowLayout.justify = false;
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginLeft = 10;
		rowLayout.marginTop = 80;
		rowLayout.marginRight = 10;
		rowLayout.marginBottom = 80;
		rowLayout.spacing = 5;
		actionGroup.setLayout(rowLayout);

		if (leftItems != null)
			leftList.setItems(leftItems);

		Button toRightAll = new Button(actionGroup, SWT.NONE);
		toRightAll.setText("=>");
		Button toRight = new Button(actionGroup, SWT.NONE);
		toRight.setText("->");
		Button toleft = new Button(actionGroup, SWT.NONE);
		toleft.setText("<-");
		Button toleftAll = new Button(actionGroup, SWT.NONE);
		toleftAll.setText("<=");

		Group rightGroup = new Group(mainGroup, SWT.NONE);
		if (rightGroupTitle != null)
			rightGroup.setText(rightGroupTitle);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 3;
		rightGroup.setLayoutData(gridData);

		rightGroup.setLayout(new FillLayout());
		rightList = new List(rightGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

		if (rightItems != null)
			rightList.setItems(rightItems);

		Group buttonGroup = new Group(shell, SWT.NONE);
		gridData = new GridData(GridData.END, GridData.FILL, false, false);
		buttonGroup.setLayoutData(gridData);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		buttonGroup.setLayout(gridLayout);
		
		Button cancel = new Button(buttonGroup, SWT.PUSH);
		cancel.setText(LocaleMessage.getString("Cancel"));
		gridData = new GridData(GridData.END, GridData.CENTER, false, false);
		cancel.setLayoutData(gridData);
		Button enter = new Button(buttonGroup, SWT.PUSH);
		enter.setText(LocaleMessage.getString("OK"));
		gridData = new GridData(GridData.END, GridData.CENTER, false, false);
		enter.setLayoutData(gridData);

		toRightAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int oldLength = rightList.getItems().length;
				String[] newItems = joinItems(rightList.getItems(), leftList
						.getItems());
				rightList.setItems(newItems);
				rightList.setSelection(oldLength, newItems.length);
				leftList.removeAll();

			}
		});

		toRight.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int oldLength = rightList.getItems().length;
				String[] newItems = joinItems(rightList.getItems(), leftList
						.getSelection());
				rightList.setItems(newItems);
				rightList.setSelection(oldLength, newItems.length);
				leftList.remove(leftList.getSelectionIndices());
			}
		});

		toleft.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int oldLength = leftList.getItems().length;
				String[] newItems = joinItems(leftList.getItems(), rightList
						.getSelection());
				leftList.setItems(newItems);
				leftList.setSelection(oldLength, newItems.length);
				rightList.remove(rightList.getSelectionIndices());
			}
		});

		toleftAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int oldLength = leftList.getItems().length;
				String[] newItems = joinItems(leftList.getItems(), rightList
						.getItems());
				leftList.setItems(newItems);
				leftList.setSelection(oldLength, newItems.length);
				rightList.removeAll();
			}
		});
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
				result = CANCEL;
			}
		});
		enter.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
				result = OK;
			}
		});
		shell.open();
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				leftItems = leftList.getItems();
				rightItems = rightList.getItems();

			}
		});

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		String[] ITEMS = { "Alpha", "Bravo", "Charlie", "Delta", "Echo",
				"Foxtrot", "Golf" };
		new ListElementsExchangeDialog(shell, "Dialog", "left ", "right", ITEMS, ITEMS)
				.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public static String[] joinItems(String[] left, String[] right) {
		String[] result = new String[left.length + right.length];
		int resultIndex = 0;
		for (int i = 0; i < left.length; i++) {
			result[resultIndex++] = left[i];
		}
		for (int i = 0; i < right.length; i++) {
			result[resultIndex++] = right[i];
		}
		return result;
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	public String getLeftGroupTitle() {
		return leftGroupTitle;
	}

	public void setLeftGroupTitle(String leftGroupTitle) {
		this.leftGroupTitle = leftGroupTitle;
	}

	public String getRightGroupTitle() {
		return rightGroupTitle;
	}

	public void setRightGroupTitle(String rightGroupTitle) {
		this.rightGroupTitle = rightGroupTitle;
	}

	public String[] getLeftItems() {
		return leftItems;
	}

	public void setLeftItems(String[] leftItems) {
		this.leftItems = leftItems;
	}

	public String[] getRightItems() {
		return rightItems;
	}

	public void setRightItems(String[] rightItems) {
		this.rightItems = rightItems;
	}
}
