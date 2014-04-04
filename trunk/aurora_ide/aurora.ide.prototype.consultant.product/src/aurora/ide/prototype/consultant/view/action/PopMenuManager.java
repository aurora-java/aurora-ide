package aurora.ide.prototype.consultant.view.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;

import aurora.ide.prototype.consultant.view.NavigationView;

public class PopMenuManager extends MenuAction {

	private NavigationView viewer;

	private MenuCreateFunctionAction mcfa;
	private MenuCreateModuleAction mcma;
	private MenuCreateProjectAction mcpa;
	private MenuCreateUIPAction mcua;
	
	private UpItemAction uia;
	
	private DownItemAction dia;

	private List<MenuAction> mas = new ArrayList<MenuAction>();

	public PopMenuManager(NavigationView viewer) {
		this.viewer = viewer;
		makeActions();
	}

	public void makeActions() {
		mcfa = new MenuCreateFunctionAction(viewer);
		mcma = new MenuCreateModuleAction(viewer);
		mcpa = new MenuCreateProjectAction(viewer);
		mcua = new MenuCreateUIPAction(viewer);
		mas.add(mcpa);
		mas.add(mcma);
		mas.add(mcfa);
		mas.add(mcua);
		uia = new UpItemAction(viewer);
		dia = new DownItemAction(viewer);
		mas.add(uia);
		mas.add(dia);
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		for (MenuAction ma : mas) {
			ma.fillContextMenu(menu);
		}
	}

}
