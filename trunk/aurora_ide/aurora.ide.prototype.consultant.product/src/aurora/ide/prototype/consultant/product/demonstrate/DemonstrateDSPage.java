package aurora.ide.prototype.consultant.product.demonstrate;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

import aurora.ide.prototype.consultant.product.Activator;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.WidgetFactory;
import aurora.plugin.source.gen.screen.model.DemonstrateDS;
import aurora.plugin.source.gen.screen.model.DemonstrateData;

public class DemonstrateDSPage extends WizardPage {
	private DemonstrateData data;
	private TreeViewer dsViewer;
	private TextField dsNameField;
	private Text dsData;

	protected DemonstrateDSPage(String pageName, String title,
			ImageDescriptor titleImage, DemonstrateData data) {
		super(pageName, title, titleImage);
		this.setData(data);
		this.setMessage(Messages.DemonstrateDSPage_3 + Messages.DemonstrateDSPage_0);
	}

	private TextField createInputField(Composite parent, String label) {
		TextField createTextField = WidgetFactory
				.createTextField(parent, label);
		return createTextField;
	}

	@Override
	public void createControl(Composite root) {

		SashForm sashForm = new SashForm(root, SWT.HORIZONTAL);
		Group c1 = new Group(sashForm, SWT.NONE);
		c1.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		c1.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		c1.setText(Messages.DemonstrateDSPage_4);

		dsViewer = new TreeViewer(c1, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.SINGLE);
		dsViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite c = WidgetFactory.composite(sashForm);
		c.setLayout(GridLayoutUtil.COLUMN_LAYOUT_2);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		dsNameField = createInputField(c, Messages.DemonstrateDSPage_1);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		WidgetFactory.hSeparator(c).setLayoutData(layoutData);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		Label l = new Label(c, SWT.NONE);
		l.setText(Messages.DemonstrateDSPage_5);
		l.setLayoutData(layoutData);
		GridData gd = new GridData(GridData.FILL_BOTH);
		dsData = new Text(c, SWT.BORDER | SWT.MULTI);
		gd.horizontalSpan = 2;
		dsData.setLayoutData(gd);
		sashForm.setWeights(new int[] { 1, 4 });
		this.setControl(sashForm);

		makeMenu();
		makeListener();
		init();
	}

	private void makeMenu() {

		MenuManager menuMgr = new MenuManager("DemonstrateDSTreeViewer"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection) dsViewer
						.getSelection();
				final Object firstElement = selection.getFirstElement();
				if (firstElement != null)
					manager.add(new Action(Messages.DemonstrateDSPage_2) {
						public void run() {
							Activator
									.getDefault()
									.getDemonstrateDSManager()
									.removeDemonstrateDS(
											(DemonstrateDS) firstElement);
							dsViewer.setInput(getDemonstrateDSs());
						}
					});
			}
		});
		Menu menu = menuMgr.createContextMenu(dsViewer.getTree());
		dsViewer.getTree().setMenu(menu);

	}

	private void init() {
		dsViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void dispose() {

			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {

			}

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Object[]) {
					return (Object[]) inputElement;
				}
				return null;
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}

		});
		dsViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if (element instanceof DemonstrateDS) {
					return ((DemonstrateDS) element).getName();
				}
				return null;
			}
		});
		dsViewer.setInput(getDemonstrateDSs());
		String demonstrateDSName = data.getDemonstrateDSName();
		if (demonstrateDSName == null || "".equals(demonstrateDSName)) { //$NON-NLS-1$
			String demonstrateData = data.getDemonstrateData();
			if (demonstrateData == null || "".equals(demonstrateDSName)) { //$NON-NLS-1$
				dsData.setText(demonstrateData);
			} else {
				dsData.setText(demonstrateData);
			}
		} else {
			DemonstrateDS demonstrateDS = Activator.getDefault()
					.getDemonstrateDSManager()
					.getDemonstrateDS(demonstrateDSName);
			if (demonstrateDS != null)
				dsViewer.setSelection(new StructuredSelection(demonstrateDS));
			else {
				dsNameField.setText(data.getDemonstrateDSName());
				dsData.setText(data.getDemonstrateData());
			}
		}
	}

	private void makeListener() {
		dsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection)
							.getFirstElement();
					if (firstElement instanceof DemonstrateDS) {
						dsNameField.setText(((DemonstrateDS) firstElement)
								.getName());
						dsData.setText(((DemonstrateDS) firstElement).getData());
					}
				}
			}
		});
		dsNameField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				data.setDemonstrateDSName(dsNameField.getText().getText());
			}
		});
		dsData.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				data.setDemonstrateData(dsData.getText());
			}
		});
	}

	protected boolean verifyPage(String key, String message) {
		// Object propertyValue = getModel().getPropertyValue(key);
		//		if (propertyValue == null || "".equals(propertyValue)) { //$NON-NLS-1$
		// this.setErrorMessage(message);
		// this.setPageComplete(false);
		// return false;
		// }
		this.setErrorMessage(null);
		this.setPageComplete(true);
		return true;
	}

	public DemonstrateData getData() {
		return data;
	}

	public void setData(DemonstrateData data) {
		this.data = data;
	}

	private DemonstrateDS[] getDemonstrateDSs() {
		return Activator.getDefault().getDemonstrateDSManager()
				.getDemonstrateDS();
	}

}
