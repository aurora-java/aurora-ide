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

package org.eclipse.bpmn2.modeler.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.ItemKind;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerResourceSetImpl;
import org.eclipse.bpmn2.util.Bpmn2Resource;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;

/**
 * @author Bob Brodt
 *
 */
public class ImportUtil {

	public static final String IMPORT_TYPE_WSDL = "http://schemas.xmlsoap.org/wsdl/"; //$NON-NLS-1$
	public static final String IMPORT_TYPE_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
	public static final String IMPORT_TYPE_JAVA = "http://www.java.com/javaTypes"; //$NON-NLS-1$
	public static final String IMPORT_TYPE_BPMN2 = "http://www.omg.org/spec/BPMN/20100524/MODEL"; //$NON-NLS-1$
	
	public static final String IMPORT_KIND_WSDL = "wsdl"; //$NON-NLS-1$
	public static final String IMPORT_KIND_XML_SCHEMA = "xsd"; //$NON-NLS-1$
	public static final String IMPORT_KIND_JAVA = "java"; //$NON-NLS-1$
	public static final String IMPORT_KIND_BPMN2 = "bpmn2"; //$NON-NLS-1$
	
	protected Bpmn2ModelerResourceSetImpl fHackedResourceSet;

	public Object loadImport(Import imp) {
		if (fHackedResourceSet==null) {
			ResourceSet rs =  imp.eResource().getResourceSet();
			fHackedResourceSet = ModelUtil.slightlyHackedResourceSet(rs);
		}
		URI uri;
		String type = imp.getImportType();
		String kind = null;
		if (IMPORT_TYPE_WSDL.equals(type))
			kind = IMPORT_KIND_WSDL;
		else if (IMPORT_TYPE_XML_SCHEMA.equals(type))
			kind = IMPORT_KIND_XML_SCHEMA;
		else if (IMPORT_TYPE_JAVA.equals(type))
			kind = IMPORT_KIND_JAVA;
		else if (IMPORT_TYPE_BPMN2.equals(type))
			kind = "bpmn"; //$NON-NLS-1$
		else {
			throw new IllegalArgumentException("Unsupported Import type: "+type);
		}
		String location = imp.getLocation();
		if (location==null) {
			location = ""; //$NON-NLS-1$
		}
		uri = URI.createURI(location);
		return loadImport(uri,kind);
	}
	
	public Import findImportForNamespace(Resource resource, String namespace) {
		if (namespace!=null && !namespace.isEmpty()) {
			Definitions definitions = ModelUtil.getDefinitions(resource);
			for (Import imp : definitions.getImports()) {
				if (namespace.equals(imp.getNamespace()))
					return imp;
			}
		}
		return null;
	}
	
	public Import findImportForObject(Resource resource, Object o) {
		String namespace = null;
		if (o instanceof EObject) {
			EObject object = (EObject)o;
			while (object!=null) {
				if (object instanceof Definition) {
					// WSDL import
					namespace = ((Definition)object).getTargetNamespace();
				}
				else if (object instanceof XSDSchema) {
					// XSD Schema import
					namespace = ((XSDSchema)object).getTargetNamespace();
				}
				else if (object instanceof Definitions) {
					// BPMN2 import
					namespace = ((Definitions)object).getTargetNamespace();
				}
				object = object.eContainer();
			}
		}
		else if (o instanceof IType) {
			// TODO: what is the namespace for Java types?
		}
		
		return findImportForNamespace(resource, namespace);
	}
	
	public Object getObjectForLocalname(Import imp, EObject referencingObject, EReference referencingFeature, String localname) {
		// Load the import file: if successful, this should give us its contents which will be an EObject
		Object result = loadImport(imp);
		if (result instanceof EObject) {
			EObject contents = (EObject)result;
			// Depending on the import type, determine the object hierarchy using the given object
			// and feature. The "id" string will identify which object is being referenced in the import.
			if (IMPORT_TYPE_WSDL.equals(imp.getImportType())) {
				// the import is a WSDL which may generate the following BPMN2 elements:
				// Interface
				// Operation
				// Message
				// Error
				// ItemDefinition
				if (referencingObject instanceof Interface) {
					if (referencingFeature == Bpmn2Package.eINSTANCE.getInterface_ImplementationRef()) {
						// this corresponds to a WSDL PortType
						TreeIterator<EObject> iter = contents.eAllContents();
						while (iter.hasNext()) {
							EObject o = iter.next();
							if (o instanceof PortType) {
								if (localname.equals(getLocalnameForObject(o)))
									return o;
							}
						}
					}
				}
				else if (referencingObject instanceof org.eclipse.bpmn2.Operation) {
					if (referencingFeature == Bpmn2Package.eINSTANCE.getOperation_ImplementationRef()) {
						// this corresponds to a WSDL Operation
						TreeIterator<EObject> iter = contents.eAllContents();
						while (iter.hasNext()) {
							EObject o = iter.next();
							if (o instanceof Operation) {
								if (localname.equals(getLocalnameForObject(o)))
									return o;
							}
						}
					}
				}
				else if (referencingObject instanceof org.eclipse.bpmn2.Message) {
					if (referencingFeature == Bpmn2Package.eINSTANCE.getMessage_ItemRef()) {
						// this corresponds to a WSDL Message
						TreeIterator<EObject> iter = contents.eAllContents();
						while (iter.hasNext()) {
							EObject o = iter.next();
							if (o instanceof Message) {
								if (localname.equals(getLocalnameForObject(o)))
									return o;
							}
						}
					}
				}
				else if (referencingObject instanceof org.eclipse.bpmn2.Error) {
					if (referencingFeature == Bpmn2Package.eINSTANCE.getError_StructureRef()) {
						// this corresponds to a WSDL Fault
						TreeIterator<EObject> iter = contents.eAllContents();
						while (iter.hasNext()) {
							EObject o = iter.next();
							if (o instanceof Fault) {
								if (localname.equals(getLocalnameForObject(o)))
									return o;
							}
						}
					}
				}
				else if (referencingObject instanceof ItemDefinition) {
					if (referencingFeature == Bpmn2Package.eINSTANCE.getItemDefinition_StructureRef()) {
						// this corresponds to a WSDL Message or Fault, or an XSD element declaration
						TreeIterator<EObject> iter = contents.eAllContents();
						while (iter.hasNext()) {
							EObject o = iter.next();
							if (o instanceof Message || o instanceof Fault || o instanceof XSDElementDeclaration) {
								if (localname.equals(getLocalnameForObject(o)))
									return o;
							}
						}
					}
				}
			}
			else if (IMPORT_TYPE_XML_SCHEMA.equals(imp.getImportType())) {
				// XML Schema imports can only generate ItemDefinitions
				if (referencingObject instanceof ItemDefinition) {
					if (referencingFeature == Bpmn2Package.eINSTANCE.getItemDefinition_StructureRef()) {
						// this corresponds to a WSDL Message or Fault, or an XSD element declaration
						TreeIterator<EObject> iter = contents.eAllContents();
						while (iter.hasNext()) {
							EObject o = iter.next();
							if (localname.equals(getLocalnameForObject(o)))
								return o;
							if (o instanceof XSDElementDeclaration) {
								if (localname.equals(getLocalnameForObject(o)))
									return o;
							}
						}
					}
				}
			}
			else if (IMPORT_TYPE_BPMN2.equals(imp.getImportType())) {
				// TODO: figure out what to do with BPMN2 imports...
			}
		}
		else if (result instanceof List) {
			// It's a Java import, which may generate the following BPMN2 elements:
			// Interface
			// Operation
			// Message
			// Error
			// ItemDefinition

			// TODO: can we wrap an IType in an EObject?
			List<IType> types = (List<IType>)result;
		}
		return null;
	}
	
	public String getLocalnameForObject(Object o) {
		if (o instanceof PortType) {
			return ((PortType)o).getQName().getLocalPart();
		}
		if (o instanceof Operation) {
			return ((Operation)o).getName();
		}
		if (o instanceof Message) {
			return ((Message)o).getQName().getLocalPart();
		}
		if (o instanceof Fault) {
			return ((Fault)o).getName();
		}
		if (o instanceof XSDElementDeclaration) {
			return ((XSDElementDeclaration)o).getName();
		}
		return null;
	}
	
	// FIXME: {@see ICustomElementFeatureContainer#getId(EObject)}
	public static String getImportKind(Object object) {
		String kind = null;
		if (object instanceof IFile) {
			String ext = ((IFile)object).getFileExtension();
			if ("xml".equals(ext) || "xsd".equals(ext))
				kind = IMPORT_KIND_XML_SCHEMA;
			else if ("bpmn".equals(ext) || "bpmn2".equals(ext))
				kind = IMPORT_KIND_BPMN2;
			else if ("wsdl".equals(ext))
				kind = IMPORT_KIND_WSDL;
		}
		else if (object instanceof IType) {
			kind = IMPORT_KIND_JAVA;
		}
		return kind;
	}		
	
	public Object loadImport(URI uri, String kind) {
		return loadImport(this.fHackedResourceSet, uri, kind);
	}		
	
	public Object loadImport(Bpmn2ModelerResourceSetImpl resourceSet, URI uri, String kind) {

		Resource resource = null;
		if (IMPORT_KIND_JAVA.equals(kind)) {
			final String fileName = uri.lastSegment();
			final List<IType> results = new ArrayList<IType>();
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject p : projects) {
				try {
					if (p.isOpen() && p.hasNature(JavaCore.NATURE_ID)) {
						final IJavaProject javaProject = JavaCore.create(p);
						JavaProjectClassLoader cl = new JavaProjectClassLoader(javaProject);
						results.addAll(cl.findClasses(fileName));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return results;
		}
		else {
			try {
				resource = resourceSet.getResource(uri, true, kind);
			} catch (Throwable t) {
				return t;
			}
		
			if (resource!=null && resource.getErrors().isEmpty() && resource.isLoaded() && resource.getContents().size()>0) {
				if (!resource.isTrackingModification()) {
					// set modification tracking on so Graphiti's EMFService doesn't try to save this thing!
					resource.setTrackingModification(true);
				}
				if (kind.equals(IMPORT_KIND_BPMN2))
					return ModelUtil.getDefinitions(resource);
				return resource.getContents().get(0);
			}
		}
		return null;
	}

	protected Object loadImport(Bpmn2ModelerResourceSetImpl resourceSet, IFile file, String kind) {
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		return loadImport(resourceSet, uri, kind);
	}

	/**
	 * Attempt to resolve object ID references by examining all files in the
	 * current Project. The referencing object and feature are used to determine
	 * the type of object being referenced. Currently only CallableElements,
	 * XSDElementDeclarations and WSDL PortTypes are supported as referenced
	 * objects.
	 * 
	 * @param object the referencing object
	 * @param feature the feature of the referencing object that identifies the
	 *            object being referenced
	 * @param id the ID string of the referenced object
	 */
	public EObject resolveExternalReference(EObject object, EStructuralFeature feature, String id) {
		Resource resource = object.eResource();
		Bpmn2ModelerResourceSetImpl rs = new Bpmn2ModelerResourceSetImpl();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile file = workspace.getRoot().getFile(new Path(resource.getURI().toPlatformString(true)));
		IContainer container = file.getProject();
		List<IFile> files = new ArrayList<IFile>();
		if (object instanceof CallActivity) {
			if (feature == Bpmn2Package.eINSTANCE.getCallActivity_CalledElementRef()) {
				// search other BPMN2 files in this project for a CallableElement
				findAllFiles(container, new String[] {"bpmn","bpmn2"}, files);
				for (IFile f : files) {
					if (f==file)
						continue;
					Object root = loadImport(rs, f, IMPORT_KIND_BPMN2);
					if (root instanceof Definitions) {
						TreeIterator<EObject> iter = ((Definitions)root).eAllContents();
						while (iter.hasNext()) {
							EObject o = iter.next();
							if (o instanceof CallableElement) {
								if (id.equals( ((CallableElement)o).getId() )) {
									return o;
								}
							}
						}
					}
				}
			}
		}
		else if (object instanceof ItemDefinition) {
			if (feature == Bpmn2Package.eINSTANCE.getItemDefinition_StructureRef()) {
				findAllFiles(container, new String[] {"xml","xsd"}, files);
				for (IFile f : files) {
					if (f==file)
						continue;
					Object root = loadImport(rs, f, IMPORT_KIND_XML_SCHEMA);
					if (root instanceof XSDSchema) {
						TreeIterator<EObject> iter = ((XSDSchema)root).eAllContents();
						while (iter.hasNext()) {
							EObject o = iter.next();
							if (o instanceof XSDElementDeclaration) {
								String name = getLocalnameForObject(o);
								if ( id.equals(name) )
									return o;
							}
						}
					}
				}
			}			
		}
		else if (object instanceof Interface) {
			if (feature == Bpmn2Package.eINSTANCE.getInterface_ImplementationRef()) {
				// Look for a WSDL PortType or a Java type
				findAllFiles(container, new String[] {"wsdl"}, files);
				for (IFile f : files) {
					if (f==file)
						continue;
					Object root = loadImport(rs, f, IMPORT_KIND_WSDL);
					if (root instanceof org.eclipse.wst.wsdl.Definition) {
						TreeIterator<EObject> iter = ((Definition)root).eAllContents();
						while (iter.hasNext()) {
							EObject o = iter.next();
							if (o instanceof PortType) {
								String name = getLocalnameForObject(o);
								if ( id.equals(name) )
									return o;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private void findAllFiles(IContainer container, String[] extensions, List<IFile> files) {
		try {
			for (IResource res : container.members()) {
				if (res instanceof IFile) {
					String ext = ((IFile)res).getFileExtension();
					for (String s : extensions) {
						if (s.equals(ext)) {
							files.add((IFile)res);
						}
					}
				}
				else if (res instanceof IContainer) {
					findAllFiles((IContainer)res, extensions, files);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Convenience method for <code>addImport(Resource,Object)</code>
	 * 
	 * @param modelObject - an EObject that is currently contained in a Resource
	 * @param importObject - the import model object. This can be any of the following:
	 *    WSDL Definition object
	 *    XSDSchema object
	 *    BPMN2.0 Definitions object
	 *    Java Class object
	 * @return the newly constructed Import
	 */
	public Import addImport(EObject modelObject, Object importObject) {
		Resource resource = modelObject.eResource();
		return addImport(resource,importObject);
	}

	/**
	 * Create and add an Import object to the given BPMN2 Resource. This will also create
	 * all of the defined ItemDefinition, Message, Operation and Interface objects as
	 * defined in the imported resource "importObject".
	 *  
	 * @param resource - the target Resource. The new Import will be added to the RootElements in
	 * the Definitions object.
	 * @param importObject - the import model object. This can be any of the following:
	 *    WSDL Definition object
	 *    XSDSchema object
	 *    BPMN2.0 Definitions object
	 *    Java Class object
	 * @return the newly constructed Import
	 */
	public Import addImport(Resource resource, final Object importObject) {
		Import imp = null;
		if (resource instanceof Bpmn2Resource) {
			final Definitions definitions = ModelUtil.getDefinitions(resource);
	
			if (importObject instanceof org.eclipse.wst.wsdl.Definition) {
				// WSDL Definition
				Definition wsdlDefinition = (Definition)importObject;
	
				imp = Bpmn2ModelerFactory.create(Import.class);
				imp.setImportType(IMPORT_TYPE_WSDL);
				imp.setLocation(makeURIRelative(resource.getURI(), wsdlDefinition.getLocation()));
				imp.setNamespace(wsdlDefinition.getTargetNamespace());
			}
			else if (importObject instanceof XSDSchema){
				// XSD Schema
				XSDSchema schema = (XSDSchema)importObject;
				
				imp = Bpmn2ModelerFactory.create(Import.class);
				imp.setImportType(IMPORT_TYPE_XML_SCHEMA);
				imp.setLocation(makeURIRelative(resource.getURI(), schema.getSchemaLocation()));
				imp.setNamespace(schema.getTargetNamespace());
			}
			else if (importObject instanceof IType) {
				// Java class
			    IType clazz = (IType)importObject;
				// TODO: create a location URI for the class file
//				ClassLoader cl = clazz.getClassLoader();
//				String name = clazz.getName().replaceAll("\\.", "/").concat(".class");
//				java.net.URL url = cl.getResource(name);
//				URI uri = URI.createPlatformPluginURI(url.getPath(), true);
				imp = Bpmn2ModelerFactory.create(Import.class);
				imp.setImportType(IMPORT_TYPE_JAVA);
				imp.setLocation(clazz.getFullyQualifiedName('.'));
				imp.setNamespace("http://" + clazz.getPackageFragment().getElementName()); //$NON-NLS-1$
			}
			else if (importObject instanceof Definitions) {
				// BPMN 2.0 Diagram file
				Definitions defs = (Definitions)importObject;
				
				imp = Bpmn2ModelerFactory.create(Import.class);
				imp.setImportType(IMPORT_TYPE_BPMN2);
				imp.setLocation(makeURIRelative(resource.getURI(), defs.eResource().getURI().toString()));
				imp.setNamespace(defs.getTargetNamespace());
			}

			if (imp!=null) {
				// make sure this is a new one!
				for (Import i : definitions.getImports()) {
					String location = i.getLocation();
					if (location!=null && location.equals(imp.getLocation())) {
						imp = null;
						break;
					}
				}
			}
			
			if (imp!=null) {
				definitions.getImports().add(imp);
				NamespaceUtil.addNamespace(imp.eResource(), imp.getNamespace());
				addImportObjects(imp, importObject);
			}
		}
		return imp;
	}
	
	public void addImportObjects(Import imp, Object importObject) {

		final Definitions definitions = (Definitions) ModelUtil.getDefinitions(imp);
		
		if (importObject instanceof org.eclipse.wst.wsdl.Definition) {
			// WSDL Definition
			Definition wsdlDefinition = (Definition)importObject;

			// WSDL Bindings are optional, instead create a new
			// BPMN2 Interface for each PortType found in the WSDL.
//					for (Binding b : (List<Binding>)wsdlDefinition.getEBindings()) {
//						createInterface(definitions, imp,  b.getEPortType());
//					}
			for (PortType pt : (List<PortType>)wsdlDefinition.getEPortTypes()) {
				createInterface(definitions, imp,  pt);
			}

			// create XSD types (if any) defined in the WSDL
			Types t = wsdlDefinition.getETypes();
			if (t!=null) {
				for (Object s : t.getSchemas()) {
					if (s instanceof XSDSchema) {
						XSDSchema schema = (XSDSchema)s;

						for (XSDElementDeclaration elem : schema.getElementDeclarations()) {
							createItemDefinition(definitions, imp, elem, ItemKind.INFORMATION);
						}
					}
				}
			}
		}
		else if (importObject instanceof XSDSchema){
			XSDSchema schema = (XSDSchema)importObject;

			for (XSDElementDeclaration elem : schema.getElementDeclarations()) {
				createItemDefinition(definitions, imp, elem, ItemKind.INFORMATION);
			}
		}
		else if (importObject instanceof IType) {
			IType clazz = (IType)importObject;

			createItemDefinition(definitions, imp, clazz);
			// TODO: automatically create an interface too?
			//createInterface(definitions, imp, clazz);
		}
		else if (importObject instanceof Definitions) {
			// what to do here?
		}
	}

	public static String makeURIRelative(URI baseURI, String s) {
		// convert platform URI to a relative URI string
		URI uri = URI.createURI(s);
		uri = uri.deresolve(baseURI, false, true, true);
		return uri.toString();
	}
	
	/**
	 * Remove the given Import object and delete all of its associated elements (i.e. ItemDefinition,
	 * Message, Operation and Interface) that were defined in the Import.
	 * 
	 * @param imp - the Import to remove
	 * @return true if the Import object was removed, false if not
	 */
	public static boolean removeImport(Import imp) {
		Definitions definitions = ModelUtil.getDefinitions(imp);
		boolean canRemove = true;
		boolean canRemoveNamespace = true;
		String location = imp.getLocation();
		String namespace = imp.getNamespace();
		for (Import i : definitions.getImports()) {
			if (i!=imp) {
				String loc1 = i.getLocation();
				String ns1 = i.getNamespace();
				// different import locations, same namespace?
				if (loc1!=null && location!=null && !loc1.equals(location) &&
						ns1!=null && namespace!=null && ns1.equals(namespace)) {
					// this namespace is still in use by another import!
					canRemoveNamespace = false;
					break;
				}
			}
		}
		
		if (canRemoveNamespace)
			NamespaceUtil.removeNamespace(imp.eResource(), namespace);
		
		if (canRemove) {
			String type = imp.getImportType();
			String loc = imp.getLocation();
			
			if (IMPORT_TYPE_WSDL.equals(type)) {
				List<Interface> list = ModelUtil.getAllRootElements(definitions, Interface.class);
				for (Interface intf : list) {
					Object ref = intf.getImplementationRef();
					if (ref instanceof EObject) {
						URI uri = EcoreUtil.getURI((EObject)ref);
						String uriString = uri.trimFragment().toString();
						if (uriString.equals(loc))
							deleteInterface(definitions, intf);
					}
				}
			}
			else if (IMPORT_TYPE_XML_SCHEMA.equals(type)) {
				List<ItemDefinition> list = ModelUtil.getAllRootElements(definitions, ItemDefinition.class);
				for (ItemDefinition itemDef : list) {
					Object ref = itemDef.getStructureRef();
					if (ref instanceof EObject) {
						URI uri = EcoreUtil.getURI((EObject)ref);
						String uriString = uri.trimFragment().toString();
						if (uriString.equals(loc))
							EcoreUtil.delete(itemDef);
					}
				}
			}
			else if (IMPORT_TYPE_JAVA.equals(type)) {
				List<Interface> list = ModelUtil.getAllRootElements(definitions, Interface.class);
				for (Interface intf : list) {
					Object ref = intf.getImplementationRef();
					if (ref instanceof EObject) {
						URI uri = EcoreUtil.getURI((EObject) ref);
						String uriString = uri.trimFragment().toString();
						if (uriString.equals(loc))
							deleteInterface(definitions, intf);
					}
				}

				// If the imported Java type did not create an interface, it may still have
				// created some ItemDefinitions which need to be deleted 
				String className = imp.getLocation();
				boolean deleted = false;
				String filename = definitions.eResource().getURI().trimFragment().toPlatformString(true);
				if (filename != null) {
					IJavaProject project = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()
							.findMember(filename).getProject());
					if (project != null) {
						try {
							IType clazz = project.findType(className);
							if (clazz != null) {
								deleteItemDefinition(definitions, imp, clazz);
								deleted = true;
							}
						}
						catch (JavaModelException e) {
						}
					}
				}
				if (!deleted)
					deleteItemDefinition(definitions, imp, className);
			}
			else if (IMPORT_TYPE_BPMN2.equals(type)) {
				// TODO: what objects need to be created? Interface maybe? 
			}
			definitions.getImports().remove(imp);
		}
		return canRemove;
	}

	/**
	 * Create a new Interface object. If an identical Interface already exists, a new one is not created.
	 * This also creates all of the Operations, Messages and ItemDefinitions that are defined in the
	 * "portType" element.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object; the new Interface is added to its rootElements 
	 * @param imp - the Import object where the WSDL Port Type is defined
	 * @param portType - the WSDL Port Type that corresponds to this Interface
	 * @return the newly created object, or an existing Interface with the same name and implementation reference
	 */
	public Interface createInterface(Definitions definitions, Import imp, PortType portType) {
		Interface intf = Bpmn2ModelerFactory.create(Interface.class);
		intf.setName(portType.getQName().getLocalPart());
		intf.setImplementationRef(portType);
		Interface i = findInterface(definitions,intf);
		if (i!=null)
			return i;
		
		definitions.getRootElements().add(intf);
		ModelUtil.setID(intf);
		createOperations(definitions, imp, intf, portType);
		
		return intf;
	}

    /**
     * Create a new Interface object. If an identical Interface already exists, a new one is not created.
     * This also creates all of the Operations and ItemDefinitions that are defined in the "type" element.
     * 
     * @param definitions - the BPMN2 Definitions parent object; the new Interface is added to its rootElements 
     * @param imp - the Import object where the WSDL Port Type is defined
     * @param type - the Java type that corresponds to this Interface
     * @return the newly created object, or an existing Interface with the same name and implementation reference
     */
    public Interface createInterface(Definitions definitions, Import imp, IType type, IMethod[] methods) {
        Interface intf = Bpmn2ModelerFactory.create(Interface.class);
        intf.setName(type.getElementName());
        intf.setImplementationRef(ModelUtil.createStringWrapper(type.getFullyQualifiedName('.')));
        Interface i = findInterface(definitions,intf);
        if (i!=null)
            return i;
        
        definitions.getRootElements().add(intf);
        ModelUtil.setID(intf);
        createOperations(definitions, imp, intf, type, methods);
        
        return intf;
    }

	/**
	 * Delete an existing Interface object. This also deletes all of the Operations, Messages and ItemDefinitions
	 * that are referenced by the Interface.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object that contains the Interface
	 * @param intf - the Interface object to remove
	 */
	public static void deleteInterface(Definitions definitions, Interface intf) {
		deleteOperations(definitions,intf);
		EcoreUtil.delete(intf);
	}
	
	/**
	 * Search for an existing Interface object that is identical to the one specified.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object
	 * @param intf - an Interface to search for
	 * @return the Interface if it already exists, null if not 
	 */
	public static Interface findInterface(Definitions definitions, Interface intf) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(intf);
		if (adapter!=null) {
			List <Interface> list = ModelUtil.getAllRootElements(definitions, Interface.class);
			for (Interface i : list) {
				if (adapter.getObjectDescriptor().similar(i))
					return i;
			}
		}
		return null;
	}

	/**
	 * Create a new Operation object and add it to the given Interface.
	 * This also creates all of the Messages, Errors and ItemDefinitions that are defined in the
	 * "portType" element.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the Interface is defined
	 * @param intf - the Interface to which this Operation will be added
	 * @param portType - the WSDL Port Type that corresponds to this Interface
	 */
	public void createOperations(Definitions definitions, Import imp, Interface intf, PortType portType) {
		for (Operation wsdlop : (List<Operation>)portType.getEOperations()) {
			org.eclipse.bpmn2.Operation bpmn2op = Bpmn2ModelerFactory.create(org.eclipse.bpmn2.Operation.class);
			bpmn2op.setImplementationRef(wsdlop);
			bpmn2op.setName(wsdlop.getName());
			
			Input input = wsdlop.getEInput();
			if (input!=null && input.getEMessage()!=null) {
				org.eclipse.bpmn2.Message bpmn2msg = createMessage(definitions, imp, input.getEMessage());
				bpmn2op.setInMessageRef(bpmn2msg);
			}
			
			Output output = wsdlop.getEOutput();
			if (output!=null && output.getEMessage()!=null) {
				org.eclipse.bpmn2.Message bpmn2msg = createMessage(definitions, imp, output.getEMessage());
				bpmn2op.setOutMessageRef(bpmn2msg);
			}
			
			for (Fault fault : (List<Fault>)wsdlop.getEFaults()) {
				bpmn2op.getErrorRefs().add(createError(definitions, imp, fault));
			}
			
			if (findOperation(definitions, bpmn2op)==null) {
				intf.getOperations().add(bpmn2op);
				ModelUtil.setID(bpmn2op);
			}
		}
	}

    /**
     * Create a new Operation object and add it to the given Interface. This
     * also creates all of the ItemDefinitions that are defined in the type.
     * 
     * @param definitions - the BPMN2 Definitions parent object
     * @param imp - the Import object where the Interface is defined
     * @param intf - the Interface to which this Operation will be added
     * @param type - the Java type that corresponds to this Interface
     */
    public void createOperations(Definitions definitions, Import imp, Interface intf, IType type, IMethod[] methods) {
        try {
        	if (methods==null)
        		methods = type.getMethods();
            for (IMethod method : methods) {
            	if (method.isConstructor()) {
            		// don't create Operations for Constructors
            		continue;
            	}
            	if (method.getElementName().contains("<")) { //$NON-NLS-1$
            		continue;
            	}
            	if ((method.getFlags() & Flags.AccPublic) == 0) {
            		continue;
            	}
				if (method.getNumberOfParameters()!=1) {
					continue;
				}
                org.eclipse.bpmn2.Operation bpmn2op = Bpmn2ModelerFactory.create(org.eclipse.bpmn2.Operation.class);
                bpmn2op.setImplementationRef(ModelUtil.createStringWrapper(method.getElementName()));
                bpmn2op.setName(method.getElementName());

                String[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length > 1) {
                    // only allow methods with one parameter at most.
                    continue;
                }
                String baseName = intf.getName() + "_" + bpmn2op.getName() + "_"; //$NON-NLS-1$ //$NON-NLS-2$
                if (parameterTypes.length == 1) {
                    try {
                        IType parameterType = resolveType(type, parameterTypes[0]);
                        org.eclipse.bpmn2.Message bpmn2msg = createMessage(definitions, imp, parameterType, baseName
                                + method.getParameterNames()[0]);
                        if (parameterType == null) {
                            String boxedType = getBoxedType(Signature.toString(parameterTypes[0]));
                            if (boxedType != null && boxedType.length() > 0) {
                                bpmn2msg.setItemRef(createItemDefinition(definitions, null, boxedType, ItemKind.INFORMATION));
                            }
                        }
                        bpmn2op.setInMessageRef(bpmn2msg);
                    } catch (JavaModelException e) {
                        Activator.logStatus(e.getStatus());
                        continue;
                    } catch (Exception e) {
                        Activator.logError(e);
                        continue;
                    }
                }

                try {
                    IType returnType = resolveType(type, method.getReturnType());
                    org.eclipse.bpmn2.Message bpmn2msg = null;
                    if (returnType != null) {
                    	bpmn2msg = createMessage(definitions, imp, returnType, baseName + "Result"); //$NON-NLS-1$
                    }
                    else {
                        String boxedType = getBoxedType(Signature.toString(method.getReturnType()));
                        if (boxedType != null && boxedType.length() > 0) {
                        	bpmn2msg = createMessage(definitions, imp, returnType, baseName + "Result"); //$NON-NLS-1$
                            bpmn2msg.setItemRef(createItemDefinition(definitions, null, boxedType, ItemKind.INFORMATION));
                        }
                    }
                    if (bpmn2msg!=null)
                    	bpmn2op.setOutMessageRef(bpmn2msg);
                } catch (JavaModelException e) {
                    Activator.logStatus(e.getStatus());
                } catch (Exception e) {
                    Activator.logError(e);
                }

                try {
                    for (String exceptionTypeString : method.getExceptionTypes()) {
                        try {
                            IType exceptionType = resolveType(type, exceptionTypeString);
                            bpmn2op.getErrorRefs().add(createError(definitions, imp, exceptionType));
                        } catch (JavaModelException e) {
                            Activator.logStatus(e.getStatus());
                        } catch (Exception e) {
                            Activator.logError(e);
                        }
                    }
                } catch (JavaModelException e) {
                    Activator.logStatus(e.getStatus());
                }

                if (findOperation(definitions, bpmn2op) == null) {
                    intf.getOperations().add(bpmn2op);
                    ModelUtil.setID(bpmn2op);
                }
            }
        } catch (JavaModelException e) {
            Activator.logStatus(e.getStatus());
        }
    }

    private static IType resolveType(IType type, String typeSignature) throws JavaModelException,
            IllegalArgumentException {
        String typeString = Signature.toString(typeSignature);
        String[][] resolvedType = type.resolveType(typeString);
        return resolvedType == null || resolvedType.length == 0 ? null : type.getJavaProject().findType(resolvedType[0][0], resolvedType[0][1]);
    }

    private static String getBoxedType(String primitiveType) {
        if ("boolean".equals(primitiveType)) { //$NON-NLS-1$
            return Boolean.class.getName();
        } else if ("byte".equals(primitiveType)) { //$NON-NLS-1$
            return Byte.class.getName();
        } else if ("short".equals(primitiveType)) { //$NON-NLS-1$
            return Short.class.getName();
        } else if ("int".equals(primitiveType)) { //$NON-NLS-1$
            return Integer.class.getName();
        } else if ("long".equals(primitiveType)) { //$NON-NLS-1$
            return Long.class.getName();
        } else if ("char".equals(primitiveType)) { //$NON-NLS-1$
            return Character.class.getName();
        } else if ("float".equals(primitiveType)) { //$NON-NLS-1$
            return Float.class.getName();
        } else if ("double".equals(primitiveType)) { //$NON-NLS-1$
            return Double.class.getName();
        }
        return null;
    }

	/**
	 * Remove all Operations from the given Interface.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param intf - the Interface from which Operations will be removed
	 */
	public static void deleteOperations(Definitions definitions, Interface intf) {
		List<org.eclipse.bpmn2.Operation> opList = new ArrayList<org.eclipse.bpmn2.Operation>();
		for (org.eclipse.bpmn2.Operation bpmn2op : intf.getOperations()) {
			opList.add(bpmn2op);
		}
		for (org.eclipse.bpmn2.Operation bpmn2op : opList) {
			org.eclipse.bpmn2.Message m;
			m = bpmn2op.getInMessageRef();
			if (m!=null) {
				deleteMessage(definitions,m);
			}
			m = bpmn2op.getOutMessageRef();
			if (m!=null) {
				deleteMessage(definitions,m);
			}
			
			List<org.eclipse.bpmn2.Error> errorList = new ArrayList<org.eclipse.bpmn2.Error>();
			errorList.addAll(bpmn2op.getErrorRefs());
			for (org.eclipse.bpmn2.Error e : errorList) {
				ItemDefinition itemDef = e.getStructureRef();
				if (itemDef!=null)
					EcoreUtil.delete(itemDef);
				EcoreUtil.delete(e);
			}
			EcoreUtil.delete(bpmn2op);
		}
	}
	
	/**
	 * Search for an existing Operation object that is identical to the one specified.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object
	 * @param bpmn2op - an Operation to search for
	 * @return the Operation if it already exists, null if not 
	 */
	public static org.eclipse.bpmn2.Operation findOperation(Definitions definitions, org.eclipse.bpmn2.Operation bpmn2op) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(bpmn2op);
		if (adapter!=null) {
			List<org.eclipse.bpmn2.Operation> list = ModelUtil.getAllRootElements(definitions, org.eclipse.bpmn2.Operation.class);
			for (org.eclipse.bpmn2.Operation o : list) {
				if (adapter.getObjectDescriptor().similar(o))
					return (org.eclipse.bpmn2.Operation)o;
			}
		}
		return null;
	}
	/**
	 * Create a new Message object and add it to the rootElements in the given Definitions.
	 * This also creates all of the ItemDefinitions that are defined in the "wsdlmsg" element.
	 * If a Message that matches the new one already exists, it is returned instead.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the WSDL Message is defined
	 * @param wsdlmsg - a WSDL Message object used to create the BPMN2 Message
	 * @return the newly created object, or an existing Message that is identical to the given WSDL Message
	 */
	public org.eclipse.bpmn2.Message createMessage(Definitions definitions, Import imp, Message wsdlmsg) {
		org.eclipse.bpmn2.Message bpmn2msg = Bpmn2ModelerFactory.create(org.eclipse.bpmn2.Message.class);
		ItemDefinition itemDef = createItemDefinition(definitions, imp, wsdlmsg);
		bpmn2msg.setItemRef(itemDef);
		bpmn2msg.setName(wsdlmsg.getQName().getLocalPart());
		
		org.eclipse.bpmn2.Message m = findMessage(definitions, bpmn2msg);
		if (m!=null)
			return m;
		
		definitions.getRootElements().add(bpmn2msg);
		ModelUtil.setID(bpmn2msg);
		
		return bpmn2msg;
	}

    /**
     * Create a new Message object and add it to the rootElements in the given Definitions.
     * This also creates all of the ItemDefinitions that are defined in the "wsdlmsg" element.
     * If a Message that matches the new one already exists, it is returned instead.
     * 
     * @param definitions - the BPMN2 Definitions parent object 
     * @param imp - the Import object where the WSDL Message is defined
     * @param param - Java type representing the message
     * @param paramName - the name of the parameter
     * @return the newly created object, or an existing Message that is identical to the given WSDL Message
     */
    public org.eclipse.bpmn2.Message createMessage(Definitions definitions, Import imp, IType param, String paramName) {
        org.eclipse.bpmn2.Message bpmn2msg = Bpmn2ModelerFactory.create(org.eclipse.bpmn2.Message.class);
        if (param != null) {
            ItemDefinition itemDef = createItemDefinition(definitions, imp, param);
            bpmn2msg.setItemRef(itemDef);
        }
        bpmn2msg.setName(paramName);
        
        org.eclipse.bpmn2.Message m = findMessage(definitions, bpmn2msg);
        if (m!=null)
            return m;
        
        definitions.getRootElements().add(bpmn2msg);
        ModelUtil.setID(bpmn2msg);
        
        return bpmn2msg;
    }

	/**
	 * Remove the given Message and its related ItemDefinitions.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object
	 * @param msg - the Message object to be removed
	 */
	public static void deleteMessage(Definitions definitions, org.eclipse.bpmn2.Message msg) {
		ItemDefinition itemDef = msg.getItemRef();
		if (itemDef!=null)
			EcoreUtil.delete(itemDef);
		EcoreUtil.delete(msg);
	}

	/**
	 * Search for an existing Message object that is identical to the one specified.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object
	 * @param msg - a Message to search for
	 * @return the Operation if it already exists, null if not 
	 */
	public static org.eclipse.bpmn2.Message findMessage(Definitions definitions, org.eclipse.bpmn2.Message msg) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(msg);
		if (adapter!=null) {
			List<org.eclipse.bpmn2.Message> list = ModelUtil.getAllRootElements(definitions, org.eclipse.bpmn2.Message.class);
			for (org.eclipse.bpmn2.Message m : list) {
				if (adapter.getObjectDescriptor().similar(m))
					return (org.eclipse.bpmn2.Message)m;
			}
		}
		return null;
	}
	
	/**
	 * Create a new Error object and add it to the rootElements in the given Definitions.
	 * This also creates all of the ItemDefinitions that are defined in the WSDL "fault" element.
	 * If an Error that matches the new one already exists, it is returned instead.
	 * WSDL Fault types always create "INFORMATION" ItemDefinitions.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the WSDL Fault is defined
	 * @param fault - a WSDL Fault object used to create the new BPMN2 Error
	 * @return the newly created object, or an existing Error that is identical to the given WSDL Fault
	 */
	public org.eclipse.bpmn2.Error createError(Definitions definitions, Import imp, Fault fault) {
		org.eclipse.bpmn2.Error error = Bpmn2ModelerFactory.create(org.eclipse.bpmn2.Error.class);
		ItemDefinition itemDef = createItemDefinition(definitions, imp, fault, ItemKind.INFORMATION);
		error.setName(fault.getName());
		error.setStructureRef(itemDef);
		org.eclipse.bpmn2.Error e = findError(definitions, error);
		if (e!=null)
			return e;

		definitions.getRootElements().add(error);
		ModelUtil.setID(error);
		
		return error;
	}
	
    /**
     * Create a new Error object and add it to the rootElements in the given Definitions.
     * This also creates all of the ItemDefinitions that are defined in the WSDL "fault" element.
     * If an Error that matches the new one already exists, it is returned instead.
     * WSDL Fault types always create "INFORMATION" ItemDefinitions.
     * 
     * @param definitions - the BPMN2 Definitions parent object 
     * @param imp - the Import object where the WSDL Fault is defined
     * @param exceptionType - Java type of exception thrown by operation
     * @return the newly created object, or an existing Error that is identical to the given WSDL Fault
     */
    public org.eclipse.bpmn2.Error createError(Definitions definitions, Import imp, IType exceptionType) {
        org.eclipse.bpmn2.Error error = Bpmn2ModelerFactory.create(org.eclipse.bpmn2.Error.class);
        ItemDefinition itemDef = createItemDefinition(definitions, imp, exceptionType);
        error.setName(exceptionType.getElementName());
        error.setStructureRef(itemDef);
        org.eclipse.bpmn2.Error e = findError(definitions, error);
        if (e!=null)
            return e;

        definitions.getRootElements().add(error);
        ModelUtil.setID(error);
        
        return error;
    }
    
	/**
	 * Search for an existing Error object that is identical to the one specified.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object
	 * @param error - an Error to search for
	 * @return the Error if it already exists, null if not 
	 */
	public static org.eclipse.bpmn2.Error findError(Definitions definitions, org.eclipse.bpmn2.Error error) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(error);
		if (adapter!=null) {
			List<org.eclipse.bpmn2.Error> list = ModelUtil.getAllRootElements(definitions, org.eclipse.bpmn2.Error.class);
			for (org.eclipse.bpmn2.Error e : list) {
				if (adapter.getObjectDescriptor().similar(e))
					return (org.eclipse.bpmn2.Error)e;
			}
		}
		return null;
	}
	
	/**
	 * Create a new ItemDefinition for the given WSDL Message. The WSDL Message becomes the target of the
	 * ItemDefinition's structureRef. WSDL Message types always create "INFORMATION" ItemDefinitions.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the WSDL Message is defined
	 * @param wsdlmsg - a WSDL Message object that defines the structure of the ItemDefinition
	 * @return the newly created object, or an existing ItemDefinition that is identical to the given WSDL Message
	 */
	public ItemDefinition createItemDefinition(Definitions definitions, Import imp, Message wsdlmsg) {
		return createItemDefinition(definitions, imp, wsdlmsg, ItemKind.INFORMATION);
	}

	/**
	 * Create a new ItemDefinition for the given Java type. This also creates ItemDefinitions for any
	 * internal Classes or Interfaces that are defined in the given Class. Java types always create
	 * "PHYSICAL" ItemDefinitions.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the Java type is defined
	 * @param clazz - the Java Class object that defines the structure of the ItemDefinition
	 * @return the newly created object, or an existing ItemDefinition that is identical to the given Java type
	 */
	public ItemDefinition createItemDefinition(Definitions definitions, Import imp, IType clazz) {
		try {
            for (IType c : clazz.getTypes()) {
                if (Flags.isPublic(c.getFlags())) {
                    createItemDefinition(definitions, imp, c);
                }
            }
        } catch (JavaModelException e) {
        }
		return createItemDefinition(definitions, imp, clazz.getFullyQualifiedName('.'), ItemKind.INFORMATION);
	}
	
	/**
	 * Create a new ItemDefinition for an arbitrary String type. The String is wrapped in a DynamicEObjectImpl
	 * (a.k.a. "String Wrapper") so that it can be handled as a proxy EObject
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the String type is defined
	 * @param structName - the type string that defines the structure of the ItemDefinition
	 * @param kind - the ItemKind, either PHYSICAL or INFORMATION
	 * @return the newly created object, or an existing ItemDefinition that is identical to the given String type
	 */
	public ItemDefinition createItemDefinition(Definitions definitions, Import imp, String structName, ItemKind kind) {
		EObject structureRef = ModelUtil.createStringWrapper(structName);
		return createItemDefinition(definitions, imp, structureRef, kind);
	}
	
	
	/**
	 * Create a new ItemDefinition for an arbitrary String type. The String is wrapped in a DynamicEObjectImpl
	 * (a.k.a. "String Wrapper") so that it can be handled as a proxy EObject
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the EObject is defined
	 * @param structureRef - the EObject that defines the structure of the ItemDefinition
	 * @param kind - the ItemKind, either PHYSICAL or INFORMATION
	 * @return the newly created object, or an existing ItemDefinition that is identical to the given String type
	 */
	public ItemDefinition createItemDefinition(Definitions definitions, Import imp, EObject structureRef, ItemKind kind) {
		ItemDefinition itemDef = Bpmn2ModelerFactory.create(ItemDefinition.class);
		itemDef.setImport(imp);
		if (kind==null) {
			// try to determine the ItemKind based on the type of Process:
			// if the Process is executable ItemKind is INFORMATION,
			// else PHYSICAL
			List<Process> processes = ModelUtil.getAllRootElements(definitions, Process.class);
			if (processes.size()>0) {
				if (processes.get(0).isIsExecutable())
					kind = ItemKind.INFORMATION;
				else
					kind = ItemKind.PHYSICAL;
			}
			else
				kind = ItemKind.INFORMATION;
		}
		itemDef.setItemKind(kind);
		itemDef.setStructureRef(structureRef);
		ItemDefinition i = findItemDefinition(definitions, itemDef);
		if (i!=null)
			return i;

		definitions.getRootElements().add(itemDef);
		ModelUtil.setID(itemDef);
		
		return itemDef;
	}
	
	/**
	 * Convenience method for findItemDefinition(Definitions,ItemDefinition)
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the EObject is defined
	 * @param structureRef - the EObject that defines the structure of the ItemDefinition
	 * @param kind - the ItemKind, either PHYSICAL or INFORMATION
	 * @return the ItemDefinition object if found, or null
	 */
	public static ItemDefinition findItemDefinition(Definitions definitions, Import imp, EObject structureRef, ItemKind kind) {
		ItemDefinition itemDef = Bpmn2ModelerFactory.create(ItemDefinition.class);
		itemDef.setImport(imp);
		itemDef.setItemKind(kind);
		itemDef.setStructureRef(structureRef);
		return findItemDefinition(definitions, itemDef);
	}
	
	/**
	 * Search for an existing ItemDefinition object that is identical to the one specified.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object
	 * @param itemDef - an ItemDefinition to search for
	 * @return the ItemDefinition if it already exists, null if not 
	 */
	public static ItemDefinition findItemDefinition(Definitions definitions, ItemDefinition itemDef) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(itemDef);
		if (adapter!=null) {
			List<ItemDefinition> list = ModelUtil.getAllRootElements(definitions, ItemDefinition.class);
			for (ItemDefinition i : list) {
				if (adapter.getObjectDescriptor().similar(i))
					return (ItemDefinition)i;
			}
		}
		return null;
	}
	
	public static ItemDefinition findItemDefinition(Definitions definitions, Import imp, IType clazz) {
		EObject structureRef = ModelUtil.createStringWrapper(clazz.getFullyQualifiedName('.'));
		return findItemDefinition(definitions, imp, structureRef, ItemKind.INFORMATION);
	}
	
	/**
	 * Remove an ItemDefinition for the given Java type. This also removes the ItemDefinitions for
	 * all inner classes and interfaces.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the Java type is defined
	 * @param clazz - the Java Class object that defines the structure of the ItemDefinition
	 */
	public static void deleteItemDefinition(Definitions definitions, Import imp, IType clazz) {
		try {
            for (IType c : clazz.getTypes()) {
            	deleteItemDefinition(definitions, imp, c);
            }
        } catch (JavaModelException e) {
        }
		EObject structureRef = ModelUtil.createStringWrapper(clazz.getFullyQualifiedName('.'));
		ItemDefinition itemDef = findItemDefinition(definitions, imp, structureRef, ItemKind.INFORMATION);
		if (itemDef!=null) {
			EcoreUtil.delete(itemDef);
		}
	}

	/**
	 * Remove an ItemDefinition for the given String type. This will search for both PHYSICAL and
	 * INFORMATION definitions.
	 * 
	 * @param definitions - the BPMN2 Definitions parent object 
	 * @param imp - the Import object where the String type is defined
	 * @param structName - the type string that defines the structure of the ItemDefinition
	 */
	public static void deleteItemDefinition(Definitions definitions, Import imp, String structName) {
		if (structName!=null && !structName.isEmpty()) {
			EObject structureRef = ModelUtil.createStringWrapper(structName);
			ItemDefinition itemDef = findItemDefinition(definitions, imp, structureRef, ItemKind.INFORMATION);
			if (itemDef==null)
				itemDef = findItemDefinition(definitions, imp, structureRef, ItemKind.INFORMATION);
			
			if (itemDef!=null) {
				EcoreUtil.delete(itemDef);
			}
		}
	}
}
