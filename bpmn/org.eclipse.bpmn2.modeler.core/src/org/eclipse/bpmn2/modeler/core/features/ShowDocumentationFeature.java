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
package org.eclipse.bpmn2.modeler.core.features;

import java.util.List;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.modeler.core.IConstants;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.AbstractObjectEditingDialog;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The Class ShowPropertiesFeature.
 */
public class ShowDocumentationFeature extends AbstractCustomFeature {

	/** The changes done. */
	protected boolean changesDone = false;
	
	/** The current context */
	ICustomContext context;
	
	/**
	 * Instantiates a new show properties feature.
	 *
	 * @param fp the fp
	 */
	public ShowDocumentationFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return Messages.ShowDocumentationFeature_Documentation_Title;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		if (context!=null) {
			PictogramElement[] pes = context.getPictogramElements();
			EObject businessObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(pes[0]);
			EStructuralFeature feature = businessObject.eClass().getEStructuralFeature("documentation"); //$NON-NLS-1$
			List<Documentation> docList = (List<Documentation>)businessObject.eGet(feature);
			if (docList.size()>0) {
				String text = docList.get(0).getText();
				if (text!=null)
					return text;
			}
		}
		return ""; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		EObject businessObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(pes[0]);
		if (businessObject!=null && pes.length==1) {
			EStructuralFeature feature = businessObject.eClass().getEStructuralFeature("documentation"); //$NON-NLS-1$
			if (feature!=null) {
				ModelEnablements me = getModelEnablements();
				if (me!=null && me.isEnabled(businessObject.eClass().getName(), feature.getName())) {
					this.context = context;
					return true;
				}
			}
		}
		this.context = null;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		EObject businessObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(pes[0]);
		EStructuralFeature feature = businessObject.eClass().getEStructuralFeature("documentation"); //$NON-NLS-1$
		List<Documentation> docList = (List<Documentation>)businessObject.eGet(feature);
		Documentation documentation = null;
		String text = ""; //$NON-NLS-1$
		if (docList.size()>0) {
			documentation = docList.get(0);
			text = documentation.getText();
		}
		
		DocumentationDialog dialog = new DocumentationDialog(Display.getDefault().getActiveShell());
		dialog.setValue(text);
		if (dialog.open() == Window.OK){
			text = dialog.getValue();
			if (documentation==null) {
				if (text.isEmpty())
					return;
				documentation = Bpmn2Factory.eINSTANCE.createDocumentation();
				docList.add(documentation);
			}
			documentation.setText(text);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
	 */
	@Override
	public boolean hasDoneChanges() {
		return changesDone;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getImageId()
	 */
	@Override
	public String getImageId() {
		return IConstants.ICON_INFO_16;
	}
	
	/**
	 * Gets the model enablements.
	 * TODO: consider moving this stuff to a superclass
	 *
	 * @return the model enablements
	 */
	protected ModelEnablements getModelEnablements() {
		DiagramEditor editor = (DiagramEditor) getDiagramEditor();
		return (ModelEnablements) editor.getAdapter(ModelEnablements.class);
	}

	public class DocumentationDialog extends Dialog {
		
		/**
		 * @param parentShell
		 */
		protected DocumentationDialog(Shell parentShell) {
			super(parentShell);
		}

		String value;
		StyledText text;
		Font descriptionFont;
		Display display = Display.getDefault();
		
		public Font getDescriptionFont() {
			if (descriptionFont==null) {
			    FontData data = display.getSystemFont().getFontData()[0];
			    descriptionFont = new Font(display, data.getName(), data.getHeight() + 1, SWT.NONE);
			}
			return descriptionFont;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);
			container.setLayout(new GridLayout(2,true));
			text = new StyledText(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
			text.setText(value);

		    text.setFont(getDescriptionFont());
			
//			text.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
//			text.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
//			text.setData(AbstractObjectEditingDialog.DO_NOT_ADAPT , Boolean.TRUE);
			
			GridData d = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
			d.horizontalIndent = 0;
			d.verticalIndent = 0;
			d.heightHint = (int)(5.5 * getDescriptionFont().getFontData()[0].getHeight());
			d.widthHint = 100;
			text.setLayoutData(d);
			
			return container;
		}

		@Override
		protected void okPressed() {
			value = text.getText();
			super.okPressed();
		}
		
		@Override
		protected void configureShell(Shell newShell) {
			newShell.setText(Messages.ShowDocumentationFeature_Documentation_Title);
			super.configureShell(newShell);
		}

		protected int getShellStyle() {
			return super.getShellStyle(); //SWT.TOP | SWT.RESIZE | SWT.BORDER | SWT.TOOL;
		}

		@Override
		protected Point getInitialSize() {
			return new Point(450, 300);
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
	}
}
