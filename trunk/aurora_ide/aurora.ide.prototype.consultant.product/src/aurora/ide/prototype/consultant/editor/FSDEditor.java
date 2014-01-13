package aurora.ide.prototype.consultant.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.meta.gef.editors.consultant.property.FSDPropertyFactory;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.GridSelectionCol;
import aurora.plugin.source.gen.screen.model.Navbar;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;
import aurora.plugin.source.gen.screen.model.io.Object2CompositeMap;

public class FSDEditor extends EditorPart implements ISelectionChangedListener {

	public static final String EDITOR_ID = "aurora.ide.prototype.consultant.editor.FSDEditor";
	private IEditorInput input;
	private ScreenBody diagram;
	private FSDPropertyFactory editablePropertyFactory = new FSDPropertyFactory();
	private Composite propertyComposite;
	private boolean isDirty;

	@Override
	public void doSave(IProgressMonitor monitor) {

		IEditorInput editorInput = this.getEditorInput();
		if (editorInput instanceof PathEditorInput) {
			PathEditorInput pei = (PathEditorInput) editorInput;
			IPath path = pei.getPath();
			File file = path.toFile();
			try {
				file.createNewFile();
				if (file.exists()) {
					if (file.canWrite()) {
						Object2CompositeMap o2c = new Object2CompositeMap();
						CompositeMap map = o2c.createCompositeMap(diagram);
						XMLOutputter.saveToFile(file, map);
						updateEditorStatus(false);
					}
				}
			} catch (IOException e) {
				MessageDialog.openInformation(this.getSite().getShell(),
						"Info", "保存文件失败");
			}
		}
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);
		this.setInput(input);

		this.input = input;
		if (input instanceof PathEditorInput) {
			File file = ((PathEditorInput) input).getPath().toFile();
			CompositeMap loadFile = CompositeMapUtil.loadFile(file);
			if (loadFile != null) {
				CompositeMap2Object c2o = new CompositeMap2Object();
				diagram = c2o.createScreenBody(loadFile);
			} else {
				diagram = new ScreenBody();
			}
		}
		String lastSegment = ((PathEditorInput) input).getPath()
				.removeFileExtension().lastSegment();
		this.setPartName(lastSegment);

	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
//		Composite root = new Composite(parent, SWT.NONE);
//		root.setLayout(new GridLayout());

//		Composite filterComposite = new Composite(root, SWT.NONE);
//		filterComposite.setLayout(new GridLayout());
//		filterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//		createFilterComposite(filterComposite);

		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTreeViewer(sashForm);

		createPropertyView(sashForm);

		sashForm.setWeights(new int[] { 1, 4 });
	}

	private void createFilterComposite(Composite filterComposite) {

	}

	private void createPropertyView(Composite sashForm) {
		propertyComposite = new Composite(sashForm, SWT.NONE);
		propertyComposite.setLayout(new GridLayout());
		
		IPropertyDescriptor[] pds = editablePropertyFactory
				.createPropertyDescriptors((AuroraComponent) diagram);
		createPropertyControl((AuroraComponent) diagram, pds);
		
	}

	private void createTreeViewer(Composite sashForm) {
		Composite treeViewerComposite = new Composite(sashForm, SWT.NONE);
		treeViewerComposite.setLayout(new GridLayout());

		TreeViewer viewer = new TreeViewer(treeViewerComposite, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ScreenBodyContentProvider());
		viewer.setLabelProvider(new ScreenBodyLabelProvider());
		viewer.addFilter(new ComponentFilter(new String[] {
				GridSelectionCol.GRIDSELECTIONCOL, Navbar.NAVBAR }));
		viewer.addFilter(new TabBodyFilter());
		viewer.addSelectionChangedListener(this);
		viewer.setInput(this.diagram);
	}

	@Override
	public void setFocus() {

	}

	public IPropertyDescriptor[] getFSDPropertyDescriptors(
			AuroraComponent object) {
		IPropertyDescriptor[] pd = editablePropertyFactory
				.createPropertyDescriptors(object);
		return pd;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		Object firstElement = selection.getFirstElement();

		firstElement = firstElement == null ? this.diagram : firstElement;
		IPropertyDescriptor[] pds = editablePropertyFactory
				.createPropertyDescriptors((AuroraComponent) firstElement);
		createPropertyControl((AuroraComponent) firstElement, pds);

	}

	private void createPropertyControl(final AuroraComponent ac,
			IPropertyDescriptor[] pds) {
		Control[] children = propertyComposite.getChildren();
		for (Control control : children) {
			org.eclipse.swt.widgets.Listener[] listeners = control
					.getListeners(SWT.Modify);
			for (int i = 0; i < listeners.length; i++) {
				control.removeListener(SWT.Modify, listeners[i]);
			}
			control.dispose();
		}
		for (IPropertyDescriptor pd : pds) {
			CellEditor ce = pd.createPropertyEditor(propertyComposite);
			ce.create(propertyComposite);
			Control control = ce.getControl();
			if (control instanceof Text) {
				((Text) control).setText(ac.getStringPropertyValue(""
						+ pd.getId()));
			}
			TypedListener typedListener = new TypedListener(
					new Listener(ac, pd));
			control.addListener(SWT.Modify, typedListener);
		}
		propertyComposite.layout();
	}

	private class Listener implements ModifyListener {

		private IPropertyDescriptor pd;
		private AuroraComponent ac;

		private Listener(AuroraComponent ac, IPropertyDescriptor pd) {
			this.pd = pd;
			this.ac = ac;
		}

		public void modifyText(ModifyEvent e) {
			Object source = e.getSource();
			if (source instanceof Text) {
				updateFSDProperty(pd, ((Text) source).getText(), ac);
			}
		}
	}

	public void updateFSDProperty(IPropertyDescriptor pd, String text,
			AuroraComponent ac) {
		ac.setPropertyValue("" + pd.getId(), text);
		updateEditorStatus(true);
	}

	protected void updateEditorStatus(boolean isDirty) {
		this.isDirty = isDirty;
		firePropertyChange(PROP_DIRTY);
	}

}
