package aurora.bpmn.designer.rcp.viewer.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.rcp.viewer.BPMServiceViewer;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.bpmn.designer.ws.Endpoints;

public class SubmitBPMDefineAction extends ViewAction {
	private BPMNDefineModel model;
	private BPMServiceViewer viewer;

	public SubmitBPMDefineAction(String text, BPMServiceViewer viewer) {
		this.setText(text);
		this.viewer = viewer;
	}

	public void run() {
		LoadJob loadJob = new LoadJob("Load BPM Define");
		loadJob.schedule();
	}

	private class LoadJob extends UIJob {

		public LoadJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {

			BPMService service = new BPMService(model.getServiceModel());
			String oaf = model.getApprove_flag();
			model.setApprove_flag("2");
			service.setServiceType(Endpoints.T_SUBMIT_BPM);
			service.setParas(makeParas(model));
			BPMServiceRunner runner = new BPMServiceRunner(service);
			BPMServiceResponse list = runner.saveBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {
				List<BPMNDefineModel> defines = list.getDefines();
				BPMNDefineModel define = defines.get(0);
				if (define != null) {
					model.copy(define);
				}
			} else {
				model.setApprove_flag(oaf);
				String serviceL = Endpoints.getListService(
						model.getServiceModel().getHost(), "").getUrl();
				MessageDialog.openError(this.getDisplay().getActiveShell(),
						"Error", "服务" + serviceL + "未响应");
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

	}

	@Override
	public void init() {
		TreeItem[] selection = viewer.getTreeViewer().getTree().getSelection();
		if (selection.length > 0) {
			Object data = selection[0].getData();
			if (data instanceof BPMNDefineModel) {
				this.model = (BPMNDefineModel) data;
			}
		}
		this.setVisible(model instanceof BPMNDefineModel
				&& "0".equals(model.getApprove_flag())
				&& "n".equalsIgnoreCase(model.getCurrent_version_flag())
				&& "n".equalsIgnoreCase(model.getEnable()));
	}

	private Map<String, String> makeParas(BPMNDefineModel define) {
		Map<String, String> paras = new HashMap<String, String>();
		paras.put("approve_flag", define.getApprove_flag());
		paras.put("define_id", define.getDefine_id());
		return paras;
	}

}
