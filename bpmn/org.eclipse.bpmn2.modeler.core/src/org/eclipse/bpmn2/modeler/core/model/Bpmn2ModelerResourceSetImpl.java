/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Oracle Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.ErrorDialog;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.wsdl.util.WSDLResourceFactoryRegistry;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;


/**
 * @author Michal Chmielewski (michal.chmielewski@oracle.com)
 * @date Apr 17, 2007
 *
 */


@SuppressWarnings("nls")

public class Bpmn2ModelerResourceSetImpl extends ResourceSetImpl implements IResourceChangeListener {
	// this ID identifies the BPMN file content type
	public static final String BPMN2_CONTENT_TYPE = "org.eclipse.bpmn2.content-type.xml"; //$NON-NLS-1$
	public static final String OPTION_PROGRESS_MONITOR = "PROGRESS_MONITOR"; //$NON-NLS-1$
	
	private String connectionTimeout;
	private String readTimeout;

	public Bpmn2ModelerResourceSetImpl() {
		super();
        getLoadOptions().put(XMLResource.OPTION_DISABLE_NOTIFY, true);
        getLoadOptions().put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, true);
	}

	/**
	 * Used to force loading using the right resource loaders.
	 */
	static public final String SLIGHTLY_HACKED_KEY = "slightly.hacked.resource.set"; //$NON-NLS-1$
	
	/* (non-Javadoc)
	 * 
	 * Intercept getEObject() calls and validate the URI.
	 * This allows us to use arbitrary proxy URIs for things like ItemDefinition.structureRef
	 * 
	 * Also handle the setting of connection timeout here for URIs that do not point to physical
	 * resources, or resources that are currently unavailable due to (e.g. possibly) server outages.
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#getEObject(org.eclipse.emf.common.util.URI, boolean)
	 */
	@Override
	public EObject getEObject(URI uri, boolean loadOnDemand) {
		EObject o = null;
		if (uri!=null) {
			// bug fix for EditUIMarkerHelper#getTargetObjects() which decodes the URI,
			// and causes all kinds of problems with demand resource load processing.
			URI newUri;
			String uriString = uri.toString();
			if (uriString==null)
				newUri = uri;
			else
				newUri = URI.createURI(uriString, true);
			if (newUri.fragment()!=null) {
				try {
					setDefaultTimeoutProperties();
					o = super.getEObject(newUri, loadOnDemand);
				}
				catch (Exception e) {
					// if the resource does not contain anything, it was probably not found
					// and has already been reported earlier so don't bother with error log here.
//				    Resource resource = getResource(uri.trimFragment(), loadOnDemand);
//				    if (resource!=null && resource.getContents().size()>0)
//				    	Activator.logError(e);
				}
				finally {
					restoreTimeoutProperties();
				}
			}
			else {
				newUri = newUri.appendFragment(uri.lastSegment());
				o = super.getEObject(newUri, loadOnDemand);
			}
		}
		return o;
	}

	private void saveTimeoutProperties() {
		if (connectionTimeout==null) {
			connectionTimeout = System.getProperty("sun.net.client.defaultConnectTimeout"); //$NON-NLS-1$
			if (connectionTimeout==null)
				connectionTimeout = ""; //$NON-NLS-1$
		}
		if (readTimeout==null) {
			readTimeout = System.getProperty("sun.net.client.defaultReadTimeout"); //$NON-NLS-1$
			if (readTimeout==null)
				readTimeout = ""; //$NON-NLS-1$
		}
	}
	
	private void restoreTimeoutProperties() {
		if(connectionTimeout!=null) {
			System.setProperty("sun.net.client.defaultConnectTimeout", connectionTimeout); //$NON-NLS-1$
			connectionTimeout = null;
		}
		if (readTimeout!=null) {
			System.setProperty("sun.net.client.defaultReadTimeout", readTimeout); //$NON-NLS-1$
			readTimeout = null;
		}
	}
	
	private void setDefaultTimeoutProperties() {
		saveTimeoutProperties();
		Resource resource = getResources().isEmpty() ? null : getResources().get(0);
		Integer timeout = Bpmn2Preferences.getInstance(resource).getConnectionTimeout();
		System.setProperty("sun.net.client.defaultConnectTimeout", timeout.toString()); //$NON-NLS-1$
		System.setProperty("sun.net.client.defaultReadTimeout", timeout.toString()); //$NON-NLS-1$
	}

	/**
	 * Load the resource from the resource set, assuming that it is the kind
	 * indicated by the last argument. The "kind" parameter is the extension 
	 * without the . of the resource.
	 * 
	 * This forces the right resource to be loaded even if the URI of the resource
	 * is "wrong".
	 * 
	 * @param uri the URI of the resource.
	 * @param loadOnDemand load on demand
	 * @param kind the resource kind. It has to be of the form "*.wsdl", or "*.xsd", or "*.bpmn"
	 * @return the loaded resource. 
	 */
	
	@SuppressWarnings("nls")
	public Resource getResource(URI uri, boolean loadOnDemand, String kind)  {

		// Bugzilla 324164
		// don't bother if URI is null or empty
		if (uri==null || uri.isEmpty())
			return null;
		Map<URI, Resource> map = getURIResourceMap();
		
		if (map != null) {
			Resource resource = map.get(uri);
			if (resource != null) {
				if (loadOnDemand && !resource.isLoaded()) {
					// Bugzilla 324164
					// if load fails, mark resource as unloaded
					try {
						demandLoadHelper(resource);
					} catch (Exception ex) {
						resource.unload();
					}
				}
				
				return resource;
			}
		}

//		System.out.println("Loading Resource "+uri);
		URIConverter theURIConverter = getURIConverter();
		URI normalizedURI = theURIConverter.normalize(uri);
//		System.out.println("     Normalized="+normalizedURI);
		
		for (Resource resource : getResources()) {
			if (theURIConverter.normalize(resource.getURI()).equals(
					normalizedURI)) {
				if (loadOnDemand && !resource.isLoaded()) {
					// Bugzilla 324164
					// if load fails, mark resource as unloaded
					try {
						demandLoadHelper(resource);
					} catch (Exception ex) {
						resource.unload();
						break;
					}
				}

				if (map != null) {
					map.put(uri, resource);
				}
				return resource;
			}			
		}
		
		if (loadOnDemand) {
			Resource resource = demandCreateResource(uri,kind);
			if (resource == null) {
				throw new RuntimeException("Cannot create a resource for '" //$NON-NLS-1$
						+ uri + "'; a registered resource factory is needed"); //$NON-NLS-1$
			}

			demandLoadHelper(resource);

			if (map != null) {
				map.put(uri, resource);
			}
			return resource;
		}

		return null;
	}
	
	public Resource getResource(URI uri, boolean loadOnDemand) {
		Resource resource = super.getResource(uri, loadOnDemand);
		int index = resources.indexOf(resource);
		if (index>2) {
			// the first two Resources loaded are the Graphiti XMI resource and
			// our BPMN2 model file. All others are probably loaded as a result
			// of imports or references to external Resources. We want to track
			// changes to those and also make sure they don't get saved by Graphiti's
			// EmfService class.
			if (!resource.isTrackingModification())
				resource.setTrackingModification(true);
		}
		return resource;
	}
	
	protected Resource demandCreateResource ( URI uri, String kind ) {
		return createResource ( uri, kind );
	}
	
	/*
	 * Fix for Bug 278205 - Problem with importing remote WSIL/WSDL still exists.
	 * Telesh Alexandr added this method to solve this which is to load remote WSIL/WSDL 
     * by the correct resource loader based on known before loading resouce 
     * extension which is posed as resource content type.
	 */
	public Resource.Factory.Registry getResourceFactoryRegistry() {
		if (resourceFactoryRegistry == null) {

			resourceFactoryRegistry = new ResourceFactoryRegistryImpl() {
				@Override
				protected Resource.Factory delegatedGetFactory(URI uri, String contentTypeIdentifier) {
					// TODO: move the code that handles the WSIL factory to the org.eclipse.bpmn2.modeler.wsil plugin
					// patch for "wsil" and "wsdl" resources without extensions
					final Map<String, Object> extensionToFactoryMap =
						Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
					
					Object wsilFactory = extensionToFactoryMap.get("wsil"); //$NON-NLS-1$
					Object wsdlFactory = extensionToFactoryMap.get("wsdl"); //$NON-NLS-1$
					Object xsdFactory = extensionToFactoryMap.get("xsd"); //$NON-NLS-1$
					Object bpmnFactory = extensionToFactoryMap.get("bpmn"); //$NON-NLS-1$
					
					final Map<String, Object> contentTypeToFactoryMap = 
						Resource.Factory.Registry.INSTANCE.getContentTypeToFactoryMap();
					
					if (null != wsilFactory) {
						contentTypeToFactoryMap.put("wsil", wsilFactory); //$NON-NLS-1$
					}
					
					if (wsdlFactory==null) {
						wsdlFactory = WSDLResourceFactoryRegistry.INSTANCE.getExtensionToFactoryMap().get("wsdl");  //$NON-NLS-1$
					}
					contentTypeToFactoryMap.put("wsdl", wsdlFactory); //$NON-NLS-1$
					
					if (xsdFactory==null) {
						xsdFactory = new XSDResourceFactoryImpl();
					}
					contentTypeToFactoryMap.put("xsd", xsdFactory); //$NON-NLS-1$
					
					if (bpmnFactory==null) {
						bpmnFactory = new Bpmn2ResourceFactoryImpl();;
					}
					contentTypeToFactoryMap.put("bpmn", bpmnFactory); //$NON-NLS-1$
					contentTypeToFactoryMap.put("bpmn2", bpmnFactory); //$NON-NLS-1$

					return convert(getFactory(uri,
							Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap(),
							extensionToFactoryMap, contentTypeToFactoryMap,
							contentTypeIdentifier, false));
				}

				@Override
				protected URIConverter getURIConverter() {
					// return ResourceSetImpl.this.getURIConverter();
					return Bpmn2ModelerResourceSetImpl.this.getURIConverter();
				}

				@Override
				protected Map<?, ?> getContentDescriptionOptions() {
					return getLoadOptions();
				}
				
				@Override
				public Resource.Factory getFactory(URI uri, String contentType) {
					Resource.Factory factory = convert(getFactory(uri, protocolToFactoryMap,
							extensionToFactoryMap,
							contentTypeIdentifierToFactoryMap, contentType,
							true));
					if (factory instanceof Bpmn2ModelerResourceFactoryImpl && uri.isPlatformResource()) {
						try {
							// check if an extension model has been defined by the target runtime.
							String name = uri.segment(1);
							IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
							Bpmn2Preferences pref = Bpmn2Preferences.getInstance(project);
							TargetRuntime rt = pref.getRuntime();
							factory = rt.getModelDescriptor().getResourceFactory();
						}
						catch (Exception e) {
						}
					}
					return factory;
				}

			};
		}
		return resourceFactoryRegistry;
	}

	protected void demandLoadHelper(final Resource resource) {
		try {
			setDefaultTimeoutProperties();

			// If there is a Progress Monitor in the ResourceSet load options
			// use it instead of creating our own - this happens if we are being
			// called from the background Project Validation builder thread.
			Map<Object,Object> options = resource.getResourceSet().getLoadOptions();
			Object o = options.get(Bpmn2ModelerResourceSetImpl.OPTION_PROGRESS_MONITOR);
			if (o instanceof IProgressMonitor) {
				IProgressMonitor monitor = (IProgressMonitor)o;
				doLoad(resource, monitor);
			}
			else {
				// This causes a deadlock waiting on the EditingDomain's CommandStack.
				// Use a Null Progress Monitor instead - it won't show progress, but
				// it's better than a deadlock.
				// TODO: figure out how to show a ProgressMonitorDialog without causing a deadlock
//				Display.getDefault().asyncExec(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							IProgressService ps = PlatformUI.getWorkbench().getProgressService();
//							ps.busyCursorWhile(
//								new IRunnableWithProgress() {
//
//									@Override
//									public void run(IProgressMonitor monitor) throws InvocationTargetException,
//											InterruptedException {
										doLoad(resource, new NullProgressMonitor());
//									}
//								}
//							);
//						}
//						catch (Exception e) {
//						}
//					}
//				});
			}
		}
		finally {
			restoreTimeoutProperties();
		}
	}

	private void doLoad(final Resource resource, IProgressMonitor monitor) {
		try {
			String taskName = NLS.bind(Messages.Bpmn2ModelerResourceSetImpl_Loading_Title, resource.getURI());
			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
			resource.eSetDeliver(false);
			Bpmn2ModelerResourceSetImpl.super.demandLoadHelper(resource);
			if (!resource.isLoaded()) {
				throw new Exception(Messages.Bpmn2ModelerResourceSetImpl_Loading_Resource_Not_Found);
			}
		}
		catch (final Exception e) {
			Activator.logError(e);
			String msg = NLS.bind(Messages.Bpmn2ModelerResourceSetImpl_Loading_Error_Message,resource.getURI());
			ErrorDialog dlg = new ErrorDialog(Messages.Bpmn2ModelerResourceSetImpl_Loading_Error, msg, e);
			dlg.show();
		}
		finally {
			monitor.done();
		}
	}
	
	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged (IResourceChangeEvent event) {
		
		// System.out.println("IResourceChangeEvent: " + event + "; " + event.getType()  );				
		IResourceDelta[] deltas = event.getDelta().getAffectedChildren( IResourceDelta.CHANGED | IResourceDelta.REMOVED, IResource.FILE );	
		processDeltas ( deltas );
	}
	
	void processDeltas ( IResourceDelta [] deltas ) {
		
		for(IResourceDelta delta : deltas) {			
			processDeltas( delta.getAffectedChildren(IResourceDelta.CHANGED | IResourceDelta.REMOVED, IResource.FILE) );
			
			IResource resource = delta.getResource();
			if (resource.getType () != IResource.FILE) {
				continue;
			}
			
			if (delta.getKind() == IResourceDelta.REMOVED){
				resourceChanged((IFile)resource);
				continue;
			}
			
//			 * @see IResourceDelta#CONTENT
//			 * @see IResourceDelta#DESCRIPTION
//			 * @see IResourceDelta#ENCODING
//			 * @see IResourceDelta#OPEN
//			 * @see IResourceDelta#MOVED_TO
//			 * @see IResourceDelta#MOVED_FROM
//			 * @see IResourceDelta#TYPE
//			 * @see IResourceDelta#SYNC
//			 * @see IResourceDelta#MARKERS
//			 * @see IResourceDelta#REPLACED
			 
			if ((delta.getFlags() & IResourceDelta.CONTENT) == 0){
				continue;
			}
			
			// TODO: Temporary hack
			// Actually we should remove all resources from the resourceSet,
			// but for some reasons bpmn files can't be removed now
			// Bugzilla 320545:
			if (isBPMN2File(resource)){
				continue;
			}
			
			resourceChanged((IFile) resource);
		}		
	}
	
	public void setLoadOptions (Map<Object, Object> options) {
		loadOptions = options;
	}
	
	/**
	 * Resource has changed, remove it from the cache or list of loaded resources.
	 * 
	 * @param file
	 */
	public void resourceChanged (IFile file) {
		// System.out.println("ResourceChanged: " + file  );
		URI uri = URI.createPlatformResourceURI( file.getFullPath().toString() ) ;		
		// System.out.println("    ResourceURI: " + uri );		
		URIConverter theURIConverter = getURIConverter();
		URI normalizedURI = theURIConverter.normalize(uri);
				
		if (uriResourceMap != null) {
			uriResourceMap.remove(uri);
			uriResourceMap.remove(normalizedURI);
			// System.out.println("Removed from Map: " + map );
		}
				
		List<Resource> resourceList = getResources();
		if (resources.size() < 1) {
			return ;
		}
		
		for(Resource r : new ArrayList<Resource>(resourceList) )  {
			if (uri.equals(r.getURI()) || normalizedURI.equals(r.getURI() )) {
				resources.remove(r);
				// System.out.println("Removed from List: " + r );				
			}
		}
	}

	// Bugzilla 320545:
	public static boolean isBPMN2File(IResource res)
	{
		try
		{
			if (res.getType() == IResource.FILE) {
				IContentDescription desc = ((IFile) res).getContentDescription();
				if (desc != null) {
					IContentType type = desc.getContentType();
					if (type.getId().equals(BPMN2_CONTENT_TYPE))
						return true;
				}
			}
		}
		catch(Exception ex)
		{
		}
		return false;	
	}
}
