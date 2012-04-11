package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.QueryContainer;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class QueryContainerEditDialog extends EditWizard {
	private String section_type_filter = Container.SECTION_TYPE_QUERY;
	private QueryContainer queryContainer = null;
	private Container tmpTarget = null;

	public QueryContainerEditDialog() {
		super();
		setWindowTitle("QueryDataSet"); //$NON-NLS-1$
	}

	public void addPages() {
		addPage(new InnerPage("QueryContainerSelection")); //$NON-NLS-1$
	}

	@Override
	public void setDialogEdiableObject(DialogEditableObject obj) {
		queryContainer = (QueryContainer) obj;
	}

	@Override
	public boolean performFinish() {
		if (tmpTarget != null)
			queryContainer.setTarget(tmpTarget);
		return true;
	}

	private class InnerPage extends WizardPage {

		protected InnerPage(String pageName) {
			super(pageName);
			setTitle(Messages.QueryContainerEditDialog_2);
		}

		public void createControl(Composite parent) {
			AuroraComponent comp = (AuroraComponent) queryContainer
					.getContextInfo();
			ViewDiagram root = null;
			while (comp != null) {
				if (comp instanceof ViewDiagram) {
					root = (ViewDiagram) comp;
					break;
				}
				comp = comp.getParent();
			}
			if (root == null)
				throw new RuntimeException("Null root"); //$NON-NLS-1$
			ModelTreeSelector mts = new ModelTreeSelector(parent, SWT.BORDER);
			TreeViewer tv = mts.getTreeViewer();
			tv.setFilters(new ViewerFilter[] {
					ModelTreeSelector.CONTAINER_FILTER,
					ModelTreeSelector.getSectionFilter(section_type_filter) });
			final Tree tree = tv.getTree();
			mts.setRoot(root);
			mts.refreshTree();
			tree.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					TreeItem ti = tree.getSelection()[0];
					tmpTarget = (Container) ti.getData();
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});

			//
			setControl(tree);
		}
	}
}
