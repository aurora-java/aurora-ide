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

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.runtime.Bpmn2SectionDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.PropertyTabDescriptor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;

public class DefaultDialogComposite extends AbstractDialogComposite {

	protected IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	protected EObject businessObject;
	protected List<AbstractDetailComposite> details = new ArrayList<AbstractDetailComposite>();
	protected List<AbstractBpmn2PropertySection> sections = new ArrayList<AbstractBpmn2PropertySection>();
	protected TabFolder folder;
	protected Composite control;
	protected ITabDescriptor[] tabDescriptors;
	protected AbstractBpmn2PropertySection section;
    private IPropertiesCompositeFactory compositeFactory = null;
	
	public DefaultDialogComposite(Composite parent, EClass eclass, int style) {
		super(parent, eclass, style);
	}
	
	protected void init() {
		Composite parent = getParent();
		setLayout(new FormLayout());
		
		if (compositeFactory!=null) {
			// client provided us with a IPropertiesCompositeFactory:
			// use it to create the Property Sheet composite
			control = compositeFactory.createDetailComposite(eclass.getInstanceClass(), this, SWT.NONE);
		}
		else {
			// Get a list of Property Sheet tabs using the current selection from the active WorkbenchPart
			// and build a TabFolder that looks like the normal tabbed Property View, except the tabs
			// are laid out horizontally instead of vertically.
			ITabDescriptor[] tabDescriptors = getTabDescriptors();
			int detailsCount = getDetailsCount();
			
			if (detailsCount>1) {
				folder = new TabFolder(this, SWT.NONE);
				folder.setLayout(new FormLayout());
				folder.setBackground(parent.getBackground());
	
				for (ITabDescriptor td : tabDescriptors) {
					for (Object o : td.getSectionDescriptors()) {
						if (o instanceof Bpmn2SectionDescriptor) {
							Bpmn2SectionDescriptor sd = (Bpmn2SectionDescriptor)o;
				
							TabItem tab = new TabItem(folder, SWT.NONE);
							ScrolledForm form = new ScrolledForm(folder, SWT.V_SCROLL);
							form.setBackground(parent.getBackground());
							FormData data = new FormData();
							data.top = new FormAttachment(0, 0);
							data.bottom = new FormAttachment(100, 0);
							data.left = new FormAttachment(0, 0);
							data.right = new FormAttachment(100, 0);
		
							form.setLayoutData(data);
							form.setExpandVertical(true);
							form.setExpandHorizontal(true);
							form.setBackground(parent.getBackground());
							
							Composite body = form.getBody();
							TableWrapLayout tableWrapLayout = new TableWrapLayout();
							tableWrapLayout.numColumns = 1;
							tableWrapLayout.verticalSpacing = 1;
							tableWrapLayout.horizontalSpacing = 1;
							TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
							body.setLayout(tableWrapLayout);
							body.setLayoutData(twd);
							
							section = (AbstractBpmn2PropertySection)sd.getSectionClass();
							AbstractDetailComposite detail = getDetail(section, body);
							detail.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
							form.setContent(body);
							
							tab.setText(td.getLabel());
							tab.setControl(form);
							details.add(detail);
							sections.add(section);
						}
					}
				}
				control = folder;
				control.setBackground(parent.getBackground());
			}
			else if (section!=null) {
				control = section.createSectionRoot(this,SWT.NONE);
			}
		}
		if (control==null) {
			// Unable to get the current selection from the WorkbenchPart?
			// Look up the Property Sheet composite in the global registry.
			control = PropertiesCompositeFactory.INSTANCE.createDetailComposite(eclass.getInstanceClass(), this, SWT.NONE);
		}
		
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		control.setLayoutData(data);
		layout(true);
	}
	
	protected ITabDescriptor[] getTabDescriptors() {
		if (tabDescriptors==null) {
			IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart();
			part.getAdapter(ITabDescriptorProvider.class);
			ISelection selection;
			if (businessObject==null) {
				selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
			}
			else {
				selection = new StructuredSelection(businessObject);
			}
			ITabDescriptorProvider tabDescriptorProvider = (ITabDescriptorProvider)part.getAdapter(ITabDescriptorProvider.class);
			List<ITabDescriptor> list = new ArrayList<ITabDescriptor>();
			for (ITabDescriptor td : tabDescriptorProvider.getTabDescriptors(part,selection)) {
				if (td instanceof PropertyTabDescriptor && !((PropertyTabDescriptor)td).isPopup()) {
					// exclude this tab if not intended for popup dialog
					continue;
				}
				list.add(td);
			}
			tabDescriptors = list.toArray(new ITabDescriptor[list.size()]);
		}
		return tabDescriptors;
	}
	
	protected int getDetailsCount() {
		int detailsCount = 0;
		ITabDescriptor[] tabDescriptors = getTabDescriptors();
		for (ITabDescriptor td : tabDescriptors) {
			if (td instanceof PropertyTabDescriptor && !((PropertyTabDescriptor)td).isPopup()) {
				// exclude this tab if not intended for popup dialog
				continue;
			}
			
			for (Object o : td.getSectionDescriptors()) {
				if (o instanceof Bpmn2SectionDescriptor) {
					Bpmn2SectionDescriptor sd = (Bpmn2SectionDescriptor)o;
					section = (AbstractBpmn2PropertySection)sd.getSectionClass();
					++detailsCount;
				}
			}
		}
		return detailsCount;
	}
	
	protected AbstractDetailComposite getDetail(AbstractBpmn2PropertySection section, Composite parent) {
		return section.createSectionRoot(parent,SWT.NONE);
	}
	
	@Override
	public void setData(String key, Object object) {
		if ("factory".equals(key) && object instanceof IPropertiesCompositeFactory) //$NON-NLS-1$
			compositeFactory = (IPropertiesCompositeFactory) object;
	}
	
	@Override
	public void setData(Object object) {
		businessObject = (EObject)object;
		init();
		
		if (details!=null && details.size()>0) {
			for (int i=0; i<details.size(); ++i) {
				AbstractDetailComposite detail = details.get(i);
				AbstractBpmn2PropertySection section = sections.get(i);
				detail.setIsPopupDialog(true);
				StructuredSelection selection = new StructuredSelection(businessObject);
				EObject bo = section.getBusinessObjectForSelection(selection);
				detail.setBusinessObject(bo);
			}
		}
		else if (control instanceof AbstractDetailComposite) {
			((AbstractDetailComposite)control).setIsPopupDialog(true);
			((AbstractDetailComposite)control).setBusinessObject(businessObject);
		}
		
		if (folder!=null) {
			int i = preferenceStore.getInt("dialog."+eclass.getName()+".tab"); //$NON-NLS-1$ //$NON-NLS-2$
			if (i>=0 && i<folder.getItemCount())
				folder.setSelection(i);
			folder.addSelectionListener( new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int i = folder.getSelectionIndex();
					preferenceStore.setValue("dialog."+eclass.getName()+".tab", i); //$NON-NLS-1$ //$NON-NLS-2$
				}
			});
			
			if (details!=null) {
				List<TabItem> removedTabs = new ArrayList<TabItem>();
				List<AbstractDetailComposite> removedDetails = new ArrayList<AbstractDetailComposite>();
				for (i=0; i<details.size(); ++i) {
					AbstractDetailComposite detail = details.get(i);
					if (detail.isEmpty()) {
						removedTabs.add(folder.getItem(i));
						removedDetails.add(detail);
					}
				}
				for (TabItem tab : removedTabs) {
					tab.dispose();
				}
				details.removeAll(removedDetails);
			}
		}
	}
	
	@Override
	public void dispose() {
		if (details!=null) {
			for (AbstractDetailComposite detail : details) {
				detail.dispose();
			}
		}
		control.dispose();
		super.dispose();
	}
}
