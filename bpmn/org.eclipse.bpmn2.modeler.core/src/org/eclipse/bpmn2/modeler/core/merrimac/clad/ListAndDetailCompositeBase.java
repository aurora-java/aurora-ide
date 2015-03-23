/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.core.merrimac.clad;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectPropertyProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.DefaultBusinessObjectDelegate;
import org.eclipse.bpmn2.modeler.core.merrimac.IBusinessObjectDelegate;
import org.eclipse.bpmn2.modeler.core.merrimac.IConstants;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.service.IValidator;
import org.eclipse.emf.validation.service.ModelValidationService;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class ListAndDetailCompositeBase extends Composite implements ResourceSetListener, Adapter {

	public final static Bpmn2Package PACKAGE = Bpmn2Package.eINSTANCE;
	@Deprecated
	// use createModelObject() instead
	public final static Bpmn2ModelerFactory FACTORY = Bpmn2ModelerFactory.getInstance();
	protected AbstractBpmn2PropertySection propertySection;
	protected FormToolkit toolkit;
	protected IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	protected EObject businessObject;
	protected int style;
	protected DiagramEditor diagramEditor;
	protected TransactionalEditingDomainImpl editingDomain;
	protected ModelHandler modelHandler;
	protected boolean isPopupDialog;
	private IBusinessObjectDelegate boDelegate;

	public ListAndDetailCompositeBase(AbstractBpmn2PropertySection section) {
		this(section, SWT.NONE);
	}
	
	public ListAndDetailCompositeBase(AbstractBpmn2PropertySection section, int style) {
		super(section.getParent(), style);
		propertySection = section;
		toolkit = propertySection.getWidgetFactory();
		initialize();
	}
	
	public ListAndDetailCompositeBase(Composite parent, int style) {
		super(parent, style);
		toolkit = new FormToolkit(Display.getCurrent());
		this.style = style;
		initialize();
	}

	protected void initialize() {
		setLayout(new GridLayout(3, false));
		if (getParent().getLayout() instanceof GridLayout) {
			GridLayout layout = (GridLayout) getParent().getLayout();
			setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, layout.numColumns, 1));
		}
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
	}
	
	@Override
	public void dispose() {
		removeDomainListener();
		removeChangeListener(businessObject);

		ModelUtil.disposeChildWidgets(this);
		super.dispose();
	}
	
	protected void addDomainListener() {
		if (editingDomain==null) {
			editingDomain = (TransactionalEditingDomainImpl)getDiagramEditor().getEditingDomain();
			editingDomain.addResourceSetListener(this);
		}
	}

	protected void removeDomainListener() {
		if (editingDomain!=null) {
			editingDomain.removeResourceSetListener(this);
		}
	}

	public void setPropertySection(AbstractBpmn2PropertySection section) {
		propertySection = section;
	}
	
	public AbstractBpmn2PropertySection getPropertySection() {
		if (propertySection!=null)
			return propertySection;
		Composite parent = getParent();
		while (parent!=null && !(parent instanceof ListAndDetailCompositeBase))
			parent = parent.getParent();
		if (parent instanceof ListAndDetailCompositeBase)
			return ((ListAndDetailCompositeBase)parent).getPropertySection();
		return null;
	}

	public IBusinessObjectDelegate getBusinessObjectDelegate() {
		if (boDelegate==null)
			boDelegate = new DefaultBusinessObjectDelegate(editingDomain);
		return boDelegate;
	}
	
	public void redrawPage() {
		if (getPropertySection()!=null) {
			getPropertySection().layout();
			getParent().layout();
			layout();
		}
		else
		{
			ModelUtil.recursivelayout(getParent());
		}
	}
	
	public void setVisible(boolean visible) {
		if (getLayoutData() instanceof GridData) {
			((GridData)getLayoutData()).exclude = !visible;
		}
		super.setVisible(visible);
		redrawPage();
	}

	public TabbedPropertySheetPage getTabbedPropertySheetPage() {
		if (getPropertySection()!=null)
			return getPropertySection().getTabbedPropertySheetPage();
		return null;
	}

	public FormToolkit getToolkit() {
		return toolkit;
	}

	public void setDiagramEditor(DiagramEditor bpmn2Editor) {
		this.diagramEditor = bpmn2Editor;
	}
	
	public DiagramEditor getDiagramEditor() {
		if (diagramEditor!=null)
			return diagramEditor;
		if (getPropertySection()!=null)
			return (DiagramEditor)getPropertySection().getDiagramEditor();
		Composite parent = getParent();
		while (parent!=null && !(parent instanceof ListAndDetailCompositeBase))
			parent = parent.getParent();
		if (parent instanceof ListAndDetailCompositeBase)
			return diagramEditor = ((ListAndDetailCompositeBase)parent).getDiagramEditor();
		
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (part instanceof DiagramEditor)
			diagramEditor = (DiagramEditor)part;
		else if (part instanceof IEditorPart) {
			diagramEditor = (DiagramEditor) ((IEditorPart)part).getAdapter(DiagramEditor.class);
		}
		else if (part instanceof PropertySheet) {
			TabbedPropertySheetPage page = (TabbedPropertySheetPage) ((PropertySheet)part).getCurrentPage();
			if (page instanceof Bpmn2TabbedPropertySheetPage) {
				diagramEditor = ((Bpmn2TabbedPropertySheetPage)page).getDiagramEditor();
			}
		}
		return diagramEditor;
	}

	protected boolean isModelObjectEnabled(String className, String featureName) {
		return getModelEnablements().isEnabled(className, featureName);
	}

	protected boolean isModelObjectEnabled(EClass eclass, EStructuralFeature feature) {
		String className = eclass==null ? null : eclass.getName();
		String featureName = feature==null ? null : feature.getName();
		return isModelObjectEnabled(className, featureName);
	}

	protected boolean isModelObjectEnabled(EClass eclass) {
		if (eclass!=null)
			return isModelObjectEnabled(eclass,null);
		return false;
	}

	protected ModelEnablements getModelEnablements() {
		return (ModelEnablements)getDiagramEditor().getAdapter(ModelEnablements.class);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T extends EObject> T createModelObject(Class clazz) {
		T object = null;
		object = getBusinessObjectDelegate().createObject(clazz);
		ModelUtil.setID(object, ObjectPropertyProvider.getResource(businessObject));
		return object;
	}
	
	public TargetRuntime getTargetRuntime() {
		return (TargetRuntime) getDiagramEditor().getAdapter(TargetRuntime.class);
	}
	
	public Bpmn2Preferences getPreferences() {
		return (Bpmn2Preferences) getDiagramEditor().getAdapter(Bpmn2Preferences.class);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#setData(java.lang.Object)
	 */
	@Override
	public void setData(Object object) {
		if (object instanceof EObject)
			setBusinessObject((EObject)object);
	}
	
	public void setBusinessObject(EObject object) {
		getDiagramEditor();
		if (diagramEditor==null)
			diagramEditor = ModelUtil.getEditor(object);
		addDomainListener();
		modelHandler = ModelHandler.getInstance(getDiagramEditor().getDiagramTypeProvider().getDiagram());
		removeChangeListener(businessObject);
		businessObject = object;
		addChangeListener(businessObject);

		// Do initial validation to force display of error message if any
		validate();
	}
	
	protected void addChangeListener(EObject object) {
		if (object!=null && !object.eAdapters().contains(this))
			object.eAdapters().add(this);
	}
	
	protected void removeChangeListener(EObject object) {
		if (object!=null && object.eAdapters().contains(this)) {
			object.eSetDeliver(false);
			object.eAdapters().remove(this);
			object.eSetDeliver(true);
		}
	}

	public final EObject getBusinessObject() {
		return businessObject;
	}

	@Override
	public NotificationFilter getFilter() {
		NotificationFilter filter = null;
		// the editor needs to return a "do nothing" filter while a save is in progress
		if (diagramEditor!=null)
			filter = (NotificationFilter)diagramEditor.getAdapter(NotificationFilter.class);
		if (filter==null) {
			filter = NotificationFilter.NOT_TOUCH;
		}
		return filter;
	}

	@Override
	public Command transactionAboutToCommit(ResourceSetChangeEvent event)
			throws RollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	protected void getAllChildWidgets(Composite parent, List<Control>kids) {
		if (parent!=null && !parent.isDisposed()) {
			Control[] cs = parent.getChildren();
			for (Control c : cs) {
				if (c instanceof Composite) {
					getAllChildWidgets((Composite)c,kids);
				}
				if (!c.isDisposed() &&
					c.getData(IConstants.NOTIFY_CHANGE_LISTENER_KEY) instanceof INotifyChangedListener) {
						kids.add(c);
				}
			}
		}
	}
	
	// TODO: Figure out a broader method of detecting model changes.
	// This listener is only called AFTER a transaction has committed,
	// it will not receive notification of model changes inside a txn.
	// So, while this works in the Property Sheet pages, things like
	// the ObjectEditingDialog (which makes changes in the current txn)
	// will not cause other widgets in the dialog to be notified.
	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		final List<Notification> notifications = new ArrayList<Notification>();
		List<Notification> eventNotifications = event.getNotifications();
		NotificationFilter filter = getFilter();
		for (int i=eventNotifications.size()-1; i>=0; --i) {
			Notification n = eventNotifications.get(i);
			// We are only interested in notifications that affect BaseElements
			// since the {@link ObjectEditor} children only work with these objects.
			if (n.getNotifier() instanceof BaseElement) {
				if (filter.matches(n)) {
					boolean add = true;
					if (n.getFeature() instanceof EStructuralFeature) {
						EStructuralFeature f = (EStructuralFeature)n.getFeature();
						EClass ec = f.getEContainingClass();
						// Attempt to reduce the number of notifications to process:
						// notifications for the XMLTypePackage are inconsequential
						if (ec.getEPackage()==XMLTypePackage.eINSTANCE)
							add = false;
					}
					for (Notification n2 : notifications) {
						if (n2.getNotifier()==n.getNotifier() && n2.getFeature()==n.getFeature()) {
							add = false;
							break;
						}
					}
					if (add)
					{
						notifications.add(n);
					}
				}
			}
		}
//		System.out.println("resource changed: "+this.getClass().getSimpleName()+" "+notifications.size()+" notifications");		
		// run this in the UI thread
		Display.getDefault().asyncExec( new Runnable() {
			public void run() {
				List<Control>kids = new ArrayList<Control>();
				Composite parent = ListAndDetailCompositeBase.this;
				try {
					AbstractBpmn2PropertySection section = ListAndDetailCompositeBase.this.getPropertySection();
					if (section!=null && section.getTabbedPropertySheetPage()!=null) {
						parent = (Composite)section.getTabbedPropertySheetPage().getControl();
					}
				}
				catch (Exception e) {
					return;
				}

				boolean firstTime = true;
				for (Notification n : notifications) {
					if (getFilter().matches(n)) {
						if (n.getFeature() instanceof EStructuralFeature) {
//							EStructuralFeature f = (EStructuralFeature)n.getFeature();
//							EClass ec = (EClass)f.eContainer();
//							String et;
//							switch (n.getEventType()){
//							case Notification.SET: et = "SET"; break;
//							case Notification.UNSET: et = "UNSET"; break;
//							case Notification.ADD: et = "ADD"; break;
//							case Notification.ADD_MANY: et = "ADD_MANY"; break;
//							case Notification.REMOVE: et = "REMOVE"; break;
//							case Notification.REMOVE_MANY: et = "REMOVE_MANY"; break;
//							default: et = "UNKNOWN";
//							}
//							System.out.println("sending notification: "+
//									ec.getEPackage().getName()+":"+ec.getName()+"."+f.getName()+"   "+et+" old="+n.getOldStringValue()+" new="+n.getNewStringValue());
							if (firstTime) {
								getAllChildWidgets(parent, kids);
								firstTime = false;
							}
							for (Control c : kids) {
								if (!c.isDisposed() && c.isVisible()) {
									INotifyChangedListener listener = (INotifyChangedListener)c.getData(
											IConstants.NOTIFY_CHANGE_LISTENER_KEY);
									if (listener!=null) {
//										System.out.println("    "+listener.getClass().getSimpleName());
										listener.notifyChanged(n);
									}
								}
							}
						}
					}
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.transaction.ResourceSetListener#isAggregatePrecommitListener()
	 */
	@Override
	public boolean isAggregatePrecommitListener() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.transaction.ResourceSetListener#isPrecommitOnly()
	 */
	@Override
	public boolean isPrecommitOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.transaction.ResourceSetListener#isPostcommitOnly()
	 */
	@Override
	public boolean isPostcommitOnly() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setIsPopupDialog(boolean isPopupDialog) {
		this.isPopupDialog = isPopupDialog;
	}

	public void validate() {
    	Notification n = new ENotificationImpl((InternalEObject) businessObject, 0, null, null, null, false);
    	validate(n);
	}
	
	protected void validate(Notification notification) {
		IValidator<Notification> validator = ModelValidationService.getInstance().newValidator(EvaluationMode.LIVE);
		validator.validate(notification);
	}
	
	@Override
	public void notifyChanged(Notification notification) {
		validate(notification);
	}

	@Override
	public Notifier getTarget() {
		return null;
	}

	@Override
	public void setTarget(Notifier newTarget) {
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return false;
	}
}
