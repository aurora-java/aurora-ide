package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.prototype.consultant.product.fsd.wizard.FSDContentControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.prototype.consultant.view.FunctionSelectionDialog;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TableLabelProvider;
import aurora.ide.swt.util.UWizardPage;

public class ProjectFSDContentPage extends UWizardPage {

	public static String[] properties = new String[] {
			FSDContentControl.FSD_DOCX_PATH, FSDContentControl.FSD_TABLE_INPUT,
			FSDContentControl.ONLY_SAVE_LOGIC };
	private Node projectNode;
	private Node selectionNode;

	protected ProjectFSDContentPage(String pageName, String title,
			ImageDescriptor titleImage, Node selectionNode,Node projectNode, CompositeMap input) {
		super(pageName);
		this.setTitle(title);
		this.setImageDescriptor(titleImage);
		this.setMessage(Messages.ContentDescPage_2);
		this.projectNode = projectNode;
		this.selectionNode = selectionNode;
		new FSDContentControl(this.getModel(),selectionNode.getPath()).loadFromMap(input);
	}

	@Override
	protected String[] getModelPropertyKeys() {
		return properties;
	}

	@Override
	protected String verifyModelProperty(String key, Object val) {
		if (properties[0].equals(key)) {
			if (val == null || "".equals(val)) { //$NON-NLS-1$
				return Messages.ProjectFSDContentPage_1;
			}
		}
		return null;
	}

	@Override
	protected Composite createPageControl(Composite control) {
		Composite c2 = new Composite(control, SWT.NONE);
		c2.setLayoutData(new GridData(GridData.FILL_BOTH));
		c2.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		new FSDContentControl(this.getModel(),selectionNode.getPath()) {
			protected void createTableColumn(Table table) {
				TableColumn column1 = new TableColumn(table, SWT.LEFT);
				column1.setWidth(128);
				column1.setText(Messages.ProjectFSDContentPage_2);
				TableColumn column2 = new TableColumn(table, SWT.NONE);
				column2.setWidth(193);
				column2.setText(Messages.ProjectFSDContentPage_3);
			}

			protected TableLabelProvider getLabelProvider() {
				return new TableLabelProvider() {
					public String getColumnText(Object element, int i) {

						if (element instanceof String) {
							Path p = new Path(element.toString());
							File file = p.toFile();
							if (file.exists() == false)
								return ""; //$NON-NLS-1$
							if (i == 0) {
								return file.getParentFile().getName();
							}
							if (i == 1) {
								CompositeMap loadFile = CompositeMapUtil
										.loadFile(file);
								CompositeMap child = loadFile
										.getChild(FunctionDesc.fun_name);
								String text = child == null ? "" : child //$NON-NLS-1$
										.getText();
								return text == null ? "" : text; //$NON-NLS-1$
							}
						}
						return ""; //$NON-NLS-1$
					}
				};
			}

			protected void clickAddButton(Shell shell, final TableViewer tv) {
				FunctionSelectionDialog fsd = new FunctionSelectionDialog();
				String path = fsd.openFolderSelectionDialog(Messages.ProjectFSDContentPage_7,
						shell, getProjectNode());
				if (path != null && path.length() > 0) {
					getTableInput().add(path);
				}
				tv.setInput(getTableInput());
			}
		}.createFSDContentControl(c2);
		return c2;
	}

	protected Node getProjectNode() {
		return projectNode;
	}
	protected void saveTOMap(CompositeMap map) {
		new FSDContentControl(this.getModel(),selectionNode.getPath()).saveToMap(map);
	}
}
