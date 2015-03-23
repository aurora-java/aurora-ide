package aurora.ide.node.action;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.editor.PropertyViewer;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;

public class AddPropertyAction extends ActionListener {
	PropertyViewer viewer;

	public AddPropertyAction(PropertyViewer viewer, int actionStyle) {
		setActionStyle(actionStyle);
		this.viewer = viewer;
	}

	public void run() {
		showInputDialog();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage
				.getString("add.icon"));
	}

	private void showInputDialog() {
		final CompositeMap data = viewer.getInput();
		final Shell shell = new Shell();
		shell.setSize(400, 200);

		Label propertyLabe = new Label(shell, SWT.NONE);
		propertyLabe.setText(LocaleMessage.getString("property.name"));
		propertyLabe.setBounds(20, 20, 50, 30);

		final Text propertyText = new Text(shell, SWT.BORDER);
		propertyText.setBounds(80, 20, 300, 20);

		Label valueLabel = new Label(shell, SWT.NONE);
		valueLabel.setText(LocaleMessage.getString("value"));
		valueLabel.setBounds(20, 50, 50, 30);

		final Text valueText = new Text(shell, SWT.BORDER);
		valueText.setBounds(80, 50, 300, 20);

		final Button ok = new Button(shell, SWT.PUSH);
		ok.setText(LocaleMessage.getString("OK"));
		ok.setBounds(220, 120, 70, 25);

		final Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText(LocaleMessage.getString("Cancel"));
		cancel.setBounds(300, 120, 70, 25);
		SelectionListener listener = getListener(data, shell, propertyText,
				valueText, ok, cancel);
		ok.addSelectionListener(listener);
		cancel.addSelectionListener(listener);
		shell.open();
	}

	private SelectionListener getListener(final CompositeMap data,
			final Shell shell, final Text propertyText, final Text valueText,
			final Button ok, final Button cancel) {
		SelectionListener listener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				Widget w = e.widget;
				if (w == ok) {
					data.put(propertyText.getText(), valueText.getText());
					viewer.refresh(true);
					shell.dispose();
				} else if (w == cancel) {
					shell.dispose();
				}

			}
		};
		return listener;
	}

	public Image getDefaultImage() {
		return ImagesUtils.getImage("add.gif");
	}
}
