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

package org.eclipse.bpmn2.modeler.core.merrimac.dialogs;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.IPropertiesCompositeFactory;
import org.eclipse.bpmn2.modeler.core.validation.LiveValidationListener;
import org.eclipse.bpmn2.modeler.core.validation.ValidationErrorHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public abstract class AbstractObjectEditingDialog extends FormDialog implements ValidationErrorHandler {

	protected IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	protected DiagramEditor editor;
	protected String title = ""; //$NON-NLS-1$
	protected EObject object;
	protected boolean cancel = false;
	protected boolean abortOnCancel = true;
	protected Transaction transaction;
	protected ScrolledForm form;
	protected Composite dialogContent;
    private Text errorMessageText;
    private IPropertiesCompositeFactory compositeFactory = null;
	// If this property is set on a Control, then don't try to
	// adapt the Control's colors/fonts/etc. to dialog defaults
    // This is used by the Description Styled Text widget.
	public final static String DO_NOT_ADAPT = "do_not_adapt"; //$NON-NLS-1$
    
	public AbstractObjectEditingDialog(DiagramEditor editor, EObject object) {
		super(editor.getEditorSite().getShell());
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.MAX | SWT.RESIZE
				| getDefaultOrientation());
		
		this.editor = editor;
		this.object = object;
	}

	public void setCompositeFactory(IPropertiesCompositeFactory compositeFactory) {
		this.compositeFactory = compositeFactory;
	}
	
	@Override
	protected void createFormContent(IManagedForm mform) {
		super.createFormContent(mform); 
		form = mform.getForm();
		form.setExpandHorizontal(true);
		form.setExpandVertical(true);
		form.setText(null);

		Composite body = form.getBody();
		body.setBackground(form.getBackground());

		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		body.setLayoutData(data);
		body.setLayout(new FormLayout());
		
		dialogContent = createDialogContent(body);
		if (compositeFactory!=null)
			dialogContent.setData("factory", compositeFactory); //$NON-NLS-1$
		
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		dialogContent.setLayoutData(data);
		
		form.setContent(body);
		getShell().pack();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        
        errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
        errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        errorMessageText.setForeground(errorMessageText.getDisplay()
                .getSystemColor(SWT.COLOR_RED));
        errorMessageText.setBackground(errorMessageText.getDisplay()
                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        return composite;
	}

	abstract protected Composite createDialogContent(Composite parent);
	abstract protected String getPreferenceKey();
	
	protected String getTitle() {
		return title;
	}
	
	protected void addControlListener() {

		final String key = getPreferenceKey();
		Point p = getShell().getSize();
		int width = preferenceStore.getInt("dialog."+key+".width"); //$NON-NLS-1$ //$NON-NLS-2$
		if (width==0)
			width = p.x;
		int height = preferenceStore.getInt("dialog."+key+".height"); //$NON-NLS-1$ //$NON-NLS-2$
		if (height==0)
			height = p.y;
		getShell().setSize(width,height);
		
		p = getShell().getLocation();
		int x = preferenceStore.getInt("dialog."+key+".x"); //$NON-NLS-1$ //$NON-NLS-2$
		if (x==0)
			x = p.x;
		int y = preferenceStore.getInt("dialog."+key+".y"); //$NON-NLS-1$ //$NON-NLS-2$
		if (y==0)
			y = p.y;
		getShell().setLocation(x,y);

		getShell().addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e)
			{
				Point p = getShell().getLocation();
				preferenceStore.setValue("dialog."+key+".x", p.x); //$NON-NLS-1$ //$NON-NLS-2$
				preferenceStore.setValue("dialog."+key+".y", p.y); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			public void controlResized(ControlEvent e)
			{
				Point p = getShell().getSize();
				preferenceStore.setValue("dialog."+key+".width", p.x); //$NON-NLS-1$ //$NON-NLS-2$
				preferenceStore.setValue("dialog."+key+".height", p.y); //$NON-NLS-1$ //$NON-NLS-2$
			}
	
		});
		
        hookTransaction();
	}
	
	protected void aboutToOpen() {
		dialogContent.setData(object);
	}

	@Override
	public void create() {
		super.create();
		startTransaction();
	}
	
	@Override
	public int open() {
		if (getShell()==null)
			create();
		
		getShell().setText(getTitle());
		getShell().setSize(600,400);

		addControlListener();
		
		// Tell the Live Validation Listener to report validation errors to us
		// instead of the Workbench Status line.
		LiveValidationListener.setValidationErrorHandler(this);
		getShell().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				LiveValidationListener.setValidationErrorHandler(null);
			}
		});
		
		aboutToOpen();

		adapt(dialogContent);
		
		return super.open();
	}
	
	protected void adapt(Composite content) {
		
		// The AbstractDetailComposite controls don't actually get constructed until
		// setBusinessObject() is called - the business object determines which controls
		// are required. So, this needs to happen very late in the dialog lifecycle.
		// We can now safely set the background color of all controls to match the dialog.
		content.setBackground(form.getBackground());
		for (Control k : content.getChildren()) {
			Object data = k.getData(AbstractObjectEditingDialog.DO_NOT_ADAPT);
			if (data instanceof Boolean && (Boolean)data == true)
				continue;
			
			k.setBackground(form.getBackground());
			if (k instanceof Composite) {
				adapt((Composite)k);
			}
		}
	}
	
	@Override
	public boolean close() {
		if (getReturnCode() != OK)
			cancel = true;
		return super.close();
	}

	/**
	 * Return state of the "abortOnCancel transaction on cancel" flag
	 * 
	 * @return true if the current transaction will be aborted if the dialog is canceled.
	 */
	public boolean isAbortOnCancel() {
		return abortOnCancel;
	}

	/**
	 * Abort the currently active transaction if dialog is canceled either by clicking the "Cancel"
	 * button, pressing the ESCAPE key or closing the dialog with the Window Close button.
	 * 
	 * @param abortOnCancel - if true, abortOnCancel the current transaction if dialog is canceled,
	 * otherwise allow transaction to commit.
	 */
	public void setAbortOnCancel(boolean abort) {
		this.abortOnCancel = abort;
	}

	@Override
	protected void cancelPressed() {
		cancel = true;
		dialogContent.dispose();
		super.cancelPressed();
	}
	
	@Override
	protected void okPressed() {
		cancel = false;
		dialogContent.dispose();
		super.okPressed();
	}

	public boolean hasDoneChanges() {
		return transaction==null || !transaction.getChangeDescription().isEmpty();
	}
	
	protected void startTransaction() {
		if (transaction==null) {
			try {
				final InternalTransactionalEditingDomain transactionalDomain = (InternalTransactionalEditingDomain) editor
						.getEditingDomain();
				transaction = transactionalDomain.startTransaction(false, null);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void hookTransaction() {
		getShell().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				if (transaction!=null && transaction.isActive()) {
					if (cancel) {
						transaction.rollback();
					}
					else {
						try {
							transaction.commit();
						}
						catch (RollbackException e) {
							ErrorDialog.openError(getShell(), Messages.AbstractObjectEditingDialog_Commit_Error,
									Messages.AbstractObjectEditingDialog_Commit_Error_Title, new Status(IStatus.ERROR,
											Activator.PLUGIN_ID, e.getMessage(), e));
						}
					}
				}
			}
		});
	}
	
	protected void rollbackTransaction() {
		if (transaction!=null) {
			transaction.rollback();
			transaction = null;
		}
	}
	
	public void reportError(IStatus s)
	{
		String errorMessage = (s==null) ? null : s.getMessage();
    	if (errorMessageText != null && !errorMessageText.isDisposed()) {
    		errorMessageText.setText(errorMessage == null ? "" : errorMessage); //$NON-NLS-1$
    		// Disable the error message text control if there is no error, or
    		// no error text (empty or whitespace only).  Hide it also to avoid
    		// color change.
    		boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
    		errorMessageText.setEnabled(hasError);
    		errorMessageText.setVisible(hasError);
    		GridData gd = (GridData) errorMessageText.getLayoutData();
    		gd.exclude = !hasError;
    		if (dialogArea!=null)
    			dialogArea.getParent().layout();
 
    		if (s!=null && s.getSeverity()>=IStatus.ERROR) {
	    		Control button = getButton(IDialogConstants.OK_ID);
	    		if (button != null) {
	    			button.setEnabled(hasError);
	    		}
    		}
    	}
    }
}