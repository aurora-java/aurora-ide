/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ext.org.eclipse.jdt.internal.corext.javadoc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.ui.PlatformUI;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;

import patch.org.eclipse.jdt.internal.ui.JavaPlugin;



import ext.org.eclipse.jdt.internal.corext.CorextMessages;
import ext.org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import ext.org.eclipse.jdt.internal.ui.JavaUIException;
import ext.org.eclipse.jdt.internal.ui.JavaUIStatus;
import ext.org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import ext.org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathSupport;
import ext.org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListElement;


public class JavaDocLocations {

	private static final String JAR_PROTOCOL= "jar"; //$NON-NLS-1$
	public static final String ARCHIVE_PREFIX= "jar:"; //$NON-NLS-1$
	private static final String PREF_JAVADOCLOCATIONS= "org.eclipse.jdt.ui.javadoclocations"; //$NON-NLS-1$
	public static final String PREF_JAVADOCLOCATIONS_MIGRATED= "org.eclipse.jdt.ui.javadoclocations.migrated"; //$NON-NLS-1$


	private static final String NODE_ROOT= "javadoclocation"; //$NON-NLS-1$
	private static final String NODE_ENTRY= "location_01"; //$NON-NLS-1$
	private static final String NODE_PATH= "path"; //$NON-NLS-1$
	private static final String NODE_URL= "url"; //$NON-NLS-1$

	private static final QualifiedName PROJECT_JAVADOC= new QualifiedName(JavaUI.ID_PLUGIN, "project_javadoc_location"); //$NON-NLS-1$

	public static void migrateToClasspathAttributes() {
		final Map<IPath, String> oldLocations= loadOldForCompatibility();
		if (oldLocations.isEmpty()) {
			IPreferenceStore preferenceStore= PreferenceConstants.getPreferenceStore();
			preferenceStore.setValue(PREF_JAVADOCLOCATIONS, ""); //$NON-NLS-1$
			preferenceStore.setValue(PREF_JAVADOCLOCATIONS_MIGRATED, true);
			return;
		}

		Job job= new Job(CorextMessages.JavaDocLocations_migratejob_name) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
						public void run(IProgressMonitor pm) throws CoreException {
							updateClasspathEntries(oldLocations, pm);
							IPreferenceStore preferenceStore= PreferenceConstants.getPreferenceStore();
							preferenceStore.setValue(PREF_JAVADOCLOCATIONS, ""); //$NON-NLS-1$
							preferenceStore.setValue(PREF_JAVADOCLOCATIONS_MIGRATED, true);
						}
					};
					new WorkbenchRunnableAdapter(runnable).run(monitor);
				} catch (InvocationTargetException e) {
					JavaPlugin.log(e);
				} catch (InterruptedException e) {
					// should not happen, cannot cancel
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	final static void updateClasspathEntries(Map<IPath, String> oldLocationMap, IProgressMonitor monitor) throws JavaModelException {
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IJavaProject[] javaProjects= JavaCore.create(root).getJavaProjects();
		try {
			monitor.beginTask(CorextMessages.JavaDocLocations_migrate_operation, javaProjects.length);
			for (int i= 0; i < javaProjects.length; i++) {
				IJavaProject project= javaProjects[i];
				String projectJavadoc= oldLocationMap.get(project.getPath());
				if (projectJavadoc != null) {
					try {
						setProjectJavadocLocation(project, projectJavadoc);
					} catch (CoreException e) {
						// ignore
					}
				}

				IClasspathEntry[] rawClasspath= project.getRawClasspath();
				boolean hasChange= false;
				for (int k= 0; k < rawClasspath.length; k++) {
					IClasspathEntry updated= getConvertedEntry(rawClasspath[k], project, oldLocationMap);
					if (updated != null) {
						rawClasspath[k]= updated;
						hasChange= true;
					}
				}
				if (hasChange) {
					project.setRawClasspath(rawClasspath, new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}
			}
		} finally {
			monitor.done();
		}
	}

	private static IClasspathEntry getConvertedEntry(IClasspathEntry entry, IJavaProject project, Map<IPath, String> oldLocationMap) {
		IPath path= null;
		switch (entry.getEntryKind()) {
			case IClasspathEntry.CPE_SOURCE:
			case IClasspathEntry.CPE_PROJECT:
				return null;
			case IClasspathEntry.CPE_CONTAINER:
				convertContainer(entry, project, oldLocationMap);
				return null;
			case IClasspathEntry.CPE_LIBRARY:
				path= entry.getPath();
				break;
			case IClasspathEntry.CPE_VARIABLE:
				path= JavaCore.getResolvedVariablePath(entry.getPath());
				break;
			default:
				return null;
		}
		if (path == null) {
			return null;
		}
		IClasspathAttribute[] extraAttributes= entry.getExtraAttributes();
		for (int i= 0; i < extraAttributes.length; i++) {
			if (IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME.equals(extraAttributes[i].getName())) {
				return null;
			}
		}
		String libraryJavadocLocation= oldLocationMap.get(path);
		if (libraryJavadocLocation != null) {
			CPListElement element= CPListElement.createFromExisting(entry, project);
			element.setAttribute(CPListElement.JAVADOC, libraryJavadocLocation);
			return element.getClasspathEntry();
		}
		return null;
	}

	private static void convertContainer(IClasspathEntry entry, IJavaProject project, Map<IPath, String> oldLocationMap) {
		try {
			IClasspathContainer container= JavaCore.getClasspathContainer(entry.getPath(), project);
			if (container == null) {
				return;
			}

			IClasspathEntry[] entries= container.getClasspathEntries();
			boolean hasChange= false;
			for (int i= 0; i < entries.length; i++) {
				IClasspathEntry curr= entries[i];
				IClasspathEntry updatedEntry= getConvertedEntry(curr, project, oldLocationMap);
				if (updatedEntry != null) {
					entries[i]= updatedEntry;
					hasChange= true;
				}
			}
			if (hasChange) {
				BuildPathSupport.requestContainerUpdate(project, container, entries);
			}
		} catch (CoreException e) {
			// ignore
		}
	}

	/**
	 * Sets the Javadoc location for an archive with the given path.
	 * @param project the Java project
	 * @param url the Javadoc location
	 */
	public static void setProjectJavadocLocation(IJavaProject project, URL url) {
		try {
			String location= url != null ? url.toExternalForm() : null;
			setProjectJavadocLocation(project, location);
		} catch (CoreException e) {
			JavaPlugin.log(e);
		}
	}

	private static void setProjectJavadocLocation(IJavaProject project, String url) throws CoreException {
		project.getProject().setPersistentProperty(PROJECT_JAVADOC, url);
	}

	public static URL getProjectJavadocLocation(IJavaProject project) {
		if (!project.getProject().isAccessible()) {
			return null;
		}
		try {
			String prop= project.getProject().getPersistentProperty(PROJECT_JAVADOC);
			if (prop == null) {
				return null;
			}
			return new URL(prop);
		} catch (CoreException e) {
			JavaPlugin.log(e);
		} catch (MalformedURLException e) {
			JavaPlugin.log(e);
		}
		return null;
	}


	public static URL getLibraryJavadocLocation(IClasspathEntry entry) {
		if (entry == null) {
			throw new IllegalArgumentException("Entry must not be null"); //$NON-NLS-1$
		}

		int kind= entry.getEntryKind();
		if (kind != IClasspathEntry.CPE_LIBRARY && kind != IClasspathEntry.CPE_VARIABLE) {
			throw new IllegalArgumentException("Entry must be of kind CPE_LIBRARY or CPE_VARIABLE"); //$NON-NLS-1$
		}

		IClasspathAttribute[] extraAttributes= entry.getExtraAttributes();
		for (int i= 0; i < extraAttributes.length; i++) {
			IClasspathAttribute attrib= extraAttributes[i];
			if (IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME.equals(attrib.getName())) {
				try {
					return new URL(attrib.getValue());
				} catch (MalformedURLException e) {
					return null;
				}
			}
		}
		return null;
	}

	public static URL getJavadocBaseLocation(IJavaElement element) throws JavaModelException {
		if (element.getElementType() == IJavaElement.JAVA_PROJECT) {
			return getProjectJavadocLocation((IJavaProject) element);
		}

		IPackageFragmentRoot root= JavaModelUtil.getPackageFragmentRoot(element);
		if (root == null) {
			return null;
		}

		if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
			IClasspathEntry entry= root.getResolvedClasspathEntry();
			URL javadocLocation= getLibraryJavadocLocation(entry);
			if (javadocLocation != null) {
				return getLibraryJavadocLocation(entry);
			}
			entry= root.getRawClasspathEntry();
			switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_LIBRARY:
				case IClasspathEntry.CPE_VARIABLE:
					return getLibraryJavadocLocation(entry);
				default:
					return null;
			}
		} else {
			return getProjectJavadocLocation(root.getJavaProject());
		}
	}

	// loading for compatibility

	private static JavaUIException createException(Throwable t, String message) {
		return new JavaUIException(JavaUIStatus.createError(IStatus.ERROR, message, t));
	}

	private static Map<IPath, String> loadOldForCompatibility() {
		HashMap<IPath, String> resultingOldLocations= new HashMap<IPath, String>();

		// in 3.0, the javadoc locations were stored as one big string in the preferences
		String string= PreferenceConstants.getPreferenceStore().getString(PREF_JAVADOCLOCATIONS);
		if (string != null && string.length() > 0) {
			byte[] bytes;
			try {
				bytes= string.getBytes("UTF-8"); //$NON-NLS-1$
			} catch (UnsupportedEncodingException e) {
				bytes= string.getBytes();
			}
			InputStream is= new ByteArrayInputStream(bytes);
			try {
				loadFromStream(new InputSource(is), resultingOldLocations);
				PreferenceConstants.getPreferenceStore().setValue(PREF_JAVADOCLOCATIONS, ""); //$NON-NLS-1$
				return resultingOldLocations;
			} catch (CoreException e) {
				JavaPlugin.log(e); // log but ignore
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		// in 2.1, the Javadoc locations were stored in a file in the meta data
		// note that it is wrong to use a stream reader with XML declaring to be UTF-8
		try {
			final String STORE_FILE= "javadoclocations.xml"; //$NON-NLS-1$
			File file= JavaPlugin.getDefault().getStateLocation().append(STORE_FILE).toFile();
			if (file.exists()) {
				Reader reader= null;
				try {
					reader= new FileReader(file);
					loadFromStream(new InputSource(reader), resultingOldLocations);
					file.delete(); // remove file after successful store
					return resultingOldLocations;
				} catch (IOException e) {
					JavaPlugin.log(e); // log but ignore
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
					} catch (IOException e) {}
				}
			}
		} catch (CoreException e) {
			JavaPlugin.log(e); // log but ignore
		}

		// in 2.0, the Javadoc locations were stored as one big string in the persistent properties
		// note that it is wrong to use a stream reader with XML declaring to be UTF-8
		try {
			final QualifiedName QUALIFIED_NAME= new QualifiedName(JavaUI.ID_PLUGIN, "jdoclocation"); //$NON-NLS-1$

			IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
			String xmlString= root.getPersistentProperty(QUALIFIED_NAME);
			if (xmlString != null) { // only set when workspace is old
				Reader reader= new StringReader(xmlString);
				try {
					loadFromStream(new InputSource(reader), resultingOldLocations);
					root.setPersistentProperty(QUALIFIED_NAME, null); // clear property
					return resultingOldLocations;
				} finally {

					try {
						reader.close();
					} catch (IOException e) {
						// error closing reader: ignore
					}
				}
			}
		} catch (CoreException e) {
			JavaPlugin.log(e); // log but ignore
		}
		return resultingOldLocations;
	}

	private static void loadFromStream(InputSource inputSource, Map<IPath, String> oldLocations) throws CoreException {
		Element cpElement;
		try {
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			parser.setErrorHandler(new DefaultHandler());
			cpElement = parser.parse(inputSource).getDocumentElement();
		} catch (SAXException e) {
			throw createException(e, CorextMessages.JavaDocLocations_error_readXML);
		} catch (ParserConfigurationException e) {
			throw createException(e, CorextMessages.JavaDocLocations_error_readXML);
		} catch (IOException e) {
			throw createException(e, CorextMessages.JavaDocLocations_error_readXML);
		}

		if (cpElement == null) return;
		if (!cpElement.getNodeName().equalsIgnoreCase(NODE_ROOT)) {
			return;
		}
		NodeList list= cpElement.getChildNodes();
		int length= list.getLength();
		for (int i= 0; i < length; ++i) {
			Node node= list.item(i);
			short type= node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element element= (Element) node;
				if (element.getNodeName().equalsIgnoreCase(NODE_ENTRY)) {
					String varPath = element.getAttribute(NODE_PATH);
					String varURL = element.getAttribute(NODE_URL);

					oldLocations.put(Path.fromPortableString(varPath), varURL);
				}
			}
		}
	}

	public static URL getJavadocLocation(IJavaElement element, boolean includeMemberReference) throws JavaModelException {
		URL baseLocation= getJavadocBaseLocation(element);
		if (baseLocation == null) {
			return null;
		}

		String urlString= baseLocation.toExternalForm();

		StringBuffer pathBuffer= new StringBuffer(urlString);
		if (!urlString.endsWith("/")) { //$NON-NLS-1$
			pathBuffer.append('/');
		}

		switch (element.getElementType()) {
			case IJavaElement.PACKAGE_FRAGMENT:
				appendPackageSummaryPath((IPackageFragment) element, pathBuffer);
				break;
			case IJavaElement.JAVA_PROJECT:
			case IJavaElement.PACKAGE_FRAGMENT_ROOT :
				appendIndexPath(pathBuffer);
				break;
			case IJavaElement.IMPORT_CONTAINER :
				element= element.getParent();
				//$FALL-THROUGH$
			case IJavaElement.COMPILATION_UNIT :
				IType mainType= ((ICompilationUnit) element).findPrimaryType();
				if (mainType == null) {
					return null;
				}
				appendTypePath(mainType, pathBuffer);
				break;
			case IJavaElement.CLASS_FILE :
				appendTypePath(((IClassFile) element).getType(), pathBuffer);
				break;
			case IJavaElement.TYPE :
				appendTypePath((IType) element, pathBuffer);
				break;
			case IJavaElement.FIELD :
				IField field= (IField) element;
				appendTypePath(field.getDeclaringType(), pathBuffer);
				if (includeMemberReference) {
					appendFieldReference(field, pathBuffer);
				}
				break;
			case IJavaElement.METHOD :
				IMethod method= (IMethod) element;
				appendTypePath(method.getDeclaringType(), pathBuffer);
				if (includeMemberReference) {
					appendMethodReference(method, pathBuffer);
				}
				break;
			case IJavaElement.INITIALIZER :
				appendTypePath(((IMember) element).getDeclaringType(), pathBuffer);
				break;
			case IJavaElement.IMPORT_DECLARATION :
				IImportDeclaration decl= (IImportDeclaration) element;

				if (decl.isOnDemand()) {
					IJavaElement cont= JavaModelUtil.findTypeContainer(element.getJavaProject(), Signature.getQualifier(decl.getElementName()));
					if (cont instanceof IType) {
						appendTypePath((IType) cont, pathBuffer);
					} else if (cont instanceof IPackageFragment) {
						appendPackageSummaryPath((IPackageFragment) cont, pathBuffer);
					}
				} else {
					IType imp= element.getJavaProject().findType(decl.getElementName());
					appendTypePath(imp, pathBuffer);
				}
				break;
			case IJavaElement.PACKAGE_DECLARATION :
				IJavaElement pack= element.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
				if (pack != null) {
					appendPackageSummaryPath((IPackageFragment) pack, pathBuffer);
				} else {
					return null;
				}
				break;
			default :
				return null;
		}

		try {
			return new URL(pathBuffer.toString());
		} catch (MalformedURLException e) {
			JavaPlugin.log(e);
		}
		return null;
	}

	private static void appendPackageSummaryPath(IPackageFragment pack, StringBuffer buf) {
		String packPath= pack.getElementName().replace('.', '/');
		buf.append(packPath);
		buf.append("/package-summary.html"); //$NON-NLS-1$
	}

	private static void appendIndexPath(StringBuffer buf) {
		buf.append("index.html"); //$NON-NLS-1$
	}

	private static void appendTypePath(IType type, StringBuffer buf) {
		IPackageFragment pack= type.getPackageFragment();
		String packPath= pack.getElementName().replace('.', '/');
		String typePath= type.getTypeQualifiedName('.');
		if (packPath.length() > 0) {
			buf.append(packPath);
			buf.append('/');
		}
		buf.append(typePath);
		buf.append(".html"); //$NON-NLS-1$
	}

	private static void appendFieldReference(IField field, StringBuffer buf) {
		buf.append('#');
		buf.append(field.getElementName());
	}

	private static void appendMethodReference(IMethod meth, StringBuffer buf) throws JavaModelException {
		buf.append('#');
		buf.append(meth.getElementName());

		buf.append('(');
		String[] params= meth.getParameterTypes();
		IType declaringType= meth.getDeclaringType();
		boolean isVararg= Flags.isVarargs(meth.getFlags());
		int lastParam= params.length - 1;
		for (int i= 0; i <= lastParam; i++) {
			if (i != 0) {
				buf.append(", "); //$NON-NLS-1$
			}
			String curr= Signature.getTypeErasure(params[i]);
			String fullName= JavaModelUtil.getResolvedTypeName(curr, declaringType);
			if (fullName != null) {
				buf.append(fullName);
				int dim= Signature.getArrayCount(curr);
				if (i == lastParam && isVararg) {
					dim--;
				}
				while (dim > 0) {
					buf.append("[]"); //$NON-NLS-1$
					dim--;
				}
				if (i == lastParam && isVararg) {
					buf.append("..."); //$NON-NLS-1$
				}
			}
		}
		buf.append(')');
	}

	public static String getBaseURL(IMember member) throws JavaModelException {
		if (member.isBinary()) {
			// Source attachment usually does not include Javadoc resources
			// => Always use the Javadoc location as base:
			URL baseURL= JavaUI.getJavadocLocation(member, false);
			if (baseURL != null) {
				if (baseURL.getProtocol().equals(JAR_PROTOCOL)) {
					// It's a JarURLConnection, which is not known to the browser widget.
					// Let's start the help web server:
					URL baseURL2= PlatformUI.getWorkbench().getHelpSystem().resolve(baseURL.toExternalForm(), true);
					if (baseURL2 != null) { // can be null if org.eclipse.help.ui is not available
						baseURL= baseURL2;
					}
				}
				return baseURL.toExternalForm();
			}
		} else {
			IResource resource= member.getResource();
			if (resource != null) {
				/*
				 * Too bad: Browser widget knows nothing about EFS and custom URL handlers,
				 * so IResource#getLocationURI() does not work in all cases.
				 * We only support the local file system for now.
				 * A solution could be https://bugs.eclipse.org/bugs/show_bug.cgi?id=149022 .
				 */
				IPath location= resource.getLocation();
				if (location != null)
					return location.toFile().toURI().toASCIIString();
			}
		}
		return null;
	}


}
