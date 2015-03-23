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
package org.eclipse.bpmn2.modeler.core.model;

import java.util.ArrayList;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.util.OnlyContainmentTypeInfo;
import org.eclipse.bpmn2.util.XmlExtendedMetadata;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.ElementHandlerImpl;

/**
 * The <b>Resource Factory</b> for the BPMN2 Modeler. Constructs a specialized BPMN2 Resource
 * which enhances the base Resource by adding ID lookup to sourceRef and targetRef object references. 
 * @see org.eclipse.bpmn2.util.Bpmn2ResourceImpl
 */
public class Bpmn2ModelerResourceFactoryImpl extends ResourceFactoryImpl {
    /**
     * Creates an instance of the resource eFactory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Bpmn2ModelerResourceFactoryImpl() {
        super();
    }

    /**
     * Creates an instance of the resource.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public Resource createResource(URI uri) {
        Bpmn2ModelerResourceImpl resource = new Bpmn2ModelerResourceImpl(uri);
        return resource;
    }

    /*
     * 
     * Creates a new BpmnResourceImpl and initializes it.
     * 
     * The method creates a DocumentRoot and a Definitions element, as both are
     * mandatory.
     */

    public Definitions createAndInitResource(URI uri) {
        Resource resource = createResource(uri);
        Definitions definitions = Bpmn2ModelerFactory.create(Definitions.class);
        DocumentRoot docummentRoot = Bpmn2ModelerFactory.create(DocumentRoot.class);
        docummentRoot.setDefinitions(definitions);
        resource.getContents().add(docummentRoot);

        return definitions;
    }
}
