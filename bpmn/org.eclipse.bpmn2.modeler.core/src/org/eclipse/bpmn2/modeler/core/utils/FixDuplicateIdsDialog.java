/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.utils;

import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListSelectionDialog;

public class FixDuplicateIdsDialog extends ListSelectionDialog {

	static private MyContentProvider contentProvider = new MyContentProvider();
	static private MyLabelProvider labelProvider = new MyLabelProvider();
	List<Tuple<EObject,EObject>> duplicates;
	
	public FixDuplicateIdsDialog(List<Tuple<EObject,EObject>> duplicates) {
		super(Display.getDefault().getActiveShell(), duplicates, contentProvider, labelProvider,
				Messages.FixDuplicateIdsDialog_Message);
		this.duplicates = duplicates;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int open() {
		final int status[] = new int[1];
		Display.getDefault().asyncExec( new Runnable() {

			@Override
			public void run() {
				status[0] = FixDuplicateIdsDialog.super.open();
				if (status[0]==Window.OK) {
					final Object[] results = getResult();
					if (results.length>0) {
						Tuple<EObject,EObject> tuple = (Tuple<EObject,EObject>)results[0];
						TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(tuple.getFirst().eResource());
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
						
								for (Object entry : results) {
									Tuple<EObject,EObject> tuple = (Tuple<EObject,EObject>)entry;
									EObject object = tuple.getSecond();
					
									EStructuralFeature idFeature = object.eClass().getEIDAttribute();
									Object id = object.eGet(idFeature);
									Object uniqueId = makeUniqueId(object,id);
									object.eSet(idFeature, uniqueId);
								}
							}
						});
					}
				}
			}
			
		});
		return status[0];
	}

	private Object makeUniqueId(EObject object, Object id) {
		int i = 1;
		Object uniqueId = id;
		EObject dup = null;
		do {
			dup = findDuplicateId(object,uniqueId);
			if (dup!=null) {
				uniqueId = id + "_" + i++; //$NON-NLS-1$
			}
		}
		while (dup!=null);
		return uniqueId;
	}

	private EObject findDuplicateId(EObject object, Object id) {
		if (object!=null && id!=null) {
			Resource resource = object.eResource();
			
			TreeIterator<EObject> iter = resource.getAllContents();
			while (iter.hasNext()) {
				EObject o = iter.next();
				if (o!=object) {
					EStructuralFeature f = o.eClass().getEStructuralFeature("id"); //$NON-NLS-1$
					if (f!=null) {
						Object existingId = o.eGet(f);
						if (id.equals(existingId))
							return o;
					}
				}
			}
		}
		return null;
	}

	private static class MyContentProvider implements IStructuredContentProvider {

		public MyContentProvider() {
			super();
		}
		
		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			List<Tuple<EObject,EObject>> duplicates = (List<Tuple<EObject,EObject>>) inputElement;
			return duplicates.toArray();
		}
		
	}

	private static class MyLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			Tuple<EObject,EObject> tuple = (Tuple<EObject,EObject>)element;
			EObject o1 = tuple.getFirst();
			EObject o2 = tuple.getSecond();
			return NLS.bind(
				Messages.FixDuplicateIdsDialog_Duplicate_ID,
				ModelUtil.toCanonicalString(o1),
				ModelUtil.toCanonicalString(o2)
			);
		}
		
	}
}
