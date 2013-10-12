package aurora.ide.prototype.consultant.product.demonstrate;

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
import org.eclipse.swt.widgets.Text;

import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.WidgetFactory;

public class DemonstrateDSPage extends WizardPage  {
	private DemonstrateData data;
	private TreeViewer dsViewer;
	private TextField dsNameField;
	private Text dsData;

	protected DemonstrateDSPage(String pageName, String title,
			ImageDescriptor titleImage, DemonstrateData data) {
		super(pageName, title, titleImage);
		this.setData(data);
		this.setMessage("演示数据配置");
	}

	private TextField createInputField(Composite parent, String label) {
		TextField createTextField = WidgetFactory
				.createTextField(parent, label);
		return createTextField;
	}

	@Override
	public void createControl(Composite root) {

		SashForm sashForm = new SashForm(root, SWT.HORIZONTAL);

		dsViewer = new TreeViewer(sashForm, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		dsViewer.getTree().setLayoutData(new GridData(GridData.FILL_VERTICAL));

		Composite c = WidgetFactory.composite(sashForm);
		c.setLayout(GridLayoutUtil.COLUMN_LAYOUT_2);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		dsNameField = createInputField(c, "数据源");

		GridData gd = new GridData(GridData.FILL_BOTH);
		dsData = new Text(c, SWT.BORDER | SWT.MULTI);
		gd.horizontalSpan = 2;
		dsData.setLayoutData(gd);
		sashForm.setWeights(new int[] { 1, 4 });
		this.setControl(sashForm);

		makeListener();
		init();
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
		if (demonstrateDSName == null || "".equals(demonstrateDSName)) {
			String demonstrateData = data.getDemonstrateData();
			if (demonstrateData != null)
				dsData.setText(demonstrateData);
		} else {
			
			dsViewer.setSelection(new StructuredSelection(ss));
		}

	}

	private void makeListener() {
		dsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();	
				if(selection instanceof IStructuredSelection){
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if(firstElement instanceof DemonstrateDS){
						dsNameField.setText(((DemonstrateDS) firstElement).getName());
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

	private DemonstrateDS ss = new DemonstrateDS("aac", "a,ab,abc");
	private DemonstrateDS[] ds = new DemonstrateDS[] {
			new DemonstrateDS("ab", "a,ab,abc"),
			new DemonstrateDS("abc", "a,ab,abc"),
			new DemonstrateDS("aaa", "a,ab,abc"),
			new DemonstrateDS("abb", "a,ab,abc"),
			new DemonstrateDS("acc", "a,ab,abc"), ss };

	private DemonstrateDS[] getDemonstrateDSs() {
		return ds;
	}

}
