/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.runtime;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultPropertySection;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor;
import org.eclipse.ui.views.properties.tabbed.ISection;

public class Bpmn2SectionDescriptor extends AbstractSectionDescriptor {

		protected String id;
		protected String tab;
		protected AbstractBpmn2PropertySection sectionClass;
		protected List<Class> appliesToClasses = new ArrayList<Class>();
		protected String enablesFor;
		protected String filterClassName;
		protected PropertySectionFilter filter;
		
		public Bpmn2SectionDescriptor(PropertyTabDescriptor td, IConfigurationElement e) {
			tab = td.getId();
			id = tab + ".section"; //$NON-NLS-1$

			try {
				String className = e.getAttribute("class"); //$NON-NLS-1$
				if ("default".equals(className)) { //$NON-NLS-1$
					sectionClass = new DefaultPropertySection();
					if (e.getAttribute("features")!=null) { //$NON-NLS-1$
						String value = e.getAttribute("features");
						if (value==null || value.isEmpty()) {
							((DefaultPropertySection)sectionClass).setProperties(new String[0]);
						}
						else {
							String[] properties = value.split(" "); //$NON-NLS-1$ //$NON-NLS-2$
							((DefaultPropertySection)sectionClass).setProperties(properties);
						}
					}
				}
				else if ("empty".equals(className)) { //$NON-NLS-1$
					// this tab is to be hidden
					sectionClass = null;
				}
				else {
					sectionClass = (AbstractBpmn2PropertySection) e.createExecutableExtension("class"); //$NON-NLS-1$
				}
				filterClassName = e.getAttribute("filter"); //$NON-NLS-1$
				if (filterClassName==null || filterClassName.isEmpty())
					filterClassName = "org.eclipse.bpmn2.modeler.core.runtime.PropertySectionFilter"; //$NON-NLS-1$
				filter = (PropertySectionFilter) Class.forName(filterClassName).getConstructor(null).newInstance(null);
				enablesFor = e.getAttribute("enablesFor"); //$NON-NLS-1$
				String type = e.getAttribute("type"); //$NON-NLS-1$
				if (type!=null && !type.isEmpty()) {
					String types[] = type.split(" "); //$NON-NLS-1$
					for (String t : types) {
						Class clazz = null;
						try {
							clazz = Class.forName(t);
						}
						catch (Exception cnf) {
							clazz = null;
						}
						if (clazz==null) {
							for (TargetRuntime rt : TargetRuntime.createTargetRuntimes()) {
								try {
									clazz = rt.getRuntimeExtension().getClass().getClassLoader().loadClass(t);
									break;
								}
								catch (Exception cnf) {
								}
							}
						}
						addAppliesToClass(clazz);
						if (sectionClass instanceof DefaultPropertySection) {
							((DefaultPropertySection)sectionClass).addAppliesToClass(clazz);
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			td.getSectionDescriptors().add(this);
		}
		
		public Bpmn2SectionDescriptor(PropertyTabDescriptor td, Bpmn2SectionDescriptor that) {
			tab = td.getId();
			id = tab + ".section" + hashCode(); //$NON-NLS-1$
			this.sectionClass = that.sectionClass;
			this.appliesToClasses.addAll(that.appliesToClasses);
			this.enablesFor = that.enablesFor;
			this.filterClassName = that.filterClassName;
			this.filter = that.filter;
		}
		
		@Override
		public String getId() {
			return id;
		}

		@Override
		public ISection getSectionClass() {
			return sectionClass;
		}

		@Override
		public String getTargetTab() {
			return tab;
		}

		protected void addAppliesToClass(Class clazz) {
			Assert.isNotNull(clazz);
			appliesToClasses.add(clazz);
		}
		
		@Override
		public boolean appliesTo(IWorkbenchPart part, ISelection selection) {

			if (sectionClass==null) {
				// this was defined as "empty" in the plugin, which means we should hide this tab.
				return false;
			}
			
			EObject businessObject = null;
			PictogramElement pe = BusinessObjectUtil.getPictogramElementForSelection(selection);
			if (pe != null) {
				if (pe instanceof ConnectionDecorator) {
					// this is a special hack to allow selection of connection decorator labels:
					// the connection decorator does not have a business object linked to it,
					// but its parent (the connection) does.
					pe = (PictogramElement) pe.eContainer();
				}
				if (!filter.select(pe))
					return false;
				businessObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(pe);
			}
			else {
				businessObject = BusinessObjectUtil.getBusinessObjectForSelection(selection);
			}
			if (businessObject==null)
				return false;

			DiagramEditor editor = ModelUtil.getDiagramEditor(businessObject);
			if (editor!=null) {
				TargetRuntime rt = (TargetRuntime) editor.getAdapter(TargetRuntime.class);
				if (rt!=null) {
					int selected = 0;
					int count = 0;
					for (CustomTaskDescriptor ctd : rt.getCustomTaskDescriptors()) {
						for (String s : ctd.getPropertyTabs()) {
							if (tab.equals(s)) {
								String id = ctd.getFeatureContainer().getId(businessObject);
								if (ctd.getId().equals(id))
									++selected;
								++count;
							}
						}
					}
					if (count>0 && selected==0)
						return false;
				}
				
				// check if the selected business object (a BPMN2 model element)
				// is contained in this editor's Resource
				Diagram diagram = editor.getDiagramTypeProvider().getDiagram();
				EObject o = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(diagram);
				if (o==null || o.eResource() != businessObject.eResource()) {
					return false;
				}
			}

			// delegate to the section class to determine whether it applies to this selection
			return sectionClass.appliesTo(part, selection);
		}

		public boolean appliesTo(EObject eObj) {
			DiagramEditor editor = ModelUtil.getDiagramEditor(eObj);
			if (editor!=null) {
				ModelEnablements me = (ModelEnablements) editor.getAdapter(ModelEnablements.class);
				if (me.isEnabled(eObj.eClass())) {
					for (Class c : appliesToClasses) {
						if (c.isInstance(eObj))
							return true;
					}
				}
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.views.properties.tabbed.AbstractSectionDescriptor#getEnablesFor()
		 * Returns the value of the "enablesFor" attribute of the configuration element in plugin.xml
		 * This is an integer value representing the number of items that must be selected for this
		 * Property Tab to be enabled.
		 */
		@Override
		public int getEnablesFor() {
			try {
				return Integer.parseInt(enablesFor);
			}
			catch (Exception ex) {
				
			}
			return super.getEnablesFor();
		}

		@Override
		public IFilter getFilter() {
			return new IFilter() {

				@Override
				public boolean select(Object toTest) {
					return false;
				}
				
			};
		}

		@Override
		public List getInputTypes() {
			return super.getInputTypes();
		}

		/**
		 * @param replacedId
		 * @param part
		 * @param selection
		 * @return
		 */
		public boolean doReplaceTab(String replacedId, IWorkbenchPart part, ISelection selection) {
			if (sectionClass instanceof IBpmn2PropertySection) {
				return ((IBpmn2PropertySection)sectionClass).doReplaceTab(replacedId, part, selection);
			}
			// If no "class" was specified in plugin.xml (e.g. class="empty") then do the replacement,
			// but don't show the property tab. In other words, this tab is to be hidden.
			if (sectionClass==null)
				return true;
			return appliesTo(part,selection);
		}
		
	}