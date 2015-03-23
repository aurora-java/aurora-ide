package aurora.bpmn.designer.rcp.viewer.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.ws.BPMNDefineCategory;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.bpmn.designer.ws.Endpoints;
import aurora.bpmn.designer.ws.ServiceModel;

public class LoadBPMServiceAction extends ViewAction {
	private ServiceModel model;
	private TreeViewer viewer;

	public LoadBPMServiceAction(String text, TreeViewer viewer) {
		this.setText(text);
		this.viewer = viewer;
	}

	public void run() {
		LoadJob loadJob = new LoadJob("加载BPM Service Define");
		loadJob.schedule();
	}

	private class LoadJob extends UIJob {

		public LoadJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			BPMService service = new BPMService(model);
			BPMServiceRunner runner = new BPMServiceRunner(service);
			// model
			service.setServiceType(Endpoints.T_LIST_CATEGORY);
			BPMServiceResponse listBPMCategory = runner.listBPMCategory();
			if (listBPMCategory.getStatus() == BPMServiceResponse.fail) {

				String serviceL = Endpoints.getlistBPMCategoryService(
						model.getHost(), "").getUrl();
				MessageDialog.openError(this.getDisplay().getActiveShell(),
						"Error", "服务" + serviceL + "未响应");
				return Status.CANCEL_STATUS;

			}
			service.setServiceType(Endpoints.T_LIST_BPM);
			BPMServiceResponse list = runner.listBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status) {
				model.reload();
				merge(listBPMCategory, list);

				// List<BPMNDefineModel> defines = list.getDefines();
				// model.reload();
				// for (BPMNDefineModel bpmnDefineModel : defines) {
				// model.addDefine(bpmnDefineModel);
				// }
				viewer.refresh(model);
				viewer.expandToLevel(model, 1);
			} else {
				String serviceL = Endpoints.getListService(model.getHost(), "")
						.getUrl();
				MessageDialog.openError(this.getDisplay().getActiveShell(),
						"Error", "服务" + serviceL + "未响应");
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

		private void merge(BPMServiceResponse listBPMCategory,
				BPMServiceResponse listBPM) {
			List<BPMNDefineCategory> categorys = listBPMCategory.getCategorys();
			Map<String, BPMNDefineCategory> mcs = new HashMap<String, BPMNDefineCategory>();
			for (BPMNDefineCategory category : categorys) {
				String id = category.getId();
				mcs.put(id, category);
			}
			for (BPMNDefineCategory category : categorys) {
				String parent_id = category.getParent_id();
				BPMNDefineCategory pc = mcs.get(parent_id);
				if (pc != null) {
					pc.addCategory(category);
				} else {
					model.addCategory(category);
				}
			}

			List<BPMNDefineModel> defines = listBPM.getDefines();
			for (BPMNDefineModel dm : defines) {
				String category_id = dm.getCategory_id();
				BPMNDefineCategory c = mcs.get(category_id);
				if (c != null) {
					c.addDefine(dm);
				} else {
					model.addDefine(dm);
				}
			}
			model.setAllBPMNDefineCategory(mcs);
		}

	}

	@Override
	public void init() {
		TreeItem[] selection = viewer.getTree().getSelection();
		if (selection.length > 0) {
			Object data = selection[0].getData();
			if (data instanceof ServiceModel) {
				this.model = (ServiceModel) data;
			}
		}
		this.setVisible(model instanceof ServiceModel);
	}

}
