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
package org.eclipse.bpmn2.modeler.core.builder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.runtime.XMLConfigElement;
import org.eclipse.bpmn2.modeler.core.utils.ErrorDialog;
import org.eclipse.bpmn2.modeler.core.validation.BPMN2ProjectValidator;
import org.eclipse.bpmn2.presentation.Bpmn2EditorPlugin;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class BPMN2Builder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "org.eclipse.bpmn2.modeler.core.bpmn2Builder"; //$NON-NLS-1$
	private static final String MARKER_TYPE = "org.eclipse.bpmn2.modeler.core.xmlProblem"; //$NON-NLS-1$
	public static final String CONFIG_FOLDER = ".bpmn2config"; //$NON-NLS-1$
	
	private SAXParserFactory parserFactory;
	private Hashtable<IFolder, Long> timestamps = new Hashtable<IFolder, Long>();

	public static final BPMN2Builder INSTANCE = new BPMN2Builder();

	class BPMN2DeltaVisitor implements IResourceDeltaVisitor {
		IProgressMonitor monitor;

		public BPMN2DeltaVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
		 * .core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if (resource instanceof IFile) {
				IContainer container = resource.getParent();
				if (CONFIG_FOLDER.equals(container.getName()) && container.getParent() instanceof IProject) {
					int kind = delta.getKind();
					if (kind==IResourceDelta.REMOVED) {
						unloadExtension((IFile) resource);
					}
					else {
						loadExtension((IFile) resource);
					}
					return true;
				}
			}
			
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				// checkXML(resource);
				validate(delta, monitor);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				// checkXML(resource);
				validate(delta, monitor);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class BPMN2ResourceVisitor implements IResourceVisitor {
		IProgressMonitor monitor;

		public BPMN2ResourceVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		public boolean visit(IResource resource) {
			// checkXML(resource);
			validate(resource, monitor);
			// return true to continue visiting children.
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new BPMN2DeltaVisitor(monitor));
	}

	void validate(IResourceDelta delta, IProgressMonitor monitor) {
		BPMN2ProjectValidator.validate(delta, monitor);
	}

	void validate(IResource resource, IProgressMonitor monitor) {
		BPMN2ProjectValidator.validate(resource, monitor);
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		try {
			getProject().accept(new BPMN2ResourceVisitor(monitor));
		} catch (CoreException e) {
		}
	}

	/**
	 * Load all extension definition files contained in the given IProject's CONFIG_FOLDER folder.
	 * The timestamp of this folder is cached so that it only gets loaded once.
	 * Individual extension definition files are loaded by the builder if/when they are modified.
	 * 
	 * @param project
	 */
	public void loadExtensions(IProject project) {
		try {
			IFolder folder = project.getFolder(CONFIG_FOLDER);
			if (folder.exists()) {
				Long timestamp = timestamps.get(folder);
				if (timestamp==null || timestamp.longValue() < folder.getLocalTimeStamp()) {
					timestamps.put(folder, new Long(folder.getLocalTimeStamp()));
					for (IResource r : folder.members()) {
						if (r instanceof IFile && r.exists()) {
							loadExtension((IFile) r);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadExtension(IFile file) {
		XMLConfigElementHandler handler = new XMLConfigElementHandler(file);
		try {
			TargetRuntime rt = Bpmn2Preferences.getInstance(file.getProject()).getRuntime();
			if (file.exists() && file.getLocalTimeStamp() > rt.getConfigFileTimestamp()) {
				SAXParser parser = getParser();
				FileInputStream fis = new FileInputStream(file.getLocation().makeAbsolute().toOSString());
				parser.parse(fis, handler);
				IConfigurationElement element = handler.root.getChildren()[0];
				TargetRuntime.loadExtensions(rt, element.getChildren(), file);
			}
		} catch (Exception e) {
			ErrorDialog dlg = new ErrorDialog(Messages.BPMN2Builder_ConfigFileError_Title, e);
			dlg.show();
		}
	}

	public void unloadExtension(IFile file) {
		TargetRuntime.unloadExtensions(file);
	}
	
	private class XMLConfigElementHandler extends XMLErrorHandler {
		public XMLConfigElement root;
		private Stack<XMLConfigElement> stack = new Stack<XMLConfigElement>();

		public XMLConfigElementHandler(IFile file) {
			super(file);
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			String value = new String(ch, start, length).trim();
			if (!value.isEmpty()) {
				stack.peek().setValue(value);
			}
		}

		@Override
		public void endDocument() throws SAXException {
			stack.pop();
		}

		@Override
		public void endElement(String arg0, String arg1, String arg2) throws SAXException {
			stack.pop();
		}

		@Override
		public void startDocument() throws SAXException {
			root = new XMLConfigElement(file.getProject());
			stack.push(root);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			XMLConfigElement element = new XMLConfigElement(stack.peek(), qName);
			for (int i = 0; i < attributes.getLength(); ++i) {
				element.setAttribute(attributes.getQName(i), attributes.getValue(i));
			}
			stack.push(element);
		}
	}

	private class XMLErrorHandler extends DefaultHandler {

		protected IFile file;

		public XMLErrorHandler(IFile file) {
			this.file = file;
		}

		private void addMarker(SAXParseException e, int severity) {
			BPMN2Builder.this.addMarker(file, e.getMessage(), e.getLineNumber(), severity);
		}

		public void error(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void warning(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}

	/**
	 * @param file
	 * @param message
	 * @param lineNumber
	 * @param severity
	 */
	private void addMarker(IFile file, String message, int lineNumber, int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	/**
	 * @param resource
	 */
	void checkXML(IResource resource) {
		if (BPMN2ProjectValidator.isBPMN2File(resource)) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			XMLErrorHandler reporter = new XMLErrorHandler(file);
			try {
				getParser().parse(file.getContents(), reporter);
			} catch (Exception e1) {
			}
		}
	}

	/**
	 * @param file
	 */
	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	/**
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	private SAXParser getParser() throws ParserConfigurationException, SAXException {
		if (parserFactory == null) {
			parserFactory = SAXParserFactory.newInstance();
		}
		return parserFactory.newSAXParser();
	}
}
