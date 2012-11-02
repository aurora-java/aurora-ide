package aurora.ide.views.bm.view;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import aurora.ide.helpers.DialogUtil;
import aurora.ide.views.IListener;
import aurora.ide.views.ListenerHandler;

public class ModulesComposite extends Composite {

	private Button active;

	private ListenerHandler lh = new ListenerHandler();

	private class ButtonSelectionListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (active != null) {
				active.setSelection(false);
			}
			if (e.widget instanceof Button) {
				active = (Button) e.widget;
				lh.handleIt(active.getData());
			}
		}
	}

	private ButtonSelectionListener bsl = new ButtonSelectionListener();

	public ModulesComposite(Composite parent, int style) {
		super(parent, style);
	}

	public void addListener(IListener l) {
		lh.addListener(l);
	}

	public void setInput(IContainer container) {
		try {
			this.setLayout(new GridLayout());
			this.createModuleControl(this, container);
			IResource[] members = container.members();
			for (IResource r : members) {
				if (r instanceof IContainer) {
					this.createModuleControl(this, (IContainer) r);
				}
			}
			Control[] children = this.getChildren();
			for (Control control : children) {
				control.getSize();
			}
			 this.pack();
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
	}

	private void createModuleControl(Composite parent, IContainer container) {
		Button b = new Button(parent, SWT.TOGGLE | SWT.FLAT);
		b.setData(container);
		b.addSelectionListener(bsl);
		b.setText(container.getName());
		b.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

}
