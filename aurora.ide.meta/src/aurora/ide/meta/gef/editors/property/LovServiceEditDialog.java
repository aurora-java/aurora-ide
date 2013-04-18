package aurora.ide.meta.gef.editors.property;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.meta.gef.editors.wizard.dialog.MappingComposite;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.IDialogEditableObject;
import aurora.plugin.source.gen.screen.model.LovService;
import aurora.plugin.source.gen.screen.model.Mapping;

public class LovServiceEditDialog extends EditWizard {
	private LovService lovService = null;
	private String options;
	private MappingComposite pc;
	private String for_return;
	private String for_display;

	public LovServiceEditDialog() {
		super();
		setWindowTitle("LovService");
	}

	public void addPages() {
		addPage(new InnerPage("LovServiceSelection")); 
	}

	@Override
	public void setDialogEdiableObject(IDialogEditableObject obj) {
		lovService = (LovService) obj;
	}

	@Override
	public boolean performFinish() {
		lovService.setOptions(options);
		lovService.set4Display(for_display);
		lovService.setForReturn(for_return);
		lovService.getMappings().clear();
		List<Mapping> mappings = pc.getMappings();
		for (Mapping mapping : mappings) {
			lovService.addMapping(mapping);
		}
		return true;
	}

	private class InnerPage extends WizardPage {

		protected InnerPage(String pageName) {
			super(pageName);
			setTitle("LovService");
		}

		public void createControl(Composite parent) {
			create_1(parent);
			setControl(parent);
		}
	}
	
	private void create_1(Composite composite_right) {
		composite_right.setLayout(new GridLayout(3, false));
		Label label = new Label(composite_right, SWT.NONE);
		label.setText("Options:");
		final Text text1 = new Text(composite_right, SWT.BORDER);
		text1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		 options = this.lovService.getOptions();
		if (options == null)
			options = ""; 

		text1.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				options = text1.getText();
			}
		});
		text1.setText(options);
		new Label(composite_right, SWT.NONE);

		label = new Label(composite_right, SWT.NONE);
		label.setText("For Return:");
		final Text value = new Text(composite_right, SWT.BORDER);
		value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		 for_return = this.lovService.getForReturn();
		if (for_return == null)
			for_return = ""; 

		value.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				for_return = value.getText();
			}
		});
		value.setText(for_return);
		new Label(composite_right, SWT.NONE);
		
		label = new Label(composite_right, SWT.NONE);
		label.setText("For Display");
		final Text display = new Text(composite_right, SWT.BORDER);
		display.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		 for_display = this.lovService.get4Display();
		if (for_display == null)
			for_display = ""; 
 
		display.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				for_display = display.getText();
			}
		});
		display.setText(for_display);
		new Label(composite_right, SWT.NONE);
		createParaTable(composite_right);
	}

	

	private void createParaTable(Composite composite_right) {
		AuroraComponent comp = (AuroraComponent) lovService.getContextInfo();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 3;
		pc = new MappingComposite(composite_right, SWT.NONE,
				comp);
		pc.setLayoutData(data);
		pc.setMappings(lovService.getMappings());
	}

	
}
