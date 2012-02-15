package aurora.ide.meta.gef.editors.property;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.QueryContainer;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class QueryContainerEditDialog extends EditWizard {
	private QueryContainer queryContainer = null;
	private Container tmpTarget = null;

	public QueryContainerEditDialog() {
		super();
		setWindowTitle("QueryDataSet");
	}

	public void addPages() {
		addPage(new InnerPage("QueryContainerSelection"));
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
			setTitle("选择一个组件作为查询条件来源");
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
				throw new RuntimeException("Null root");
			final Tree tree = new Tree(parent, SWT.BORDER);
			TreeItem rootItem = new TreeItem(tree, SWT.NONE);
			rootItem.setText("screenBody");
			rootItem.setForeground(new Color(null, 200, 200, 200));
			createSubTree(tree, rootItem, root);

			for (TreeItem ti : tree.getItems())
				ti.setExpanded(true);
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

		private void createSubTree(Tree tree, TreeItem ti, Container container) {
			for (AuroraComponent ac : container.getChildren()) {
				if (ac instanceof BOX) {
					TreeItem t = new TreeItem(ti, SWT.NONE);
					t.setData(ac);
					if (ac == queryContainer.getTarget())
						tree.setSelection(t);
					t.setImage(PropertySourceUtil.getImageOf(ac));
					t.setText(getTextOf(ac));
					createSubTree(tree, t, (Container) ac);
				} else if (ac instanceof TabItem) {
					TreeItem t = new TreeItem(ti, SWT.NONE);
					t.setImage(PropertySourceUtil.getImageOf(ac));
					t.setText(getTextOf(ac));
					t.setForeground(new Color(null, 200, 200, 200));
					createSubTree(tree, t, ((TabItem) ac).getBody());
				}
			}
			for (TreeItem t : ti.getItems())
				t.setExpanded(true);
		}

		private String getTextOf(AuroraComponent ac) {
			String prop = ac.getPrompt();
			String aType = ac.getType();
			return aType + " [" + prop + "]";
			// return ac.getClass().getSimpleName();
		}

	}

}
