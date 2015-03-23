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
package org.eclipse.bpmn2.modeler.core.merrimac.clad;

import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.runtime.IBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.help.IHelpContexts;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public abstract class AbstractBpmn2PropertySection extends GFPropertySection implements IBpmn2PropertySection {
	
	protected TabbedPropertySheetPage tabbedPropertySheetPage;
	protected Composite parent;
	protected DiagramEditor editor;
	private IWorkbenchWindow cachedWorkbenchWindow;
	
	private IPartListener partActivationListener = new IPartListener() {

		public void partActivated(IWorkbenchPart part) {
			Object bpmn2Editor = part.getAdapter(DiagramEditor.class);
			if (bpmn2Editor instanceof DiagramEditor) {
				editor = (DiagramEditor)bpmn2Editor;
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
		}
	};
	
	public AbstractBpmn2PropertySection() {
		cachedWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (cachedWorkbenchWindow != null) {
			cachedWorkbenchWindow.getPartService().addPartListener(
				partActivationListener);
		}
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		Composite parent = getParent();
		if (parent!=null && !parent.isDisposed())
			PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContexts.Property_View);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (cachedWorkbenchWindow != null) {
			cachedWorkbenchWindow.getPartService().removePartListener(
				partActivationListener);
			cachedWorkbenchWindow = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (e.widget instanceof Composite) {
					Composite parent = ((Composite)e.widget);
					Control[] kids = parent.getChildren();
					for (Control c : kids)
						c.dispose();
				}
			}
			
		});
		this.tabbedPropertySheetPage = aTabbedPropertySheetPage;
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));
//		editor = DiagramEditor.getActiveEditor();
	}

	/**
	 * Returns the section's parent composite. This is the composite that was
	 * created by the TabbedPropertySheetPage, not the "root" composite for
	 * this section.
	 * 
	 * @return the TabbedPropertySheetPage composite.
	 */
	public Composite getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.platform.GFPropertySection#getDiagramEditor()
	 */
	@Override
	protected DiagramEditor getDiagramEditor() {
		return editor;
	}

//	protected ModelHandler getModelHandler() {
//		if (editor!=null)
//			return editor.getModelHandler();
//		return null;
//	}
	
	/**
	 * Returns the property section's TabbedPropertySheetPage
	 * 
	 * @return the TabbedPropertySheetPage that owns this section.
	 */
	public TabbedPropertySheetPage getTabbedPropertySheetPage() {
		/**
		 * Check if the Tabbed Property Sheet Page is still alive. This prevents SWT
		 * widget disposed errors during a stray refresh attempt when the editor is
		 * shutting down.
		 */
		if (tabbedPropertySheetPage!=null
					&& tabbedPropertySheetPage.getControl()!=null
					&& !tabbedPropertySheetPage.getControl().isDisposed())
			return tabbedPropertySheetPage;
		return null;
	}

	/**
	 * Get the section's root composite, which is a subclass of AbstractBpmn2DetailComposite.
	 * Create the composite if it has not been created yet.
	 * 
	 * @return the composite
	 */
	public AbstractDetailComposite getSectionRoot() {
		AbstractDetailComposite sectionRoot = null;
		if (parent!=null && !parent.isDisposed()) {
			if (parent.getChildren().length==0) {
				sectionRoot = createSectionRoot();
				sectionRoot.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
			}
			sectionRoot = (AbstractDetailComposite)parent.getChildren()[0];
		}
		return sectionRoot;
	}
	
	/**
	 * The subclass must provide the parent Composite it created in createControls()
	 * so that we can pass the PropertySheetPage down to the Composite. This is
	 * useful for allowing the Composite to resize itself based on the parent's size.
	 * 
	 * @return
	 */
	protected abstract AbstractDetailComposite createSectionRoot();
	public abstract AbstractDetailComposite createSectionRoot(Composite parent, int style);
	public abstract EObject getBusinessObjectForSelection(ISelection selection);
	
	/* (non-Javadoc)
	 * Yet another ugly hack: this restores the current property sheet page parent
	 * composite when a different DiagramEditor is activated. Apparently TabbedPropertySheetPage
	 * does not do this for us automatically!
	 *  
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Object bpmn2Editor = part.getAdapter(DiagramEditor.class);
		if (bpmn2Editor instanceof DiagramEditor) {
			editor = (DiagramEditor)bpmn2Editor;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if (getTabbedPropertySheetPage()!=null) {
			EObject be = getBusinessObjectForSelection(getSelection());
			
			if (be!=null) {
				AbstractDetailComposite sectionRoot = getSectionRoot();
				if (sectionRoot!=null) {
					if (sectionRoot.getBusinessObject() != be) {
						sectionRoot.setDiagramEditor((DiagramEditor) getDiagramEditor());
						if (!parent.isLayoutDeferred())
							parent.setLayoutDeferred(true);
						sectionRoot.setBusinessObject(be);
						if (parent.isLayoutDeferred())
							parent.setLayoutDeferred(false);
					}
					sectionRoot.refresh();
				}
			}
		}
	}

	@Override
	// make this public!
	public PictogramElement getSelectedPictogramElement() {
		return super.getSelectedPictogramElement();
	}

	/**
	 * Force a layout of the property sheet page.
	 */
	public void layout() {
		if (getTabbedPropertySheetPage()!=null) {
			Composite composite = (Composite)tabbedPropertySheetPage.getControl();
			composite.layout(true);
			tabbedPropertySheetPage.resizeScrolledComposite();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#shouldUseExtraSpace()
	 */
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	/* (non-Javadoc)
	 * Override this to allow the section to decide whether or not it will be rendered.
	 * Subclasses MUST call this method because it sets the DiagramEditor as a side effect
	 * and checks if the selected business object is enabled in the Tool Profile.
	 * 
	 * @see org.eclipse.bpmn2.modeler.core.runtime.IBpmn2PropertySection#appliesTo(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public boolean appliesTo(IWorkbenchPart part, ISelection selection) {
		if (part instanceof ContentOutline) {
			ContentOutline outline = (ContentOutline)part;
			IContributedContentsView v = (IContributedContentsView)outline.getAdapter(IContributedContentsView.class);
			if (v!=null)
				part = v.getContributingPart();
		}
		editor = (DiagramEditor)part.getAdapter(DiagramEditor.class);
	
		if (editor!=null) {
			EObject be = getBusinessObjectForSelection(selection);
			if (be!=null)
				return isModelObjectEnabled(be);
		}
		return false;
	}
	
	protected boolean isModelObjectEnabled(EObject o, String featureName) {
		EClass eclass = (o instanceof EClass) ? (EClass)o : o.eClass();
		EStructuralFeature f = eclass.getEStructuralFeature(featureName);
		return isModelObjectEnabled(o, f);
	}
	
	protected boolean isModelObjectEnabled(EObject o, EStructuralFeature f) {
		if (o !=null && f!=null) {
			ModelEnablements me = getModelEnablements();
			if (me!=null) {
				EClass eclass = (o instanceof EClass) ? (EClass)o : o.eClass();
				return me.isEnabled(eclass, f);
			}
		}
		return false;
	}
	
	protected boolean isModelObjectEnabled(EObject o) {
		if (o !=null) {
			ModelEnablements me = getModelEnablements();
			if (me!=null) {
				EClass eclass = (o instanceof EClass) ? (EClass)o : o.eClass();
				return me.isEnabled(eclass);
			}
		}
		return false;
	}

	protected ModelEnablements getModelEnablements() {
		return (ModelEnablements)getDiagramEditor().getAdapter(ModelEnablements.class);
	}
	
	public boolean doReplaceTab(String id, IWorkbenchPart part, ISelection selection) {
		return true;
	}
}
