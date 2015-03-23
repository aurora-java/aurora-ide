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

package org.eclipse.bpmn2.modeler.ui.property.diagrams;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.ParticipantMultiplicity;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.IntObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ReadonlyTextObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextObjectEditor;
import org.eclipse.bpmn2.modeler.core.utils.ErrorUtils;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.ui.property.data.InterfacePropertySection.ProvidedInterfaceListComposite;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Bob Brodt
 *
 */
public class ParticipantDetailComposite extends DefaultDetailComposite {

	protected ProvidedInterfaceListComposite providedInterfacesTable;

	public ParticipantDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param section
	 */
	public ParticipantDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}

	@Override
	public AbstractPropertiesProvider getPropertiesProvider(EObject object) {
		if (propertiesProvider==null) {
			propertiesProvider = new AbstractPropertiesProvider(object) {
				String[] properties = new String[] {
						"processRef", //$NON-NLS-1$
						"participantMultiplicity", //$NON-NLS-1$
						"interfaceRefs", //$NON-NLS-1$
						"endPointRefs", //$NON-NLS-1$
				};
				
				@Override
				public String[] getProperties() {
					return properties; 
				}
			};
		}
		return propertiesProvider;
	}

	@Override
	public void cleanBindings() {
		super.cleanBindings();
		providedInterfacesTable = null;
	}
	
	protected void bindReference(Composite parent, EObject object, EReference reference) {
		if (isModelObjectEnabled(object.eClass(), reference)) {
			if (parent==null)
				parent = getAttributesParent();

			Participant participant = (Participant) object;
			// do not allow the processRef to be changed if this is a Pool
			if ("processRef".equals(reference.getName())) { //$NON-NLS-1$
				PictogramElement pes[] = getDiagramEditor().getSelectedPictogramElements();
				if (pes.length==1) {
					if (FeatureSupport.isChoreographyParticipantBand(pes[0])) {
						super.bindReference(parent, object, reference);
					}
					else {
						// display a read-only text field containing the referenced Process name
						TextObjectEditor editor = new TextObjectEditor(this, object, reference);
						String label = getBusinessObjectDelegate().getLabel(object, reference);
						editor.createControl(parent, label);
						editor.setEditable(false);
					}
				}
			}
			else if ("participantMultiplicity".equals(reference.getName())) { //$NON-NLS-1$
				Composite composite = getToolkit().createComposite(parent);
				composite.setLayout(new GridLayout(7,true));
				composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
				createLabel(composite,Messages.ParticipantDetailComposite_Multiplicity_Label);
				
				ParticipantMultiplicity pm =
						participant.getParticipantMultiplicity() != null ?
						participant.getParticipantMultiplicity() :
						Bpmn2Factory.eINSTANCE.createParticipantMultiplicity();
						
				InsertionAdapter.add(object, Bpmn2Package.eINSTANCE.getParticipant_ParticipantMultiplicity(), pm);
				
				MyIntObjectEditor minEditor = new MyIntObjectEditor(this, pm, Bpmn2Package.eINSTANCE.getParticipantMultiplicity_Minimum());
				minEditor.createControl(composite, Messages.ParticipantDetailComposite_Mimimum_Label);
				
				MyIntObjectEditor maxEditor = new MyIntObjectEditor(this, pm, Bpmn2Package.eINSTANCE.getParticipantMultiplicity_Maximum());
				maxEditor.createControl(composite, Messages.ParticipantDetailComposite_Maximum_Label);
				
				minEditor.updateText();
			}
			else if ("interfaceRefs".equals(reference.getName())) { //$NON-NLS-1$
				providedInterfacesTable = new ProvidedInterfaceListComposite(this);
				providedInterfacesTable.bindList(object, getFeature(object, "interfaceRefs")); //$NON-NLS-1$
			}
			else {
				super.bindReference(parent, object, reference);
			}
		}
	}
	
	private class MyIntObjectEditor extends IntObjectEditor {

		public MyIntObjectEditor(AbstractDetailComposite parent,
				EObject object, EStructuralFeature feature) {
			super(parent, object, feature);
		}

		public void updateText() {
			super.updateText();
			ParticipantMultiplicity pm = (ParticipantMultiplicity) object;
			if (pm.getMinimum() >= pm.getMaximum()) {
				ErrorUtils.showErrorMessage(Messages.ParticipantDetailComposite_MinMax_Error);
			}
		}
	}
}
