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

package org.eclipse.bpmn2.modeler.ui.wizards;

import org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.IConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Bob Brodt
 *
 */
public class BPMN2DiagramWizardPage1 extends WizardPage implements IConstants {

	private Bpmn2DiagramType diagramType = Bpmn2DiagramType.NONE;
	private final ISelection selection;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public BPMN2DiagramWizardPage1(ISelection selection) {
		super("wizardPage1"); //$NON-NLS-1$
		setTitle(Messages.BPMN2DiagramWizardPage1_Title);
		setDescription(Messages.BPMN2DiagramWizardPage1_Description);
		this.selection = selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2,false);
		container.setLayout(layout);

		Point sz = parent.getSize();
		int labelWidth = (int)(0.5 * sz.x); 
		GridData data;
		
		final Button processButton = new Button(container, SWT.RADIO | SWT.PUSH);
//		processButton.setText("Process");
		processButton.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		processButton.setImage(Activator.getDefault().getImage(IMAGE_PROCESS));
		
		Label processLabel = new Label(container, SWT.WRAP | SWT.NONE);
		data = new GridData(SWT.LEFT,SWT.FILL,false,false,1,1);
		data.widthHint = labelWidth;
		processLabel.setLayoutData(data);
		processLabel.setText(Messages.BPMN2DiagramWizardPage1_Process_Diagram_Description);

		final Button collaborationButton = new Button(container, SWT.RADIO | SWT.PUSH);
//		collaborationButton.setText("Collaboration");
		collaborationButton.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		collaborationButton.setImage(Activator.getDefault().getImage(IMAGE_COLLABORATION));
		
		Label collaborationLabel = new Label(container, SWT.WRAP | SWT.NONE);
		data = new GridData(SWT.LEFT,SWT.FILL,false,false,1,1);
		data.widthHint = labelWidth;
		collaborationLabel.setLayoutData(data);
		collaborationLabel.setText(Messages.BPMN2DiagramWizardPage1_Collaboration_Diagram_Description);
		
		final Button choreographyButton = new Button(container, SWT.RADIO | SWT.PUSH);
//		choreographyButton.setText("Choreography");
		choreographyButton.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		choreographyButton.setImage(Activator.getDefault().getImage(IMAGE_CHOREOGRAPHY));

		Label choreographyLabel = new Label(container, SWT.WRAP | SWT.NONE);
		data = new GridData(SWT.LEFT,SWT.FILL,false,false,1,1);
		data.widthHint = labelWidth;
		choreographyLabel.setLayoutData(data);
		choreographyLabel.setText(Messages.BPMN2DiagramWizardPage1_Choreography_Diagram_Description);
		
		SelectionAdapter buttonListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == processButton) {
					diagramType = Bpmn2DiagramType.PROCESS;
					processButton.setImage(Activator.getDefault().getImage(IMAGE_PROCESS_PUSHED));
					collaborationButton.setImage(Activator.getDefault().getImage(IMAGE_COLLABORATION));
					choreographyButton.setImage(Activator.getDefault().getImage(IMAGE_CHOREOGRAPHY));
				}
				else if (e.widget == collaborationButton) {
					diagramType = Bpmn2DiagramType.COLLABORATION;
					processButton.setImage(Activator.getDefault().getImage(IMAGE_PROCESS));
					collaborationButton.setImage(Activator.getDefault().getImage(IMAGE_COLLABORATION_PUSHED));
					choreographyButton.setImage(Activator.getDefault().getImage(IMAGE_CHOREOGRAPHY));
				}
				else if (e.widget == choreographyButton) {
					diagramType = Bpmn2DiagramType.CHOREOGRAPHY;
					processButton.setImage(Activator.getDefault().getImage(IMAGE_PROCESS));
					collaborationButton.setImage(Activator.getDefault().getImage(IMAGE_COLLABORATION));
					choreographyButton.setImage(Activator.getDefault().getImage(IMAGE_CHOREOGRAPHY_PUSHED));
				}
				else {
					diagramType = Bpmn2DiagramType.NONE;
					processButton.setImage(Activator.getDefault().getImage(IMAGE_PROCESS));
					collaborationButton.setImage(Activator.getDefault().getImage(IMAGE_COLLABORATION));
					choreographyButton.setImage(Activator.getDefault().getImage(IMAGE_CHOREOGRAPHY));
				}
				setPageComplete(canFlipToNextPage());
			}
		};
		processButton.addSelectionListener(buttonListener);
		collaborationButton.addSelectionListener(buttonListener);
		choreographyButton.addSelectionListener(buttonListener);
		
		setControl(container);
	}

	@Override
	public boolean isPageComplete() {
		return diagramType != Bpmn2DiagramType.NONE;
	}

	@Override
	public boolean canFlipToNextPage() {
		return diagramType != Bpmn2DiagramType.NONE;
	}

	public Bpmn2DiagramType getDiagramType() {
		return diagramType;
	}
}
