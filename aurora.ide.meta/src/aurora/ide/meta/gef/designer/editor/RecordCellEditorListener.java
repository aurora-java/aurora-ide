package aurora.ide.meta.gef.designer.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;

import aurora.ide.meta.gef.designer.model.Record;

public class RecordCellEditorListener implements ICellEditorListener {

	private String prompt;
	private CellEditor editor;
	private Record record;

	public RecordCellEditorListener(Record r, String prompt, CellEditor editor) {
		this.record = r;
		this.prompt = prompt;
		this.editor = editor;
	}

	@Override
	public void applyEditorValue() {
		record.put(prompt, editor.getValue());
	}

	@Override
	public void cancelEditor() {

	}

	@Override
	public void editorValueChanged(boolean oldValidState, boolean newValidState) {

	}
}
