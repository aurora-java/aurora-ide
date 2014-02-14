package aurora.ide.prototype.consultant.view.property.page;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.prototype.consultant.product.fsd.wizard.FSDContentControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.prototype.consultant.product.fsd.wizard.TitleControl;
import aurora.ide.prototype.consultant.view.FunctionSelectionDialog;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.prototype.consultant.view.wizard.CreateProjectWizard;
import aurora.ide.swt.util.TableLabelProvider;

public class ProjectFSDPropertyPage extends AbstractFSDPropertyPage {


	protected CompositeMap loadProperties(File file) {
		CompositeMap pp = ResourceUtil.loadProjectProperties(file);
		return pp;
	}

	protected void saveProperties(CompositeMap map) throws IOException {
		IAdaptable element = this.getElement();
		if (element instanceof Node) {
			File file = ((Node) element).getFile();
			ResourceUtil.createFile(file, CreateProjectWizard.QUICK_UI_PROJECT,
					map);
		}
	}

	protected void createTitleControl(Composite parent) {
		new TitleControl(this.getModel()) {
			public void createTitleControl(Composite parent) {
				Composite functonComposite = new Composite(parent, SWT.NONE);
				functonComposite.setLayout(new GridLayout(2, false));
				functonComposite.setLayoutData(new GridData(
						GridData.FILL_HORIZONTAL));
				createInputField(functonComposite, Messages.FunctionDescPage_0,
						FunctionDesc.doc_title);
			}
		}.createTitleControl(parent);
	}

	protected void createContentControl(Composite c2) {
		new FSDContentControl(this.getModel(),getBasePath()) {
			protected void createTableColumn(Table table) {
				TableColumn column1 = new TableColumn(table, SWT.LEFT);
				column1.setWidth(128);
				column1.setText(Messages.ProjectFSDPropertyPage_0);
				TableColumn column2 = new TableColumn(table, SWT.NONE);
				column2.setWidth(193);
				column2.setText(Messages.ProjectFSDPropertyPage_1);
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
				String path = fsd.openFolderSelectionDialog(Messages.ProjectFSDPropertyPage_5,
						shell, getElement());
				if (path != null && path.length() > 0) {
					getTableInput().add(path);
				}
				tv.setInput(getTableInput());
			}
		}.createFSDContentControl(c2);
	}
	
	protected IPath getBasePath(){
		Node element = (Node) getElement();
		return element.getPath();
	}
}
