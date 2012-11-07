package aurora.ide.create.component.wizard;

import java.util.List;
import java.util.Map;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.control.PrototpyeComposite;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class PrototpyeViewWizardPage extends WizardPage {
	private PrototpyeComposite vsEditor;
	private ViewDiagram viewDiagram;
	private List<CompositeMap> insertAfters;
	private CompositeMap afterMap;

	public PrototpyeViewWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());

		vsEditor = new PrototpyeComposite();
		ViewDiagram viewDiagram = new ViewDiagram();
		setInput(viewDiagram);
		vsEditor.createPartControl(control);
		vsEditor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite afterComposite = new Composite(control, SWT.NONE);
		afterComposite.setLayout(new FillLayout());
		Label l = new Label(afterComposite, SWT.NONE);
		l.setText("插入位置:");
		final Combo list = new Combo(afterComposite, SWT.NONE | SWT.READ_ONLY);
		if (insertAfters != null) {
			for (CompositeMap map : insertAfters) {
				list.add("after " + map.getName());
			}
		}
		list.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = list.getSelectionIndex();
				if (insertAfters != null
						&& insertAfters.size() > selectionIndex) {
					setAfterMap(insertAfters.get(selectionIndex));
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		list.select(0);
		this.setControl(control);
	}

	public void setInput(ViewDiagram viewDiagram) {
		this.setViewDiagram(viewDiagram);
		vsEditor.setInput(viewDiagram);
	}

	public ViewDiagram getViewDiagram() {
		return viewDiagram;
	}

	public void setViewDiagram(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
	}

	public void setInserOfterList(List<CompositeMap> insertAfters) {

		this.insertAfters = insertAfters;
	}

	public CompositeMap getAfterMap() {
		return afterMap;
	}

	public void setAfterMap(CompositeMap afterMap) {
		this.afterMap = afterMap;
	}

}
