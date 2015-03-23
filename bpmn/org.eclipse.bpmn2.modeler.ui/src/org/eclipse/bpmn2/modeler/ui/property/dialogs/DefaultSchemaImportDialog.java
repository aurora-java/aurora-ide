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

package org.eclipse.bpmn2.modeler.ui.property.dialogs;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerResourceSetImpl;
import org.eclipse.bpmn2.modeler.core.utils.JavaProjectClassLoader;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.Messages;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.bpmn2.modeler.ui.property.providers.BPMN2DefinitionsTreeContentProvider;
import org.eclipse.bpmn2.modeler.ui.property.providers.JavaTreeContentProvider;
import org.eclipse.bpmn2.modeler.ui.property.providers.ModelTreeLabelProvider;
import org.eclipse.bpmn2.modeler.ui.property.providers.ServiceTreeContentProvider;
import org.eclipse.bpmn2.modeler.ui.property.providers.TreeNode;
import org.eclipse.bpmn2.modeler.ui.property.providers.VariableTypeTreeContentProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDSchema;

/**
 * Browse for complex/simple types available in the process and choose that
 * simple type.
 * 
 */

public class DefaultSchemaImportDialog extends SelectionStatusDialog {
	
	// Button id for browsing the workspace
	protected final static int BID_BROWSE_WORKSPACE = IDialogConstants.CLIENT_ID + 1;
	// Button id for browsing URLs
	protected final static int BID_BROWSE_URL = IDialogConstants.CLIENT_ID + 2;
	// Button id for browse files
	protected final static int BID_BROWSE_FILE = IDialogConstants.CLIENT_ID + 3;
	// Browse button id
	protected static final int BID_BROWSE = IDialogConstants.CLIENT_ID + 4;
	// Button id for import XML file types
	protected static final int BID_IMPORT_XML = IDialogConstants.CLIENT_ID + 6;
	// Button id for import XSD file types
	protected static final int BID_IMPORT_XSD = IDialogConstants.CLIENT_ID + 7;
	// Button id for import WSDL file types
	protected static final int BID_IMPORT_WSDL = IDialogConstants.CLIENT_ID + 8;
	// Button id for import BPMN 2.0 file types
	protected static final int BID_IMPORT_BPMN2 = IDialogConstants.CLIENT_ID + 9;
	
	///////////////////////////////////////////////////////////////////////////////
	// TODO: we may want to use JavaUI.createTypeDialog(...) instead of cluttering
	// up this dialog with java types here...
	///////////////////////////////////////////////////////////////////////////////
	// Button id for import Java types
	protected static final int BID_IMPORT_JAVA = IDialogConstants.CLIENT_ID + 10;
	
	// the current import type
	protected int fImportType = BID_IMPORT_XSD;
	// the current import source
	protected int fImportSource = BID_BROWSE_WORKSPACE;
	// the import type setting, remembered in the dialog settings
	protected static final String IMPORT_TYPE = "ImportType"; //$NON-NLS-1$
	// the import source setting, remembered in the dialog settings
	protected static final String IMPORT_SOURCE = "ImportSource"; //$NON-NLS-1$

	protected static final String EMPTY = ""; //$NON-NLS-1$

	protected String[] FILTER_EXTENSIONS;
	protected String[] FILTER_NAMES;
	protected String resourceFilter;
	protected String fResourceKind;

	protected BPMN2Editor bpmn2Editor;
	protected EObject modelObject;
	protected int allowedResourceTypes;

	protected Tree fTree;
	protected TreeViewer fTreeViewer;

	protected Text fLocation;
	protected String fLocationText;
	protected Label fLocationLabel;
	protected Label fStructureLabel;

	protected Composite fLocationComposite;
	protected FileSelectionGroup fResourceComposite;

	protected Text filterText;
	protected String fFilter = ""; //$NON-NLS-1$

	protected Button fBrowseButton;

	protected Group fTypeGroup;

	protected Group fKindGroup;
	protected Composite fKindButtonComposite;

	protected IDialogSettings fSettings;

	protected String fStructureTitle;

	protected ITreeContentProvider fTreeContentProvider;

	protected Object fInput;

	protected Bpmn2ModelerResourceSetImpl fHackedResourceSet;

	long fRunnableStart;
	protected URI fRunnableLoadURI;
	protected Job fLoaderJob;

	protected IPreferenceStore fPrefStore = Activator.getDefault().getPreferenceStore();
	protected Button fBtnResource;

	/**
	 * Create a brand new shiny Schema Import Dialog.
	 * 
	 * @param parent
	 */
	public DefaultSchemaImportDialog(Shell parent, int allowedResourceTypes) {

		super(parent);
		setStatusLineAboveButtons(true);
		int shellStyle = getShellStyle();
		setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);

		fSettings = Activator.getDefault().getDialogSettingsFor(this);

		try {
			fImportSource = fSettings.getInt(IMPORT_SOURCE);
			fImportType = fSettings.getInt(IMPORT_TYPE);
		} catch (java.lang.NumberFormatException nfe) {
			fImportSource = BID_BROWSE_WORKSPACE;
			fImportType = BID_IMPORT_XSD;
		}

		setDialogBoundsSettings(fSettings, getDialogBoundsStrategy());

		this.allowedResourceTypes = allowedResourceTypes;
		if ((allowedResourceTypes & SchemaImportDialog.ALLOW_XSD) == 0) {
			if (fImportType==BID_IMPORT_XML || fImportType==BID_IMPORT_XSD)
				fImportType = 0;
		}
		if ((allowedResourceTypes & SchemaImportDialog.ALLOW_WSDL) == 0) {
			if (fImportType==BID_IMPORT_WSDL)
				fImportType = 0;
		}
		if ((allowedResourceTypes & SchemaImportDialog.ALLOW_BPMN2) == 0) {
			if (fImportType==BID_IMPORT_BPMN2)
				fImportType = 0;
		}
		if ((allowedResourceTypes & SchemaImportDialog.ALLOW_JAVA) == 0) {
			if (fImportType==BID_IMPORT_JAVA)
				fImportType = 0;
		}
		if (fImportType==0) {
			if ((allowedResourceTypes & SchemaImportDialog.ALLOW_XSD) != 0)
				fImportType = BID_IMPORT_XSD;
			if ((allowedResourceTypes & SchemaImportDialog.ALLOW_WSDL) != 0)
				fImportType = BID_IMPORT_WSDL;
			if ((allowedResourceTypes & SchemaImportDialog.ALLOW_BPMN2) != 0)
				fImportType = BID_IMPORT_BPMN2;
			if ((allowedResourceTypes & SchemaImportDialog.ALLOW_JAVA) != 0)
				fImportType = BID_IMPORT_JAVA;
		}

		if (fImportType==BID_IMPORT_XML)
			configureAsXMLImport();
		else if (fImportType==BID_IMPORT_XSD)
			configureAsSchemaImport();
		else if (fImportType==BID_IMPORT_WSDL)
			configureAsWSDLImport();
		else if (fImportType==BID_IMPORT_BPMN2)
			configureAsBPMN2Import();
		else if (fImportType==BID_IMPORT_JAVA)
			configureAsJavaImport();
		
		bpmn2Editor = BPMN2Editor.getActiveEditor();
		ResourceSet rs =  bpmn2Editor.getResourceSet();
		fHackedResourceSet = ModelUtil.slightlyHackedResourceSet(rs);
	}
	
	public DefaultSchemaImportDialog(Shell parent) {
		this(parent, -1);
	}
	
	/**
	 * 
	 * @see Dialog#createDialogArea(Composite)
	 * 
	 * @param parent
	 *            the parent composite to use
	 * @return the composite it created to be used in the dialog area.
	 */

	@Override
	public Control createDialogArea(Composite parent) {

		Composite contents = (Composite) super.createDialogArea(parent);

		createImportType(contents);
		createImportLocation(contents);
		createImportStructure(contents);

		buttonPressed(fImportSource, true);
		return contents;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case BID_BROWSE:
			if (fImportSource == BID_BROWSE_URL) {
				String loc = fLocation.getText();
				if (loc.length() > 0) {
					attemptLoad(loc);
				}
			}
			else {
				FileDialog fileDialog = new FileDialog(getShell());
				fileDialog.setFilterExtensions(FILTER_EXTENSIONS);
				fileDialog.setFilterNames(FILTER_NAMES);
				String path = fileDialog.open();
				if (path == null) {
					return;
				}
				fLocation.setText(path);
				attemptLoad(path);
			}
			break;

		case IDialogConstants.CANCEL_ID:
			if (fLoaderJob != null) {
				if (fLoaderJob.getState() == Job.RUNNING) {
					fLoaderJob.cancel();
				}
			}
			setSelectionResult(null);
			break;
		}

		super.buttonPressed(buttonId);
	}

	protected void buttonPressed(int id, boolean checked) {

		if (id==BID_BROWSE_FILE
				|| id==BID_BROWSE_WORKSPACE
				|| id==BID_BROWSE_URL) {
			if (checked==false) {
				return;
			}

			fImportSource = id;
			fSettings.put(IMPORT_SOURCE, fImportSource);
		}
		else if (id==BID_IMPORT_XML
				|| id==BID_IMPORT_XSD
				|| id==BID_IMPORT_WSDL
				|| id==BID_IMPORT_BPMN2
				|| id==BID_IMPORT_JAVA) {
			if (checked==false) {
				return;
			}
			if (id==BID_IMPORT_XML) {
				configureAsXMLImport();
				setVisibleControl(fKindButtonComposite,true);
			}
			else if (id==BID_IMPORT_XSD) {
				configureAsSchemaImport();
				setVisibleControl(fKindButtonComposite,true);
			}
			else if (id==BID_IMPORT_WSDL) {
				configureAsWSDLImport();
				setVisibleControl(fKindButtonComposite,true);
			}
			else if (id==BID_IMPORT_BPMN2) {
				configureAsBPMN2Import();
				setVisibleControl(fKindButtonComposite,true);
			}
			else if (id==BID_IMPORT_JAVA) {
				configureAsJavaImport();
				setVisibleControl(fKindButtonComposite,false);
			}
			
			fImportType = id;
			fSettings.put(IMPORT_TYPE, fImportType);
		}
		
		setVisibleControl(fResourceComposite, fImportSource==BID_BROWSE_WORKSPACE && fImportType != BID_IMPORT_JAVA);
		setVisibleControl(fLocationComposite, fImportSource==BID_BROWSE_URL || fImportSource==BID_BROWSE_FILE || fImportType==BID_IMPORT_JAVA);
		if (fImportType==BID_IMPORT_JAVA) {
			setVisibleControl(fKindButtonComposite, false);
			setVisibleControl(fBrowseButton,false);
			fLocationLabel.setText(Messages.SchemaImportDialog_Type_Label);
		}
		else {
			setVisibleControl(fKindButtonComposite, true);
			setVisibleControl(fBrowseButton,true);
			fLocationLabel.setText(Messages.SchemaImportDialog_Location_Label);
			fBrowseButton.setText(fImportSource==BID_BROWSE_FILE ?
					Messages.SchemaImportDialog_Browse_Button : Messages.SchemaImportDialog_Load_Button);
		}
		fLocation.setText(EMPTY);
		fTypeGroup.getParent().layout(true);
		fKindGroup.getParent().layout(true);

		markEmptySelection();
	}

	protected void setVisibleControl(Control c, boolean b) {
		Object layoutData = c.getLayoutData();

		if (layoutData instanceof GridData) {
			GridData data = (GridData) layoutData;
			data.exclude = !b;
		}
		c.setVisible(b);
	}

	/**
	 * Create the dialog.
	 * 
	 */

	@Override
	public void create() {
		super.create();
		buttonPressed(fImportSource, true);
	}

	protected Button createRadioButton(Composite parent, String label, int id,
			boolean checked) {

		Button button = new Button(parent, SWT.RADIO);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData( Integer.valueOf( id ));
		button.setSelection(checked);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button b = (Button) event.widget;
				int bid = ((Integer) b.getData()).intValue();

				buttonPressed(bid, b.getSelection());
			}
		});

		return button;

	}

	protected void createImportType(Composite parent) {
		fTypeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		fTypeGroup.setText(Messages.SchemaImportDialog_Import_Type_Title);
		GridLayout layout = new GridLayout(1, true);
		GridData typeGroupGridData = new GridData();
		typeGroupGridData.grabExcessVerticalSpace = false;
		typeGroupGridData.grabExcessHorizontalSpace = true;
		typeGroupGridData.horizontalAlignment = GridData.FILL;
		typeGroupGridData.verticalAlignment = GridData.FILL;

		fTypeGroup.setLayout(layout);
		fTypeGroup.setLayoutData(typeGroupGridData);

		Composite container = new Composite(fTypeGroup, SWT.NONE);

		layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 4;
		container.setLayout(layout);
		GridData data = new GridData();
		data.grabExcessVerticalSpace = false;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		container.setLayoutData(data);

		Button button;
		
//		button = createRadioButton(control, Messages.SchemaImportDialog_20,
//				BID_IMPORT_XML, fImportType == BID_IMPORT_XML);
//		button.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		int buttonCount = 0;
		if ((allowedResourceTypes & SchemaImportDialog.ALLOW_XSD) != 0) {
			button = createRadioButton(container, Messages.SchemaImportDialog_XSD_Button,
					BID_IMPORT_XSD, fImportType == BID_IMPORT_XSD);
			button.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
			++buttonCount;
		}
		
		if ((allowedResourceTypes & SchemaImportDialog.ALLOW_WSDL) != 0) {
			button = createRadioButton(container, Messages.SchemaImportDialog_WSDL_Button,
					BID_IMPORT_WSDL, fImportType == BID_IMPORT_WSDL);
			button.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
			++buttonCount;
		}
		
		if ((allowedResourceTypes & SchemaImportDialog.ALLOW_BPMN2) != 0) {
			button = createRadioButton(container, Messages.SchemaImportDialog_BPMN2_Button,
					BID_IMPORT_BPMN2, fImportType == BID_IMPORT_BPMN2);
			button.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
			++buttonCount;
		}
		
		if ((allowedResourceTypes & SchemaImportDialog.ALLOW_JAVA) != 0) {
			button = createRadioButton(container, Messages.SchemaImportDialog_Java_Button,
					BID_IMPORT_JAVA, fImportType == BID_IMPORT_JAVA);
			button.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
			++buttonCount;
		}
		if (buttonCount==1) {
			fTypeGroup.setVisible(false);
			typeGroupGridData.exclude = true;
		}
	}
	
	protected void createImportLocation(Composite parent) {

		fKindGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		fKindGroup.setText(Messages.SchemaImportDialog_Import_Source_Title);
		GridLayout layout = new GridLayout(1, true);
		GridData data = new GridData();
		data.grabExcessVerticalSpace = false;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;

		fKindGroup.setLayout(layout);
		fKindGroup.setLayoutData(data);

		fKindButtonComposite = new Composite(fKindGroup, SWT.NONE);

		layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 4;
		fKindButtonComposite.setLayout(layout);
		data = new GridData();
		data.grabExcessVerticalSpace = false;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		fKindButtonComposite.setLayoutData(data);

		fBtnResource = createRadioButton(fKindButtonComposite, Messages.SchemaImportDialog_Workspace_Button,
				BID_BROWSE_WORKSPACE, fImportSource == BID_BROWSE_WORKSPACE);
		createRadioButton(fKindButtonComposite, Messages.SchemaImportDialog_File_System_Button,
				BID_BROWSE_FILE, fImportSource == BID_BROWSE_FILE);
		createRadioButton(fKindButtonComposite, Messages.SchemaImportDialog_URL_Button,
				BID_BROWSE_URL, fImportSource == BID_BROWSE_URL);

		// Create location variant
		fLocationComposite = new Composite(fKindGroup, SWT.NONE);

		layout = new GridLayout();
		layout.numColumns = 3;
		fLocationComposite.setLayout(layout);
		data = new GridData();
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		fLocationComposite.setLayoutData(data);

		fLocationLabel = new Label(fLocationComposite, SWT.NONE);
		fLocationLabel.setText(Messages.SchemaImportDialog_Location_Label);

		fLocation = new Text(fLocationComposite, SWT.BORDER);
		fLocation.setText(EMPTY);
		data = new GridData();
		data.grabExcessVerticalSpace = false;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		fLocation.setLayoutData(data);
//		fLocation.addListener(SWT.FocusOut, new Listener() {
//
//			public void handleEvent(Event event) {
//				String loc = fLocation.getText();
//				if (loc.length() > 0) {
//					attemptLoad(loc);
//				}
//			}
//		});
		fLocation.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent event) {
				if (fImportType == BID_IMPORT_JAVA) {
				}
				else if (event.keyCode == SWT.CR) {
					attemptLoad(fLocation.getText());
					event.doit = false;
				}
			}

			public void keyReleased(KeyEvent e) {
				if (fImportType == BID_IMPORT_JAVA) {
					String s = fLocation.getText();
					if (s!=null && s.length()>1) {
						if (!s.equals(fLocationText)) {
							fLocationText = s;
							attemptLoad(s);
						}
					}
				}
			}

		});

		fBrowseButton = createButton(fLocationComposite, BID_BROWSE,
				Messages.SchemaImportDialog_Browse_Button, false);

		// End of location variant

		// Start Resource Variant
		fResourceComposite = new FileSelectionGroup(fKindGroup, new Listener() {
			public void handleEvent(Event event) {
				IResource resource = fResourceComposite.getSelectedResource();
				if (resource != null && resource.getType() == IResource.FILE) {
					// only attempt to load a resource which is not a control
					attemptLoad((IFile) resource);
					return;
				}
				markEmptySelection();
			}
		}, Messages.SchemaImportDialog_Select_Resource_Title, resourceFilter); //$NON-NLS-1$

		TreeViewer viewer = fResourceComposite.getTreeViewer();
		viewer.setAutoExpandLevel(2);

		// End resource variant
	}

	protected Object createImportStructure(Composite parent) {

		fStructureLabel = new Label(parent, SWT.NONE);
		fStructureLabel.setText(fStructureTitle);

		// Tree viewer for variable structure ...
		fTree = new Tree(parent, SWT.BORDER);

		fTreeViewer = new TreeViewer(fTree);
		fTreeViewer.setContentProvider(fTreeContentProvider);
		fTreeViewer.setLabelProvider(new ModelTreeLabelProvider());
		fTreeViewer.setInput(null);
		fTreeViewer.setAutoExpandLevel(3);
		fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (!sel.isEmpty()) {
					computeResult();
					if (getResult()!=null)
						updateStatus(Status.OK_STATUS);
					else
						updateStatus(new Status(IStatus.ERROR, Activator.getDefault().getID(),0,
								Messages.SchemaImportDialog_Select_Java_Message,null));
				} else {
					markEmptySelection();
				}
			}
		});
		// end tree viewer for variable structure
		GridData data = new GridData();
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.minimumHeight = 200;
		fTree.setLayoutData(data);

		return fTree;
	}

	protected Object attemptLoad(URI uri, String kind) {

		Resource resource = null;
		if ("java".equals(kind)) { //$NON-NLS-1$
			final String fileName = uri.lastSegment();
			final ArrayList<IType> results = new ArrayList<IType>();
				try {
				    IProject p = bpmn2Editor.getProject();
					if (p.isOpen() && p.hasNature(JavaCore.NATURE_ID)) {
						final IJavaProject javaProject = JavaCore.create(p);
						JavaProjectClassLoader cl = new JavaProjectClassLoader(javaProject);
						results.addAll(cl.findClasses(fileName));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			return results;
		}
		else {
			try {
				resource = fHackedResourceSet.getResource(uri, true, kind);
			} catch (Exception e) {
				Activator.logError(e);
				return e;
			}
		
			if (resource!=null && resource.getErrors().isEmpty() && resource.isLoaded()) {
				return resource.getContents().get(0);
			}
		}
		return null;
	}
	
	Object  attemptLoad ( URI uri ) {
		return attemptLoad (uri, fResourceKind );
	}
	
	
	void attemptLoad ( IFile file ) {
		attemptLoad ( file.getFullPath().toString() );
	}
	
		
	protected void attemptLoad ( String path ) {
		
		if (fLoaderJob != null) {			
			if (fLoaderJob.getState() == Job.RUNNING) {
				fLoaderJob.cancel();
			}			
		}
		
		updateStatus ( Status.OK_STATUS );		

		// empty paths are ignored
		path = path.trim();
		if (path.length() == 0) {
			return ;
		}
		

		URI uri = convertToURI ( path );
		if (uri == null) {
			return ;
		}
		
		
		fRunnableLoadURI = uri;		
		final String msg = MessageFormat.format(Messages.SchemaImportDialog_Loading_Message,fRunnableLoadURI);		 	    
		fLoaderJob = new Job(msg) {

			@Override
			protected IStatus run (IProgressMonitor monitor) {
				monitor.beginTask(msg, 1);				
				// Bug 290090 - move this to asyncExec() as below because the method will
				// modify UI parameter, if not, will have a invalid access error.

				/* fInput = attemptLoad(fRunnableLoadURI); */

				monitor.worked(1);
				if (fBrowseButton != null
						&& fBrowseButton.isDisposed() == false) {
					fBrowseButton.getDisplay().asyncExec(new Runnable() {
						public void run() {
							fInput = attemptLoad(fRunnableLoadURI);
							loadDone();
						}
					});
				}
				
				return Status.OK_STATUS;
			}			 		
		};	
		 
		fLoaderJob.schedule();		 
		fRunnableStart = System.currentTimeMillis();

		updateStatus ( new Status(IStatus.INFO, Activator.getDefault().getID(),0,msg,null));
	}

	
	 
	@SuppressWarnings("boxing")
	protected void loadDone () {
		
		long elapsed = System.currentTimeMillis() - fRunnableStart;
		
		if (fInput == null || fInput instanceof Throwable) {
			markEmptySelection();
			
			updateStatus( new Status(IStatus.ERROR,Activator.getDefault().getID(),0,
					MessageFormat.format(Messages.SchemaImportDialog_Load_Failed_Message,fRunnableLoadURI,elapsed),(Throwable) fInput) );
			fInput = null;
			
		} else {
			
			updateStatus ( new Status(IStatus.INFO, Activator.getDefault().getID(),0,
					MessageFormat.format(Messages.SchemaImportDialog_Loaded_Message,fRunnableLoadURI,elapsed),null)) ;
				

			// display a warning if this import does not define a targetNamespace
			String type = null;
			String ns = null;
			String loc = null;
			if (fInput instanceof XSDSchema) {
				XSDSchema schema = (XSDSchema)fInput;
				ns = schema.getTargetNamespace();
				loc = schema.getSchemaLocation();
				type = Messages.DefaultSchemaImportDialog_XSD_Type;
			}
			else if (fInput instanceof Definition) {
				Definition definition = (Definition)fInput;
				ns = definition.getTargetNamespace();
				loc = definition.getLocation();
				type = Messages.DefaultSchemaImportDialog_WSDL_Type;
			}
			else if (fInput instanceof org.eclipse.bpmn2.DocumentRoot) {
				DocumentRoot root = (DocumentRoot)fInput;
				org.eclipse.bpmn2.Definitions definitions = root.getDefinitions();
				ns = definitions.getTargetNamespace();
				loc = root.eResource().getURI().toString();
				type = Messages.DefaultSchemaImportDialog_BPMN2_Type;
				fInput = definitions;
			}
			else if (fInput instanceof List) {
				markEmptySelection();
			}
			if (type!=null) {
				if (ns==null || ns.isEmpty()) {
					updateStatus ( new Status(IStatus.WARNING, Activator.getDefault().getID(),0,
							MessageFormat.format(Messages.DefaultSchemaImportDialog_Missing_Namespace_Message,type),null)) ;
				}
				if (loc==null || loc.isEmpty()) {
					updateStatus( new Status(IStatus.ERROR,Activator.getDefault().getID(),0,
							MessageFormat.format(Messages.SchemaImportDialog_Load_Failed_Message,fRunnableLoadURI,elapsed),null) );
					fInput = null;
				}
			}
			
			fTreeViewer.setInput(fInput);				
			fTree.getVerticalBar().setSelection(0);
		}
	}
	
	
	
	protected void markEmptySelection () {
		updateStatus ( Status.OK_STATUS );
		updateOK(false);
		fTreeViewer.setInput(null);
	}
	
	
	protected URI convertToURI (String path ) {
		
		try {
			switch (fImportSource) {
			case BID_BROWSE_FILE : 
				return URI.createFileURI( path );				
			
			case BID_BROWSE_WORKSPACE :
				return URI.createPlatformResourceURI(path,true);				

			case BID_BROWSE_URL :
				return URI.createURI(path);
				

				
			default :
				return null;
			}
			
		} catch (Exception ex) {
			updateStatus ( new Status(IStatus.ERROR,Activator.getDefault().getID(),0,Messages.SchemaImportDialog_Invalid_Location,ex) );			
			return null;
		}
	}

	/**
	 * Update the state of the OK button to the state indicated.
	 * 
	 * @param state
	 *            false to disable, true to enable.
	 */

	public void updateOK(boolean state) {
		Button okButton = getOkButton();
		if (okButton != null && !okButton.isDisposed()) {
			okButton.setEnabled(state);
		}
	}

	/**
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
	 */

	@Override
	protected void computeResult() {
		Object object = fTreeViewer.getInput();
		if (object == null) {
			return;
		}
		if (fImportType == BID_IMPORT_JAVA) {
			IStructuredSelection sel = (IStructuredSelection)fTreeViewer.getSelection();
			if (!sel.isEmpty()) {
				TreeNode treeNode = (TreeNode)sel.getFirstElement();
				if (treeNode.getModelObject() instanceof IType)
					setSelectionResult(new Object[] { treeNode.getModelObject() });
				else
					setSelectionResult(null);
			}
		}
		else {
			setSelectionResult(new Object[] { object });
		}
	}

	/**
	 * TODO: not implemented - do we need this?
	 */
	public void configureAsXMLImport() {
		setTitle(Messages.SchemaImportDialog_Browse_XML_Title);
		fStructureTitle = Messages.SchemaImportDialog_Structure_Label;
		if (fStructureLabel!=null)
			fStructureLabel.setText(fStructureTitle);
		fTreeContentProvider = new VariableTypeTreeContentProvider(true, true);
		if (fTreeViewer!=null)
			fTreeViewer.setContentProvider(fTreeContentProvider);
		fResourceKind = "xml"; //$NON-NLS-1$

		String[] xml_FILTER_EXTENSIONS = {
				"*.xml", //$NON-NLS-1$
				"*.xsd", //$NON-NLS-1$
				"*.wsdl", //$NON-NLS-1$
				"*.*" //$NON-NLS-1$
		};
		FILTER_EXTENSIONS = xml_FILTER_EXTENSIONS;

		String[] xml_FILTER_NAMES = {
				Messages.DefaultSchemaImportDialog_XML_Filter,
				Messages.DefaultSchemaImportDialog_XSD_Filter,
				Messages.DefaultSchemaImportDialog_WSDL_Filter,
				Messages.DefaultSchemaImportDialog_All
		};
		FILTER_NAMES = xml_FILTER_NAMES;

		resourceFilter = ".xml"; //$NON-NLS-1$
		if (fResourceComposite!=null)
			fResourceComposite.setFileFilter(resourceFilter);
	}

	/**
	 * Configure the dialog as a schema import dialog. Set the title and the
	 * structure pane message.
	 * 
	 */

	public void configureAsSchemaImport() {
		setTitle(Messages.SchemaImportDialog_Browse_XSD_Title);
		fStructureTitle = Messages.SchemaImportDialog_Types_Label;
		if (fStructureLabel!=null)
			fStructureLabel.setText(fStructureTitle);
		fTreeContentProvider = new VariableTypeTreeContentProvider(true, true);
		if (fTreeViewer!=null)
			fTreeViewer.setContentProvider(fTreeContentProvider);
		fResourceKind = "xsd"; //$NON-NLS-1$

		String[] wsdl_FILTER_EXTENSIONS = {
				"*.xml", //$NON-NLS-1$
				"*.xsd", //$NON-NLS-1$
				"*.wsdl", //$NON-NLS-1$
				"*.*" //$NON-NLS-1$
		};
		FILTER_EXTENSIONS = wsdl_FILTER_EXTENSIONS;

		String[] wsdl_FILTER_NAMES = {
				Messages.DefaultSchemaImportDialog_XML_Filter,
				Messages.DefaultSchemaImportDialog_XSD_Filter,
				Messages.DefaultSchemaImportDialog_WSDL_Filter,
				Messages.DefaultSchemaImportDialog_All
		};
		FILTER_NAMES = wsdl_FILTER_NAMES;

		resourceFilter = ".xsd"; //$NON-NLS-1$
		if (fResourceComposite!=null)
			fResourceComposite.setFileFilter(resourceFilter);
	}

	/**
	 * Configure the dialog as a WSDL import dialog. Set the title and the
	 * structure pane message.
	 * 
	 */

	public void configureAsWSDLImport() {

		setTitle(Messages.SchemaImportDialog_Browse_WSDL_Title);
		fStructureTitle = Messages.SchemaImportDialog_Ports_Title;
		if (fStructureLabel!=null)
			fStructureLabel.setText(fStructureTitle);
		fTreeContentProvider = new ServiceTreeContentProvider(true);
		if (fTreeViewer!=null)
			fTreeViewer.setContentProvider(fTreeContentProvider);
		fResourceKind = "wsdl"; //$NON-NLS-1$

		String[] wsdl_FILTER_EXTENSIONS = {
				"*.wsdl", //$NON-NLS-1$
				"*.*" //$NON-NLS-1$
		};
		FILTER_EXTENSIONS = wsdl_FILTER_EXTENSIONS;

		String[] wsdl_FILTER_NAMES = {
				Messages.DefaultSchemaImportDialog_WSDL_Filter,
				Messages.DefaultSchemaImportDialog_All
		};
		FILTER_NAMES = wsdl_FILTER_NAMES;

		resourceFilter = ".wsdl"; //$NON-NLS-1$
		if (fResourceComposite!=null)
			fResourceComposite.setFileFilter(resourceFilter);
	}

	public void configureAsBPMN2Import() {

		setTitle(Messages.SchemaImportDialog_Browse_BPMN2_Title);
		fStructureTitle = Messages.SchemaImportDialog_Interfaces_Label;
		if (fStructureLabel!=null)
			fStructureLabel.setText(fStructureTitle);
		fTreeContentProvider = new BPMN2DefinitionsTreeContentProvider(true);
		if (fTreeViewer!=null)
			fTreeViewer.setContentProvider(fTreeContentProvider);
		fResourceKind = ""; //$NON-NLS-1$

		String[] wsdl_FILTER_EXTENSIONS = {
				"*.bpmn", //$NON-NLS-1$
				"*.bpmn2", //$NON-NLS-1$
				"*.*" //$NON-NLS-1$
		};
		FILTER_EXTENSIONS = wsdl_FILTER_EXTENSIONS;

		String[] wsdl_FILTER_NAMES = {
				Messages.DefaultSchemaImportDialog_BPMN2_Filter_1,
				Messages.DefaultSchemaImportDialog_BPMN2_Filter_2,
				Messages.DefaultSchemaImportDialog_All
		};
		FILTER_NAMES = wsdl_FILTER_NAMES;

		resourceFilter = ".bpmn,.bpmn2"; //$NON-NLS-1$
		if (fResourceComposite!=null)
			fResourceComposite.setFileFilter(resourceFilter);
	}

	public void configureAsJavaImport() {

		setTitle(Messages.SchemaImportDialog_Browse_Java_Title);
		fStructureTitle = Messages.SchemaImportDialog_Java_Types_Label;
		if (fStructureLabel!=null)
			fStructureLabel.setText(fStructureTitle);
		fTreeContentProvider = new JavaTreeContentProvider(true);
		if (fTreeViewer!=null)
			fTreeViewer.setContentProvider(fTreeContentProvider);
		fResourceKind = "java"; //$NON-NLS-1$

		String[] java_FILTER_EXTENSIONS = {
				"*.java", //$NON-NLS-1$
				"*.class", //$NON-NLS-1$
				"*.jar", //$NON-NLS-1$
				"*.*" //$NON-NLS-1$
		};
		FILTER_EXTENSIONS = java_FILTER_EXTENSIONS;

		String[] wsdl_FILTER_NAMES = {
				Messages.DefaultSchemaImportDialog_Java_Filter_1,
				Messages.DefaultSchemaImportDialog_Java_Filter_2,
				Messages.DefaultSchemaImportDialog_Java_Filter_3,
				Messages.DefaultSchemaImportDialog_All
		};
		FILTER_NAMES = wsdl_FILTER_NAMES;

		// Resource selection widget not used (yet)
		resourceFilter = ".java"; //$NON-NLS-1$
		if (fResourceComposite!=null)
			fResourceComposite.setFileFilter(resourceFilter);
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		if (getShell()!=null)
			getShell().setText(title);
	}
	
	/**
	 * 
	 * @author Michal Chmielewski (michal.chmielewski@oracle.com)
	 * @date May 4, 2007
	 * 
	 */
	public class TreeFilter extends ViewerFilter {

		/**
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			return true;
		}
	}
}
