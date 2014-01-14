package aurora.ide.prototype.consultant.product.demonstrate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.control.ConsultantDemonstratingComposite;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.wizard.dialog.DemonstratingDialog;
import aurora.ide.prototype.consultant.product.action.DemonstrateAction;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;

public class DemonstratingLoginPageDialog extends DemonstratingDialog {

	private DemonstrateAction demonstrateAction;

	public DemonstratingLoginPageDialog(Shell parentShell, File project) {
		super(parentShell, null);
		this.setProject(project);
	}

	protected Point getInitialSize() {
		Rectangle bounds = Display.getCurrent().getBounds();
		return new Point(bounds.width, bounds.height);
	}

	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// super.createButtonsForButtonBar(parent);
	}

	public boolean close() {
		boolean close = super.close();
		if (demonstrateAction != null) {
			demonstrateAction.updateStatus();
		}
		return close;
	}

	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) innerCreateDialogArea(parent);
		container.setLayout(new GridLayout());
		ConsultantDemonstratingComposite vsEditor = new ConsultantDemonstratingComposite(
				this);
		vsEditor.setInput(loadXML());
		vsEditor.createPartControl(container);
		vsEditor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		return container;
	}

	protected Control innerCreateDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		return composite;
	}

	public void setInput(Object input) {
	}

	private static ScreenBody loadXML() {

		InputStream is = null;
		try {
			is = DemonstratingLoginPageDialog.class
					.getResourceAsStream("login.uip");
			CompositeLoader parser = new CompositeLoader();
			CompositeMap loadFile = parser.loadFromStream(is);

			if (loadFile != null) {
				CompositeMap2Object c2o = new CompositeMap2Object();
				return c2o.createScreenBody(loadFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new ScreenBody();

	}

	public void applyValue(String value) {
		// demon.applyValue(value);
		this.close();
	}

	public EditPartFactory getPartFactory(EditorMode editorMode) {
		return new LoginPagePartFactory(editorMode);
	}

	public DemonstrateAction getDemonstrateAction() {
		return demonstrateAction;
	}

	public void setDemonstrateAction(DemonstrateAction demonstrateAction) {
		this.demonstrateAction = demonstrateAction;
	}


}
