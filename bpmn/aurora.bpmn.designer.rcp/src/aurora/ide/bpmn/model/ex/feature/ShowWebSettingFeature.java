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
package aurora.ide.bpmn.model.ex.feature;

import java.util.List;

import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.modeler.core.IConstants;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.browser.DefaultWorkbenchBrowserSupport;

import aurora.bpmn.designer.rcp.browser.BrowserDialog;
import aurora.ide.designer.editor.AuroraBpmnEditor;

/**
 * The Class ShowPropertiesFeature.
 */
public class ShowWebSettingFeature extends AbstractCustomFeature {

	/** The changes done. */
	protected boolean changesDone = false;

	/** The current context */
	ICustomContext context;

	/**
	 * Instantiates a new show properties feature.
	 * 
	 * @param fp
	 *            the fp
	 */
	public ShowWebSettingFeature(IFeatureProvider fp) {
		super(fp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return "Setting";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		if (context != null) {
			PictogramElement[] pes = context.getPictogramElements();
			EObject businessObject = BusinessObjectUtil
					.getBusinessObjectForPictogramElement(pes[0]);
			EStructuralFeature feature = businessObject.eClass()
					.getEStructuralFeature("documentation"); //$NON-NLS-1$
			List<Documentation> docList = (List<Documentation>) businessObject
					.eGet(feature);
			if (docList.size() > 0) {
				String text = docList.get(0).getText();
				if (text != null)
					return text;
			}
		}
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute
	 * (org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		// PictogramElement[] pes = context.getPictogramElements();
		// EObject businessObject =
		// BusinessObjectUtil.getBusinessObjectForPictogramElement(pes[0]);
		// if (businessObject!=null && pes.length==1) {
		//			EStructuralFeature feature = businessObject.eClass().getEStructuralFeature("documentation"); //$NON-NLS-1$
		// if (feature!=null) {
		// ModelEnablements me = getModelEnablements();
		// if (me!=null && me.isEnabled(businessObject.eClass().getName(),
		// feature.getName())) {
		// this.context = context;
		// return true;
		// }
		// }
		// }
		// this.context = null;
		// return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable
	 * (org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse
	 * .graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {

		PictogramElement[] pes = context.getPictogramElements();
		EObject businessObject = BusinessObjectUtil
				.getBusinessObjectForPictogramElement(pes[0]);
		EStructuralFeature feature = businessObject.eClass()
				.getEStructuralFeature("id"); //$NON-NLS-1$
		Object eGet = businessObject.eGet(feature);
		AuroraBpmnEditor multipageEditor = (AuroraBpmnEditor) BPMN2Editor
				.getActiveEditor().getMultipageEditor();
		// this.getDiagramBehavior().getDiagramContainer();
		// this.getDiagramEditor();
		String host = multipageEditor.getDefine().getServiceModel().getHost();
		String process_code = multipageEditor.getDefine().getProcess_code();
		String process_version = multipageEditor.getDefine()
				.getProcess_version();
		String define_id = multipageEditor.getDefine().getDefine_id();
		BrowserDialog dialog = new BrowserDialog(multipageEditor.getSite()
				.getShell());
		// http://172.20.0.38:9090/bpm/modules/bpm/BPM1001/bpmn_usertask_node.screen?process_code=WFL_TEST3&process_version=1&node_id=UserTask_1

		dialog.open(host
				+ "modules/bpm/BPM1001/bpmn_usertask_node.screen?process_code="
				+ process_code + "&process_version=" + process_version
				+ "&node_id=" + eGet);
		// DefaultWorkbenchBrowserSupport dbs = new
		// DefaultWorkbenchBrowserSupport();
		// try {
		// dbs.createBrowser(
		// "aurora.ide.prototype.consultant.product.action.GetHelpAction")
		// .openURL(
		// new URL(
		// "http://aurora.hand-china.com/demo/modules/fnd/FND2020/fnd_company.screen?define_id="
		// + define_id + "&element_id=" + eGet));
		// } catch (PartInitException e) {
		// e.printStackTrace();
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// }

		//		EStructuralFeature feature = businessObject.eClass().getEStructuralFeature("documentation"); //$NON-NLS-1$
		// List<Documentation> docList =
		// (List<Documentation>)businessObject.eGet(feature);
		// Documentation documentation = null;
		//		String text = ""; //$NON-NLS-1$
		// if (docList.size()>0) {
		// documentation = docList.get(0);
		// text = documentation.getText();
		// }
		//
		// DocumentationDialog dialog = new
		// DocumentationDialog(Display.getDefault().getActiveShell());
		// dialog.setValue(text);
		// if (dialog.open() == Window.OK){
		// text = dialog.getValue();
		// if (documentation==null) {
		// if (text.isEmpty())
		// return;
		// documentation = Bpmn2Factory.eINSTANCE.createDocumentation();
		// docList.add(documentation);
		// }
		// documentation.setText(text);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
	 */
	@Override
	public boolean hasDoneChanges() {
		return changesDone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.features.custom.AbstractCustomFeature#getImageId()
	 */
	@Override
	public String getImageId() {
		return IConstants.ICON_INFO_16;
	}

	/**
	 * Gets the model enablements. TODO: consider moving this stuff to a
	 * superclass
	 * 
	 * @return the model enablements
	 */
	protected ModelEnablements getModelEnablements() {
		DiagramEditor editor = (DiagramEditor) getDiagramEditor();
		return (ModelEnablements) editor.getAdapter(ModelEnablements.class);
	}

	public class DocumentationDialog extends Dialog {

		/**
		 * @param parentShell
		 */
		protected DocumentationDialog(Shell parentShell) {
			super(parentShell);
		}

		String value;
		StyledText text;
		Font descriptionFont;
		Display display = Display.getDefault();

		public Font getDescriptionFont() {
			if (descriptionFont == null) {
				FontData data = display.getSystemFont().getFontData()[0];
				descriptionFont = new Font(display, data.getName(),
						data.getHeight() + 1, SWT.NONE);
			}
			return descriptionFont;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);
			container.setLayout(new GridLayout(2, true));
			text = new StyledText(container, SWT.BORDER | SWT.MULTI
					| SWT.V_SCROLL | SWT.WRAP);
			text.setText(value);

			text.setFont(getDescriptionFont());

			// text.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			// text.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			// text.setData(AbstractObjectEditingDialog.DO_NOT_ADAPT ,
			// Boolean.TRUE);

			GridData d = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
			d.horizontalIndent = 0;
			d.verticalIndent = 0;
			d.heightHint = (int) (5.5 * getDescriptionFont().getFontData()[0]
					.getHeight());
			d.widthHint = 100;
			text.setLayoutData(d);

			return container;
		}

		@Override
		protected void okPressed() {
			value = text.getText();
			super.okPressed();
		}

		@Override
		protected void configureShell(Shell newShell) {
			newShell.setText("Setting");
			super.configureShell(newShell);
		}

		protected int getShellStyle() {
			return super.getShellStyle(); // SWT.TOP | SWT.RESIZE | SWT.BORDER |
											// SWT.TOOL;
		}

		@Override
		protected Point getInitialSize() {
			return new Point(450, 300);
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
