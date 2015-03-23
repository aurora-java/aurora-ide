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
import aurora.bpmn.designer.rcp.viewer.IParent;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.bpmn.designer.ws.Endpoints;
import aurora.bpmn.designer.ws.ServiceModel;

public class DeleteBPMDefineAction extends ViewAction {
	private BPMNDefineModel model;
	private BPMServiceViewer viewer;

	public DeleteBPMDefineAction(String text, BPMServiceViewer viewer) {
		this.setText(text);
		this.viewer = viewer;
	}

	public void run() {

		boolean openConfirm = MessageDialog.openConfirm(viewer.getSite()
				.getShell(), "Confirm", "是否确定");
		if (openConfirm) {
			LoadJob loadJob = new LoadJob("删除BPM Define");
			loadJob.schedule();
		}
	}

	private class LoadJob extends UIJob {

		public LoadJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {

			ServiceModel serviceModel = model.getServiceModel();
			BPMService service = new BPMService(serviceModel);
//			service.setBPMNDefineModel(model);
			service.setParas(makeParas(model));
			service.setServiceType(Endpoints.T_DELETE_BPM);
			BPMServiceRunner runner = new BPMServiceRunner(service);
			BPMServiceResponse list = runner.deleteBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {
				List<BPMNDefineModel> defines = list.getDefines();
				BPMNDefineModel repDefine = defines.get(0);
				if (repDefine != null) {
					IParent parent = model.getParent();
					parent.removeChild(model);
					viewer.getTreeViewer().refresh(parent);
					viewer.getTreeViewer().expandToLevel(parent, 1);
				}

			} else {
				String serviceL = Endpoints.getDeleteService(serviceModel.getHost(), "")
						.getUrl();
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
		// if (model instanceof BPMNDefineModel) {
		// // model.getApprove_flag()==0
		// // model.getCurrent_version_flag()==n
		// // model.getEnable()==n
		// }

		this.setVisible(model instanceof BPMNDefineModel
				&& "0".equals(model.getApprove_flag())
				&& "n".equalsIgnoreCase(model.getCurrent_version_flag())
				&& "n".equalsIgnoreCase(model.getEnable()));
	}
	private Map<String, String> makeParas(BPMNDefineModel define) {

		Map<String, String> paras = new HashMap<String, String>();
		paras.put("define_id", define.getDefine_id());
		
		return paras;

	}
}
