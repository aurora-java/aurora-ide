package aurora.ide.meta.gef.designer.editor;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import aurora.ide.meta.gef.designer.DesignerMessages;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;

public class RelationViewer extends TableViewer implements IDesignerConst {

	public RelationViewer(Composite parent) {
		super(parent);
		config();
	}

	public RelationViewer(Composite parent, int style) {
		super(parent, style);
		config();
	}

	private void config() {
		Table table = getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn zColumn = new TableColumn(table, SWT.NONE);
		zColumn.setWidth(0);
		zColumn.setResizable(false);
		zColumn.setMoveable(false);
		TableColumn column = new TableColumn(table, SWT.CENTER);
		column.setText(DesignerMessages.RelationViewer_0);
		column.pack();
		column.setResizable(false);
		column.setMoveable(false);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.RelationViewer_1);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.RelationViewer_2);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.RelationViewer_3);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.RelationViewer_4);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.RelationViewer_5);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText("Ref-Field");
		column.setWidth(100);
		setContentProvider(new BMModelContentProvider(BMModel.RELATION));
		setLabelProvider(new BMModelLabelProvider(BMModel.RELATION,
				COLUMN_PROPERTIES));
		setColumnProperties(COLUMN_PROPERTIES);
		setCellModifier(new BMModelCellModifier(this, BMModel.RELATION));
	}
}
