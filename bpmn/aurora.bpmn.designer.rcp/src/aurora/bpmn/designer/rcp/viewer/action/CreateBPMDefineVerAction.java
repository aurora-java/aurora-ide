package aurora.bpmn.designer.rcp.viewer.action;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;

import aurora.bpmn.designer.rcp.viewer.BPMServiceViewer;
import aurora.bpmn.designer.rcp.viewer.action.wizard.CreateBPMDefineWizard;
import aurora.bpmn.designer.ws.BPMNDefineCategory;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.bpmn.designer.ws.BPMService;
import aurora.bpmn.designer.ws.BPMServiceResponse;
import aurora.bpmn.designer.ws.BPMServiceRunner;
import aurora.bpmn.designer.ws.Endpoints;
import aurora.ide.designer.editor.AuroraBpmnEditor;
import aurora.ide.designer.editor.BPMServiceInputStreamEditorInput;

public class CreateBPMDefineVerAction extends ViewAction {
	private BPMNDefineModel model;
	private BPMServiceViewer viewer;

	public CreateBPMDefineVerAction(String text, BPMServiceViewer viewer) {
		this.setText(text);
		this.viewer = viewer;
	}

	public void run() {

		CreateBPMDefineWizard w = new CreateBPMDefineWizard(model
				.getServiceModel().getAllBPMNDefineCategory(), viewer.getSite()
				.getShell());
		w.setNewVer(model);
		int open = w.open();

		if (WizardDialog.OK == open) {
			LoadJob loadJob = new LoadJob("新建BPM Define", w.getModel());
			loadJob.schedule();
		}
	}

	// private class CategoryJob extends UIJob{
	//
	// public CategoryJob(String name) {
	// super(name);
	// }
	//
	// @Override
	// public IStatus runInUIThread(IProgressMonitor monitor) {
	// BPMService service = new BPMService(model);
	// BPMServiceRunner runner = new BPMServiceRunner(service);
	// BPMServiceResponse list = runner.listBPMCategory();
	// List<BPMNDefineCategory> categorys = list.getCategorys();
	// return Status.OK_STATUS;
	// }
	//
	// }
	private class LoadJob extends UIJob {

		private BPMNDefineModel define;

		public LoadJob(String name, BPMNDefineModel model) {
			super(name);
			this.define = model;
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			// BPMNDefineModel define = new BPMNDefineModel();
			// define.setCurrent_version_flag("Y");
			// define.setDescription("XX");
			// define.setName("Hello");
			// define.setProcess_code("007");
			// define.setProcess_version("001");
			define.setDefine(model.getDefines());
			define.setName(model.getName());
			define.setProcess_code(model.getProcess_code());
			define.setCategory_id(model.getCategory_id());

			BPMService service = new BPMService(model.getServiceModel());
			service.setServiceType(Endpoints.T_CREATE_BPM);
			// service.setBPMNDefineModel(define);
			service.setParas(makeParas(define));
			BPMServiceRunner runner = new BPMServiceRunner(service);
			BPMServiceResponse list = runner.saveBPM();
			int status = list.getStatus();
			if (BPMServiceResponse.sucess == status
					&& list.getDefines().size() > 0) {
				List<BPMNDefineModel> defines = list.getDefines();
				BPMNDefineModel repDefine = defines.get(0);
				if (repDefine != null) {

					BPMNDefineCategory bpmnDefineCategory = model
							.getServiceModel().getBPMNDefineCategory(
									repDefine.getCategory_id());
					if (bpmnDefineCategory != null) {
						bpmnDefineCategory.addDefine(repDefine);
						viewer.getTreeViewer().refresh(bpmnDefineCategory);
						viewer.getTreeViewer().expandToLevel(
								bpmnDefineCategory, 1);
					} else {
						model.getServiceModel().addDefine(repDefine);
						viewer.getTreeViewer().refresh(model.getServiceModel());
						viewer.getTreeViewer().expandToLevel(
								model.getServiceModel(), 1);
					}
					try {
						ByteArrayInputStream is = new ByteArrayInputStream(
								repDefine.getDefines().getBytes("UTF-8"));
						IEditorPart openEditor = viewer
								.getSite()
								.getPage()
								.openEditor(
										new BPMServiceInputStreamEditorInput(is),
										AuroraBpmnEditor.ID, true);
						if (openEditor instanceof AuroraBpmnEditor) {
							((AuroraBpmnEditor) openEditor)
									.setDefine(repDefine);
						}
					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

			} else {
				String serviceL = Endpoints.getSaveService(
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
		this.setVisible(model instanceof BPMNDefineModel);

	}

	private Map<String, String> makeParas(BPMNDefineModel define) {

		Map<String, String> paras = new HashMap<String, String>();
		paras.put("approve_flag", define.getApprove_flag());
		paras.put("category_id", define.getCategory_id());

		paras.put("current_version_flag", define.getCurrent_version_flag());

		paras.put("defines", define.getDefines());

		paras.put("description", define.getDescription());

		paras.put("enable", define.getEnable());

		paras.put("name", define.getName());

		paras.put("process_code", define.getProcess_code());

		paras.put("process_version", define.getProcess_version());
		return paras;

		// if (dm == null)
		// return request;
		// addAttribute(request, "define_id", dm.getDefine_id(), null);
		// addAttribute(request, "name", dm.getName(), null);
		// addAttribute(request, "process_code", dm.getProcess_code(), null);
		// addAttribute(request, "process_version", dm.getProcess_version(),
		// null);
		// addAttribute(request, "description", dm.getDescription(), null);
		// addAttribute(request, "current_version_flag",
		// dm.getCurrent_version_flag(), null);
		// addAttribute(request, "defines", dm.getDefines(), null);
		// addAttribute(request, "category_id", dm.getCategory_id(), null);
		// addAttribute(request, "enable", dm.getEnable(), null);
		// addAttribute(request, "approve_flag", dm.getApprove_flag(), null);
	}
}
