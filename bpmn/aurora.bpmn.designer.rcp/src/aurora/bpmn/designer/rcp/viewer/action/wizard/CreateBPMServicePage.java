package aurora.bpmn.designer.rcp.viewer.action.wizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.bpmn.designer.ws.ServiceModel;
import aurora.ide.swt.util.UWizardPage;

public class CreateBPMServicePage extends UWizardPage {

	public static String[] properties = new String[] { ServiceModel.USER_NAME,
			ServiceModel.PSD, ServiceModel.HOST, ServiceModel.SERVICE_NAME };

	protected CreateBPMServicePage(String pageName) {
		super(pageName);
	}

	@Override
	protected String[] getModelPropertyKeys() {
		return properties;
	}

	@Override
	protected String verifyModelProperty(String key, Object val) {
		return "请测试服务是否可用";
	}

	@Override
	protected Composite createPageControl(Composite parent) {

		Composite c = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		c.setLayout(layout);
		this.createInputField(c, "User Name", ServiceModel.USER_NAME);

		Label n = new Label(c, SWT.NONE);
		n.setText("Password");
		Text t = new Text(c, SWT.PASSWORD | SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t.addModifyListener(new TextModifyListener(ServiceModel.PSD, t));

		this.createInputField(c, "Host", ServiceModel.HOST);
		this.createInputField(c, "Service Name", ServiceModel.SERVICE_NAME);

		Button b = new Button(c, SWT.NONE);
		b.setText("Test Service");
		b.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ServiceModel model = new ServiceModel();
				model.setUserName(getModel().getStringPropertyValue(
						ServiceModel.USER_NAME));
				model.setPassword(getModel().getStringPropertyValue(
						ServiceModel.PSD));
				model.setHost(getModel().getStringPropertyValue(
						ServiceModel.HOST));
				new TestJob(model).schedule();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return c;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setPageComplete(false);
	}

	private class TestJob extends UIJob {

		private ServiceModel model;

		public TestJob(ServiceModel model) {
			super("Service Testing");
			this.model = model;
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			BPMService service = new BPMService(model);
			BPMServiceRunner runner = new BPMServiceRunner(service);
			// model
			BPMServiceResponse list = runner.listBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {
				verifyPage(null);
			} else {
				verifyPage("BPM Service 不可用");
			}
			return Status.OK_STATUS;
		}

	}
}
