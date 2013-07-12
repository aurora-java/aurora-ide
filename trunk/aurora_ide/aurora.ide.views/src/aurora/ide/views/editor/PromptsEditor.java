package aurora.ide.views.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import aurora.ide.view.ViewNode;

public class PromptsEditor {

	private TableItem item;
	private Table table;
	private ViewNode viewNode;
	private Text text;
	private TableEditor editor;

	public PromptsEditor(Table table, TableItem item) {
		this.table = table;
		this.item = item;
		viewNode = (ViewNode) item.getData();
	}

	public void createEditor(int column) {
		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		text = new Text(table, SWT.NONE);
		editor.setEditor(text, item, column);
		autoDispose();
	}

	public void addModifyListener(ModifyListener listener) {
		text.addModifyListener(listener);
	}

	public void setText(String t) {
		text.setText(t);
	}

	private void autoDispose() {
		item.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				text.dispose();
				editor.dispose();
			}

		});

	}

	public String getText() {
		return text.getText();
	}

}
